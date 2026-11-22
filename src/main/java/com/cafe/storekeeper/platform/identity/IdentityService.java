package com.cafe.storekeeper.platform.identity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.dao.DuplicateKeyException;

@Service
public class IdentityService {
    @org.springframework.beans.factory.annotation.Value("${storekeeper.mail.enabled:true}")
    private boolean mailEnabled = true;
    public boolean mailEnabled() { return mailEnabled; }
    private final AccountRepository accounts;
    private final MongoTemplate mongo;
    private final PasswordEncoder passwords;
    private final IdentityMail mail;
    private final AttemptLimiter limiter;
    private final String dummyHash;

    public IdentityService(AccountRepository accounts, MongoTemplate mongo, PasswordEncoder passwords,
            IdentityMail mail, AttemptLimiter limiter) {
        this.accounts = accounts; this.mongo = mongo; this.passwords = passwords;
        this.mail = mail; this.limiter = limiter;
        this.dummyHash = passwords.encode(UUID.randomUUID().toString());
    }

    public void register(String name, String email, String password) {
        passwordLength(password);
        String normalized = normalize(email);
        limiter.check("register:" + normalized, 4);
        if (accounts.findByEmail(normalized).isPresent()) return;
        Account account = new Account(UUID.randomUUID().toString(), normalized, name.strip(),
                passwords.encode(password), false, 0, Instant.now(), null);
        try { accounts.insert(account); }
        catch (DuplicateKeyException duplicate) { return; }
        if (mailEnabled) deliver(account, "verify");
    }

    public Account authenticate(String email, String password) {
        String normalized = normalize(email);
        limiter.check("login:" + normalized, 10);
        return credentials(normalized, password);
    }

    private Account credentials(String normalized, String password) {
        Account account = accounts.findByEmail(normalized).orElse(null);
        boolean matches = passwords.matches(password, account == null || account.passwordHash() == null ? dummyHash : account.passwordHash());
        if (!matches || account == null || (mailEnabled && !account.verified())) throw new IdentityProblem(401,
                "Credenciales inválidas o correo sin verificar");
        return account;
    }

    public void reauthorize(String accountId, String email, String password) {
        limiter.check("reauthorize:" + accountId, 10);
        Account approved;
        try { approved = credentials(normalize(email), password); }
        catch (IdentityProblem failure) {
            throw new IdentityProblem(403, "No fue posible autorizar la devolución; revisa tus credenciales");
        }
        if (!approved.id().equals(accountId))
            throw new IdentityProblem(403, "No fue posible autorizar la devolución; revisa tus credenciales");
    }

    public void requestToken(String email, String purpose) {
        requireMail();
        String normalized = normalize(email);
        limiter.check(purpose + ":" + normalized, 3);
        accounts.findByEmail(normalized).ifPresent(account -> {
            if (!purpose.equals("verify") || !account.verified()) deliver(account, purpose);
        });
    }

    private void deliver(Account account, String purpose) {
        try { issue(account, purpose); }
        catch (IdentityProblem failure) {
            // Public responses remain identical for existing and absent accounts.
            audit(account.id(), "MAIL_DELIVERY_FAILED");
        }
    }

    private void issue(Account account, String purpose) {
        byte[] bytes = new byte[32]; new SecureRandom().nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        IdentityToken token = new IdentityToken(hash(raw), account.id(), purpose,
                Instant.now().plus(purpose.equals("verify") ? 24 : 1, ChronoUnit.HOURS), Instant.now(), account.credentialVersion());
        mongo.insert(token);
        try { mail.send(account, purpose, raw); }
        catch (RuntimeException failure) {
            mongo.remove(Query.query(Criteria.where("_id").is(token.hash())), IdentityToken.class);
            throw new IdentityProblem(503, "No fue posible enviar el correo; solicita un nuevo enlace");
        }
    }

    public void consume(String raw, String purpose, String password) {
        requireMail();
        if (purpose.equals("reset")) passwordLength(password);
        IdentityToken token = mongo.findAndRemove(Query.query(Criteria.where("_id").is(hash(raw))
                .and("purpose").is(purpose).and("expiresAt").gt(Instant.now())), IdentityToken.class);
        if (token == null) throw new IdentityProblem(400, "Enlace inválido, utilizado o vencido");
        Account account = accounts.findById(token.accountId()).orElseThrow(() -> new IdentityProblem(400, "Enlace inválido"));
        if (purpose.equals("verify")) {
            mongo.updateFirst(Query.query(Criteria.where("_id").is(account.id())), new Update().set("verified", true), Account.class);
        } else {
            var result = mongo.updateFirst(Query.query(Criteria.where("_id").is(account.id()).and("credentialVersion").is(token.credentialVersion())),
                    new Update().set("passwordHash", passwords.encode(password)).inc("credentialVersion", 1), Account.class);
            if (result.getModifiedCount() != 1) throw new IdentityProblem(400,"Enlace inválido, utilizado o vencido");
            mongo.remove(Query.query(Criteria.where("accountId").is(account.id()).and("purpose").is("reset")), IdentityToken.class);
        }
        audit(account.id(), purpose.equals("verify") ? "EMAIL_VERIFIED" : "PASSWORD_CHANGED");
    }

    public Account current(String id) {
        return accounts.findById(id).orElseThrow(() -> new IdentityProblem(401, "Sesión expirada"));
    }

    public void profile(String id, String name) {
        mongo.updateFirst(Query.query(Criteria.where("_id").is(id)), new Update().set("name", name.strip()), Account.class);
    }

    public void revoke(String id) {
        mongo.updateFirst(Query.query(Criteria.where("_id").is(id)), new Update().inc("credentialVersion", 1), Account.class);
        audit(id, "SESSIONS_REVOKED");
    }

    private void audit(String id, String event) {
        mongo.insert(new CredentialAudit(UUID.randomUUID().toString(), id, event, Instant.now()));
    }
    @org.springframework.data.mongodb.core.mapping.Document("platform_credential_audit")
    public record CredentialAudit(@org.springframework.data.annotation.Id String id, String accountId, String event, Instant at) {}

    private void passwordLength(String password) {
        if (password == null || password.length() < 12 || password.getBytes(StandardCharsets.UTF_8).length > 72)
            throw new IdentityProblem(400, "La contraseña requiere al menos 12 caracteres y hasta 72 bytes");
    }

    private void requireMail() {
        if (!mailEnabled) throw new IdentityProblem(503, "El envío de correo está deshabilitado en este MVP");
    }

    public static String normalize(String email) { return email.strip().toLowerCase(Locale.ROOT); }
    public static String hash(String value) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); }
        catch (java.security.NoSuchAlgorithmException impossible) { throw new IllegalStateException(impossible); }
    }
}
