package com.cafe.storekeeper.platform.identity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
class GoogleLoginHandlerTest {
 Account account=new Account("owner","owner@example.test","Owner",null,true,0,Instant.now(),"subject");
 @Test void loginEstablishesSessionAndClearsLinkState() throws Exception {
  var google=mock(GoogleIdentity.class);var oidc=mock(OidcUser.class);when(google.resolve(oidc)).thenReturn(account);var request=new MockHttpServletRequest();var response=new MockHttpServletResponse();
  try{new GoogleLoginHandler(google,"https://app.test").onAuthenticationSuccess(request,response,UsernamePasswordAuthenticationToken.authenticated(oidc,null,java.util.List.of()));assertEquals("https://app.test/#/businesses",response.getRedirectedUrl());assertEquals(0L,request.getSession().getAttribute("credentialVersion"));verify(google).resolve(oidc);}finally{SecurityContextHolder.clearContext();}
 }
 @Test void validLinkIntentUsesVersionAndIsRemovedAfterSuccess() throws Exception {
  var google=mock(GoogleIdentity.class);var oidc=mock(OidcUser.class);when(google.link(oidc,"owner",0)).thenReturn(account);var request=new MockHttpServletRequest();var session=request.getSession();session.setAttribute("googleLinkId","owner");session.setAttribute("googleLinkVersion",0L);session.setAttribute("googleLinkExpires",Instant.now().plusSeconds(60));
  try{new GoogleLoginHandler(google,"").onAuthenticationSuccess(request,new MockHttpServletResponse(),UsernamePasswordAuthenticationToken.authenticated(oidc,null,java.util.List.of()));verify(google).link(oidc,"owner",0);assertNull(session.getAttribute("googleLinkId"));assertNull(session.getAttribute("googleLinkVersion"));assertNull(session.getAttribute("googleLinkExpires"));}finally{SecurityContextHolder.clearContext();}
 }
 @Test void missingAccountInvalidatesSessionAndRedirectsToLogin() throws Exception {
  var google=mock(GoogleIdentity.class);var oidc=mock(OidcUser.class);var request=new MockHttpServletRequest();var session=(org.springframework.mock.web.MockHttpSession)request.getSession();var response=new MockHttpServletResponse();
  new GoogleLoginHandler(google,"").onAuthenticationSuccess(request,response,UsernamePasswordAuthenticationToken.authenticated(oidc,null,java.util.List.of()));assertTrue(session.isInvalid());assertEquals("/#/account/login?error=google",response.getRedirectedUrl());assertNull(SecurityContextHolder.getContext().getAuthentication());
 }
}
