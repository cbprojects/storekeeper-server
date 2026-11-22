package com.cafe.storekeeper.platform.identity;
import java.io.IOException;
import java.time.Instant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

final class GoogleLoginHandler implements AuthenticationSuccessHandler {
    private final GoogleIdentity google;
    private final String publicUrl;
    GoogleLoginHandler(GoogleIdentity google,String publicUrl){this.google=google;this.publicUrl=publicUrl;}
    @Override public void onAuthenticationSuccess(HttpServletRequest request,HttpServletResponse response,Authentication authentication) throws IOException {
        OidcUser oidc=(OidcUser)authentication.getPrincipal();var session=request.getSession();
        Object linkId=session.getAttribute("googleLinkId"),linkVersion=session.getAttribute("googleLinkVersion"),expires=session.getAttribute("googleLinkExpires");
        session.removeAttribute("googleLinkId");session.removeAttribute("googleLinkVersion");session.removeAttribute("googleLinkExpires");
        try {
            Account account=linkId instanceof String id && linkVersion instanceof Long version && expires instanceof Instant until && until.isAfter(Instant.now())
                ? google.link(oidc,id,version):google.resolve(oidc);
            if(account==null)throw new IdentityProblem(401,"Sesión expirada");
            IdentityController.establish(account,request,response);response.sendRedirect(publicUrl+"/#/businesses");
        }catch(RuntimeException failure){session.invalidate();SecurityContextHolder.clearContext();response.sendRedirect(publicUrl+"/#/account/login?error=google");}
    }
}
