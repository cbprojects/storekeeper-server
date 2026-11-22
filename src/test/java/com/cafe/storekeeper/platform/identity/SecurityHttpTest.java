package com.cafe.storekeeper.platform.identity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.beans.factory.annotation.Autowired;

@SpringJUnitWebConfig(SecurityHttpTest.Config.class)
class SecurityHttpTest {
    @Configuration @EnableWebMvc @EnableWebSecurity
    @Import({PlatformSecurity.class,IdentityController.class,PlatformErrors.class})
    static class Config {
        @Bean AccountRepository accounts(){return mock(AccountRepository.class);}
        @Bean IdentityService identity(){return mock(IdentityService.class);}
        @Bean AttemptLimiter limiter(){return mock(AttemptLimiter.class);}
        @Bean GoogleIdentity google(){return mock(GoogleIdentity.class);}
        @Bean org.springframework.security.oauth2.client.registration.ClientRegistrationRepository clients(){
            return new org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository(org.springframework.security.oauth2.client.registration.ClientRegistration.withRegistrationId("google").clientId("client").clientSecret("secret").authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE).redirectUri("{baseUrl}/login/oauth2/code/{registrationId}").scope("openid","email").authorizationUri("https://id.test/authorize").tokenUri("https://id.test/token").jwkSetUri("https://id.test/keys").userInfoUri("https://id.test/userinfo").userNameAttributeName("sub").clientName("Google").build());
        }
        }
    @Autowired WebApplicationContext context;
    @Autowired AccountRepository accounts;
    @Autowired IdentityService identity;
    MockMvc mvc;
    Account account=new Account("owner","owner@example.test","Owner","secret-hash",true,0,Instant.now(),null);
    @BeforeEach void setup(){
        reset(accounts,identity);
        when(accounts.findById("owner")).thenReturn(Optional.of(account));
        when(identity.current("owner")).thenReturn(account);
        when(identity.authenticate(anyString(),anyString())).thenReturn(account);
        mvc=MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }
    @Test void anonymousSessionAndCsrfAreRequired() throws Exception {
        mvc.perform(get("/api/v1/auth/session")).andExpect(status().isOk()).andExpect(jsonPath("$.user").doesNotExist()).andExpect(jsonPath("$.csrfToken").isString());
        mvc.perform(get("/api/v1/businesses")).andExpect(status().isUnauthorized());
        mvc.perform(get("/client/v1/")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/v1/auth/register").contentType("application/json").content("{}" )).andExpect(status().isForbidden());
        mvc.perform(post("/api/v1/auth/register").with(csrf()).contentType("application/json").content("{}" )).andExpect(status().isBadRequest());
        mvc.perform(post("/api/v1/auth/register").with(csrf()).contentType("application/json").content("{\"name\":\"Owner\",\"email\":\"owner@example.test\",\"password\":\"long-password-123\"}" )).andExpect(status().isOk());
    }
    @Test void loginRotatesSessionAndLogoutInvalidatesIt() throws Exception {
        MockHttpSession session=new MockHttpSession(); String old=session.getId();
        mvc.perform(post("/api/v1/auth/login").session(session).with(csrf()).contentType("application/json").content("{\"email\":\"owner@example.test\",\"password\":\"long-password-123\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.id").value("owner")).andExpect(jsonPath("$.passwordHash").doesNotExist());
        assertNotEquals(old,session.getId());assertEquals(0L,session.getAttribute("credentialVersion"));
        mvc.perform(get("/api/v1/auth/session").session(session)).andExpect(status().isOk()).andExpect(jsonPath("$.user.id").value("owner"));
        mvc.perform(get("/product/v1/").session(session)).andExpect(status().isForbidden());
        mvc.perform(put("/api/v1/auth/profile").session(session).with(csrf()).contentType("application/json").content("{\"name\":\"Updated\"}")).andExpect(status().isOk());
        mvc.perform(post("/api/v1/auth/google-link").session(session).with(csrf())).andExpect(status().isOk()).andExpect(jsonPath("$.url").value("/oauth2/authorization/google"));
        mvc.perform(post("/api/v1/auth/revoke-sessions").session(session).with(csrf())).andExpect(status().isOk());
        mvc.perform(post("/api/v1/auth/logout").session(session).with(csrf())).andExpect(status().isNoContent());
        assertTrue(session.isInvalid());
    }
    @Test void recoveryAndVerificationContractsAndErrors() throws Exception {
        for(String path:new String[]{"forgot","resend"})
            mvc.perform(post("/api/v1/auth/"+path).with(csrf()).contentType("application/json").content("{\"email\":\"absent@example.test\"}")).andExpect(status().isOk()).andExpect(jsonPath("$.message").isString());
        mvc.perform(post("/api/v1/auth/verify").with(csrf()).contentType("application/json").content("{\"token\":\"token\"}")).andExpect(status().isOk());
        mvc.perform(post("/api/v1/auth/reset").with(csrf()).contentType("application/json").content("{\"token\":\"token\",\"password\":\"long-password-123\"}")).andExpect(status().isOk());
        doThrow(new IdentityProblem(400,"Enlace inválido")).when(identity).consume(anyString(),eq("verify"),isNull());
        mvc.perform(post("/api/v1/auth/verify").with(csrf()).contentType("application/json").content("{\"token\":\"bad\"}")).andExpect(status().isBadRequest()).andExpect(jsonPath("$.detail").value("Enlace inválido"));
        var errors=new PlatformErrors();assertEquals(400,errors.invalid(new IllegalArgumentException()).getStatusCode().value());
        assertEquals(409,errors.conflict(new org.springframework.dao.DuplicateKeyException("private")).getStatusCode().value());
        assertFalse(errors.unexpected(new Exception("private secret")).getBody().getDetail().contains("secret"));
        var mongoError=new com.mongodb.MongoException("write conflict");mongoError.addLabel(com.mongodb.MongoException.TRANSIENT_TRANSACTION_ERROR_LABEL);
        assertEquals(409,errors.unexpected(new RuntimeException(mongoError)).getStatusCode().value());
        var unknownCommit=new com.mongodb.MongoException("commit");unknownCommit.addLabel(com.mongodb.MongoException.UNKNOWN_TRANSACTION_COMMIT_RESULT_LABEL);assertEquals(409,errors.unexpected(new RuntimeException(unknownCommit)).getStatusCode().value());
    }
    @Test void credentialVersionRejectsRevokedAndRemovedAccounts() throws Exception {
        MockHttpSession session=new MockHttpSession();session.setAttribute("credentialVersion",1L);
        mvc.perform(get("/api/v1/auth/session").with(user("owner")).session(session)).andExpect(status().isUnauthorized());assertTrue(session.isInvalid());
        when(accounts.findById("owner")).thenReturn(Optional.empty());
        mvc.perform(get("/api/v1/auth/session").with(user("owner"))).andExpect(status().isUnauthorized());
    }
    @Test void identityOptionsAndPasswordOnlyUserLookup() throws Exception {
        mvc.perform(get("/api/v1/auth/options")).andExpect(status().isOk());
        mvc.perform(get("/oauth2/authorization/google")).andExpect(status().is3xxRedirection());
        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,()->new PlatformSecurity().noGeneratedUsers().loadUserByUsername("owner"));
    }
    @Test void corsRejectsUntrustedOrigin() throws Exception {
        mvc.perform(options("/api/v1/auth/session").header("Origin","https://evil.example").header("Access-Control-Request-Method","GET")).andExpect(status().isForbidden());
        mvc.perform(options("/api/v1/auth/session").header("Origin","http://localhost:4200").header("Access-Control-Request-Method","GET")).andExpect(status().isOk());
    }
}
