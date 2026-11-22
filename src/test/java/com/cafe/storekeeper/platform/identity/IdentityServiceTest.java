package com.cafe.storekeeper.platform.identity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.Instant;
import java.util.Optional;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.dao.DuplicateKeyException;

class IdentityServiceTest {
    AccountRepository accounts = mock(AccountRepository.class);
    MongoTemplate mongo = mock(MongoTemplate.class);
    IdentityMail mail = mock(IdentityMail.class);
    AttemptLimiter limiter = mock(AttemptLimiter.class);
    PasswordEncoder passwords = new BCryptPasswordEncoder(4);
    IdentityService service;
    Account account;
    @BeforeEach void setup() {
        service = new IdentityService(accounts,mongo,passwords,mail,limiter);
        account = new Account("user", "owner@example.test", "Owner", passwords.encode("long-password-123"),true,0,Instant.now(),null);
        when(accounts.findByEmail(anyString())).thenReturn(Optional.empty());
        when(accounts.findById("user")).thenReturn(Optional.of(account));
    }
    @Test void registrationNormalizesHashesAndMailsOpaqueToken() {
        service.register(" Owner "," OWNER@EXAMPLE.TEST ","long-password-123");
        var saved = ArgumentCaptor.forClass(Account.class); verify(accounts).insert(saved.capture());
        assertEquals("owner@example.test",saved.getValue().email()); assertEquals("Owner",saved.getValue().name());
        assertFalse(saved.getValue().verified()); assertTrue(passwords.matches("long-password-123",saved.getValue().passwordHash()));
        var token = ArgumentCaptor.forClass(IdentityToken.class); verify(mongo).insert(token.capture());
        var raw = ArgumentCaptor.forClass(String.class); verify(mail).send(eq(saved.getValue()),eq("verify"),raw.capture());
        assertEquals(IdentityService.hash(raw.getValue()),token.getValue().hash());
        assertTrue(token.getValue().expiresAt().isAfter(Instant.now()));
        assertNotEquals(raw.getValue(),token.getValue().hash());
    }
    @Test void duplicatesAreGenericAndNoPasswordOverwriteOccurs() {
        when(accounts.findByEmail(anyString())).thenReturn(Optional.of(account));
        service.register("Owner", account.email(),"long-password-456"); verify(accounts,never()).insert(any(Account.class));
        when(accounts.findByEmail(anyString())).thenReturn(Optional.empty());
        when(accounts.insert(any(Account.class))).thenThrow(new DuplicateKeyException("race"));
        assertDoesNotThrow(()->service.register("Owner",account.email(),"long-password-456"));
    }
    @Test void authenticationRequiresCorrectPasswordAndVerifiedEmail() {
        assertThrows(IdentityProblem.class,()->service.authenticate("absent@example.test","long-password-123"));
        when(accounts.findByEmail(account.email())).thenReturn(Optional.of(account));
        assertThrows(IdentityProblem.class,()->service.authenticate(account.email(),"incorrect"));
        assertEquals(account,service.authenticate(account.email(),"long-password-123"));
        when(accounts.findByEmail(account.email())).thenReturn(Optional.of(new Account("user",account.email(),"Owner",account.passwordHash(),false,0,Instant.now(),null)));
        assertThrows(IdentityProblem.class,()->service.authenticate(account.email(),"long-password-123"));
    }
    @Test void noMailMvpRegistersWithoutTokensAndStillChecksPasswordsAndReturnOwnership() {
        org.springframework.test.util.ReflectionTestUtils.setField(service, "mailEnabled", false);
        assertFalse(service.mailEnabled());
        service.register("Owner", "owner@example.test", "long-password-123");
        var saved = ArgumentCaptor.forClass(Account.class);
        verify(accounts).insert(saved.capture());
        assertFalse(saved.getValue().verified());
        verifyNoInteractions(mail, mongo);
        when(accounts.findByEmail(account.email())).thenReturn(Optional.of(saved.getValue()));
        assertEquals(saved.getValue(), service.authenticate(account.email(), "long-password-123"));
        assertThrows(IdentityProblem.class, () -> service.authenticate(account.email(), "wrong"));
        assertDoesNotThrow(() -> service.reauthorize(saved.getValue().id(), account.email(), "long-password-123"));
        assertThrows(IdentityProblem.class, () -> service.reauthorize("other", account.email(), "long-password-123"));
        assertEquals(503, assertThrows(IdentityProblem.class, () -> service.requestToken(account.email(), "reset")).status());
        assertEquals(503, assertThrows(IdentityProblem.class, () -> service.requestToken(account.email(), "verify")).status());
        assertEquals(503, assertThrows(IdentityProblem.class, () -> service.consume("token", "reset", "long-password-123")).status());
        verifyNoInteractions(mail, mongo);
        org.springframework.test.util.ReflectionTestUtils.setField(service, "mailEnabled", true);
        assertThrows(IdentityProblem.class, () -> service.authenticate(account.email(), "long-password-123"));
    }
    @Test void sensitiveOperationsRequireTheCurrentUsersPasswordAndPreserveRateLimits() {
        when(accounts.findByEmail(account.email())).thenReturn(Optional.of(account));
        assertDoesNotThrow(()->service.reauthorize("user",account.email(),"long-password-123"));
        assertEquals(403,assertThrows(IdentityProblem.class,()->service.reauthorize("user",account.email(),"wrong")).status());
        assertEquals(403,assertThrows(IdentityProblem.class,()->service.reauthorize("other",account.email(),"long-password-123")).status());
        doThrow(new IdentityProblem(429,"Limited")).when(limiter).check("reauthorize:user",10);
        assertEquals(429,assertThrows(IdentityProblem.class,()->service.reauthorize("user",account.email(),"long-password-123")).status());
        verify(limiter,times(3)).check("reauthorize:user",10);
    }
    @Test void tokenRequestsDoNotRevealMissingOrVerifiedAccountsAndRecordDeliveryFailure() {
        service.requestToken("missing@example.test","reset"); verifyNoInteractions(mail);
        when(accounts.findByEmail(account.email())).thenReturn(Optional.of(account));
        service.requestToken(account.email(),"verify"); verifyNoInteractions(mail);
        doThrow(new IllegalStateException("smtp down")).when(mail).send(any(),any(),any());
        assertDoesNotThrow(()->service.requestToken(account.email(),"reset"));
        verify(mongo).remove(any(Query.class),eq(IdentityToken.class));
        verify(mongo).insert(any(IdentityService.CredentialAudit.class));
    }
    @Test void tokensAreConsumedAtomicallyAndResetRevokesExistingCredentials() {
        when(mongo.findAndRemove(any(Query.class),eq(IdentityToken.class))).thenReturn(null);
        assertThrows(IdentityProblem.class,()->service.consume("bad","verify",null));
        IdentityToken token = new IdentityToken("hash","user","reset",Instant.now().plusSeconds(100),Instant.now(),0);
        when(mongo.findAndRemove(any(Query.class),eq(IdentityToken.class))).thenReturn(token);
        when(mongo.updateFirst(any(Query.class),any(UpdateDefinition.class),eq(Account.class))).thenReturn(UpdateResult.acknowledged(1,1L,null));
        service.consume("raw","verify",null);
        service.consume("raw","reset","new-password-123");
        verify(mongo).remove(any(Query.class),eq(IdentityToken.class));
        when(mongo.updateFirst(any(Query.class),any(UpdateDefinition.class),eq(Account.class))).thenReturn(UpdateResult.acknowledged(0,0L,null));
        assertThrows(IdentityProblem.class,()->service.consume("raw","reset","new-password-123"));
        assertThrows(IdentityProblem.class,()->service.consume("raw","reset","short"));
    }
    @Test void profileAndSessionRevocationNeverExposeHash() {
        assertEquals(account,service.current("user"));
        assertThrows(IdentityProblem.class,()->service.current("missing"));
        service.profile("user"," New name "); service.revoke("user");
        verify(mongo,times(2)).updateFirst(any(Query.class),any(UpdateDefinition.class),eq(Account.class));
        assertFalse(IdentityController.PublicAccount.from(account).toString().contains(account.passwordHash()));
        assertEquals(64,IdentityService.hash("anything").length());
    }
    @Test void rejectsPasswordsBeyondBcryptByteLimit() {
        assertThrows(IdentityProblem.class,()->service.register("Owner",account.email(),"é".repeat(40)));
    }
    @Test void tokensForRemovedAccountsAreRejected() {
        when(mongo.findAndRemove(any(Query.class),eq(IdentityToken.class))).thenReturn(new IdentityToken("hash","removed","verify",Instant.now().plusSeconds(60),Instant.now(),0));
        assertThrows(IdentityProblem.class,()->service.consume("raw","verify",null));
    }
    @Test void unavailableDigestAlgorithmCannotProduceAnInsecureHash() {
        try(var digest=mockStatic(java.security.MessageDigest.class)) {
            digest.when(()->java.security.MessageDigest.getInstance("SHA-256")).thenThrow(new java.security.NoSuchAlgorithmException("unavailable"));
            assertThrows(IllegalStateException.class,()->IdentityService.hash("value"));
        }
    }
}
