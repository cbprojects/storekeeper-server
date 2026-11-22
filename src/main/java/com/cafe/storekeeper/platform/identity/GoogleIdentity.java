package com.cafe.storekeeper.platform.identity;

import java.time.Instant;
import java.util.UUID;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class GoogleIdentity extends OidcUserService {
    private final AccountRepository accounts;
    private final org.springframework.data.mongodb.core.MongoTemplate mongo;
    public GoogleIdentity(AccountRepository accounts, org.springframework.data.mongodb.core.MongoTemplate mongo) { this.accounts = accounts; this.mongo = mongo; }
    @Override public OidcUser loadUser(OidcUserRequest request) throws OAuth2AuthenticationException {
        OidcUser user = super.loadUser(request);
        if (!Boolean.TRUE.equals(user.getEmailVerified()) || user.getEmail() == null)
            throw new OAuth2AuthenticationException("unverified_email");
        return user;
    }
    public Account resolve(OidcUser user) {
        if (!Boolean.TRUE.equals(user.getEmailVerified()) || user.getEmail() == null)
            throw new IdentityProblem(401, "Google no confirmó el correo");
        return accounts.findByGoogleSubject(user.getSubject()).orElseGet(() -> {
            String email = IdentityService.normalize(user.getEmail());
            // Never silently link an existing password account by email alone.
            if (accounts.findByEmail(email).isPresent())
                throw new IdentityProblem(409, "Inicia sesión con tu contraseña antes de vincular Google");
            return accounts.insert(new Account(UUID.randomUUID().toString(), email,
                    user.getFullName() == null ? email : user.getFullName(), null, true, 0, Instant.now(), user.getSubject()));
        });
    }
    public Account link(OidcUser user, String accountId, long version) {
        Account account = accounts.findById(accountId).orElseThrow(() -> new IdentityProblem(401,"Sesión expirada"));
        if (!Boolean.TRUE.equals(user.getEmailVerified()) || account.credentialVersion() != version || !account.verified()
                || !account.email().equals(IdentityService.normalize(user.getEmail())))
            throw new IdentityProblem(403,"El correo de Google debe coincidir con tu cuenta verificada");
        var other = accounts.findByGoogleSubject(user.getSubject());
        if (other.isPresent() && !other.get().id().equals(accountId)) throw new IdentityProblem(409,"Identidad vinculada a otra cuenta");
        var query = org.springframework.data.mongodb.core.query.Query.query(org.springframework.data.mongodb.core.query.Criteria.where("_id").is(accountId).and("credentialVersion").is(version));
        return mongo.findAndModify(query, new org.springframework.data.mongodb.core.query.Update().set("googleSubject",user.getSubject()),
                org.springframework.data.mongodb.core.FindAndModifyOptions.options().returnNew(true), Account.class);
    }
}
