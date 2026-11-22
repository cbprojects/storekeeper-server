package com.cafe.storekeeper.platform.identity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

class IdentityAdaptersTest {
    Account account=new Account("owner","owner@example.test","Owner","hash",true,0,Instant.now(),"subject");
    @Test void oidcClaimsMustContainVerifiedEmailBeforeLogin(){
        var google=new GoogleIdentity(mock(AccountRepository.class),mock(MongoTemplate.class));google.setRetrieveUserInfo(request->false);
        var registration=org.springframework.security.oauth2.client.registration.ClientRegistration.withRegistrationId("google").clientId("client").clientSecret("secret").authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE).redirectUri("https://app.test/callback").scope("openid").authorizationUri("https://id.test/authorize").tokenUri("https://id.test/token").jwkSetUri("https://id.test/keys").build();
        var access=new org.springframework.security.oauth2.core.OAuth2AccessToken(org.springframework.security.oauth2.core.OAuth2AccessToken.TokenType.BEARER,"access",Instant.now(),Instant.now().plusSeconds(60));
        for(boolean verified:new boolean[]{true,false}){
            var token=new org.springframework.security.oauth2.core.oidc.OidcIdToken("token",Instant.now(),Instant.now().plusSeconds(60),java.util.Map.of("sub","subject","email","owner@example.test","email_verified",verified));
            var request=new org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest(registration,access,token);
            if(verified)assertEquals("owner@example.test",google.loadUser(request).getEmail());else assertThrows(org.springframework.security.oauth2.core.OAuth2AuthenticationException.class,()->google.loadUser(request));
        }
    }
    @Test @SuppressWarnings("unchecked") void mailUsesActualTransportAndDoesNotExposeTokenElsewhere() throws Exception {
        ObjectProvider<JavaMailSender> provider=mock(ObjectProvider.class);JavaMailSender sender=mock(JavaMailSender.class);
        IdentityMail mail=new IdentityMail(provider,"no-reply@example.test","https://app.example.test");
        assertThrows(IdentityProblem.class,()->mail.send(account,"verify","token"));
        when(provider.getIfAvailable()).thenReturn(sender);
        when(sender.createMimeMessage()).thenAnswer(i -> new MimeMessage((Session) null));
        mail.send(account,"verify","token");mail.send(account,"reset","token");
        var messages=org.mockito.ArgumentCaptor.forClass(MimeMessage.class);verify(sender,times(2)).send(messages.capture());
        for (int index=0; index<2; index++) {
            var message=messages.getAllValues().get(index);
            assertEquals(account.email(), ((jakarta.mail.internet.InternetAddress) message.getAllRecipients()[0]).getAddress());
            assertEquals("auto-generated",message.getHeader("Auto-Submitted",null));
            assertEquals("es",message.getHeader("Content-Language",null));
            assertNotNull(message.getMessageID()); assertNotNull(message.getSentDate());
            assertTrue(message.isMimeType("multipart/alternative"));
            Multipart body=(Multipart)message.getContent();
            assertEquals(2,body.getCount());
            assertTrue(body.getBodyPart(0).isMimeType("text/plain"));
            assertTrue(body.getBodyPart(1).isMimeType("multipart/related"));
            Multipart rich=(Multipart)body.getBodyPart(1).getContent();
            assertTrue(rich.getBodyPart(0).isMimeType("text/html"));
            assertEquals("<storekeeper-logo>",rich.getBodyPart(1).getHeader("Content-ID")[0]);
            assertTrue(rich.getBodyPart(1).getInputStream().readAllBytes().length>100);
            String route=index==0 ? "verify" : "reset";
            for(int part=0;part<2;part++) {
                String content=(String)(part==0 ? body.getBodyPart(0).getContent() : rich.getBodyPart(0).getContent());
                assertTrue(content.contains("/#/account/"+route+"?token=token"));
                assertTrue(content.contains(index==0 ? "24 horas" : "1 hora"));
            }
        }
        assertNotEquals(messages.getAllValues().get(0).getMessageID(), messages.getValue().getMessageID());
        var unsafe=new Account("id","safe@example.test","<img src=x>","hash",false,0,Instant.now(),null);
        mail.send(unsafe,"verify","a&b");
        verify(sender,times(3)).send(messages.capture());
        String html=(String)((Multipart)((Multipart)messages.getValue().getContent()).getBodyPart(1).getContent()).getBodyPart(0).getContent();
        assertFalse(html.contains("<img src=x>")); assertTrue(html.contains("&lt;img src=x&gt;"));
        assertTrue(html.contains("token=a%26b"));
        assertThrows(IllegalArgumentException.class,()->mail.send(account,"unknown","token"));
        var invalid=new Account("id","invalid@@example.test","Test","hash",false,0,Instant.now(),null);
        assertThrows(org.springframework.mail.MailPreparationException.class,()->mail.send(invalid,"verify","token"));
    }
    @Test void rateLimitCounterIsSharedAtomicAndBounded(){
        MongoTemplate mongo=mock(MongoTemplate.class);AttemptLimiter limiter=new AttemptLimiter(mongo);
        assertThrows(IdentityProblem.class,()->limiter.check("key",2));
        when(mongo.findAndModify(any(Query.class),any(UpdateDefinition.class),any(FindAndModifyOptions.class),eq(AttemptLimiter.Counter.class)))
            .thenReturn(new AttemptLimiter.Counter("hash",2,Instant.now()));
        assertDoesNotThrow(()->limiter.check("key",2));
        when(mongo.findAndModify(any(Query.class),any(UpdateDefinition.class),any(FindAndModifyOptions.class),eq(AttemptLimiter.Counter.class)))
            .thenReturn(new AttemptLimiter.Counter("hash",3,Instant.now()));
        assertThrows(IdentityProblem.class,()->limiter.check("key",2));
    }
    @Test void googleNeverSilentlyLinksAnEmailAccount(){
        AccountRepository accounts=mock(AccountRepository.class);MongoTemplate mongo=mock(MongoTemplate.class);
        GoogleIdentity google=new GoogleIdentity(accounts,mongo);OidcUser user=mock(OidcUser.class);
        when(user.getSubject()).thenReturn("subject");when(user.getEmail()).thenReturn(account.email());
        when(user.getEmailVerified()).thenReturn(false);assertThrows(IdentityProblem.class,()->google.resolve(user));
        when(user.getEmailVerified()).thenReturn(true);when(accounts.findByGoogleSubject("subject")).thenReturn(Optional.of(account));
        assertEquals(account,google.resolve(user));
        when(accounts.findByGoogleSubject("subject")).thenReturn(Optional.empty());when(accounts.findByEmail(account.email())).thenReturn(Optional.of(account));
        assertThrows(IdentityProblem.class,()->google.resolve(user));
        when(accounts.findByEmail(account.email())).thenReturn(Optional.empty());when(accounts.insert(any(Account.class))).thenAnswer(i->i.getArgument(0));
        assertTrue(google.resolve(user).verified());
        when(accounts.findById("owner")).thenReturn(Optional.of(account));
        assertThrows(IdentityProblem.class,()->google.link(user,"owner",2));
        when(mongo.findAndModify(any(Query.class),any(UpdateDefinition.class),any(FindAndModifyOptions.class),eq(Account.class))).thenReturn(account);
        assertEquals(account,google.link(user,"owner",0));
        when(accounts.findByGoogleSubject("subject")).thenReturn(Optional.of(new Account("other",account.email(),"Other",null,true,0,Instant.now(),"subject")));
        assertThrows(IdentityProblem.class,()->google.link(user,"owner",0));
    }
}
