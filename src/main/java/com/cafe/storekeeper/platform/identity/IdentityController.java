package com.cafe.storekeeper.platform.identity;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class IdentityController {
    private final IdentityService identity;
    private final AttemptLimiter limiter;
    private final org.springframework.beans.factory.ObjectProvider<org.springframework.security.oauth2.client.registration.ClientRegistrationRepository> clients;
    public IdentityController(IdentityService identity, AttemptLimiter limiter,
            org.springframework.beans.factory.ObjectProvider<org.springframework.security.oauth2.client.registration.ClientRegistrationRepository> clients) {
        this.identity = identity; this.limiter = limiter; this.clients = clients;
    }
    @GetMapping("/options") public Map<String,Boolean> options() {
        return Map.of("google", clients.getIfAvailable() != null, "emailEnabled", identity.mailEnabled());
    }
    public record PublicAccount(String id, String email, String name, boolean verified) {
        static PublicAccount from(Account account) { return new PublicAccount(account.id(), account.email(), account.name(), account.verified()); }
    }
    public record Session(PublicAccount user, String csrfToken) {}
    public record Registration(@NotBlank @Size(max=120) String name, @NotBlank @Email @Size(max=254) String email,
            @NotBlank @Size(min=12,max=72) String password) {}
    public record Login(@NotBlank @Email @Size(max=254) String email, @NotBlank @Size(max=72) String password) {}
    public record EmailRequest(@NotBlank @Email @Size(max=254) String email) {}
    public record TokenRequest(@NotBlank @Size(max=128) String token) {}
    public record ResetRequest(@NotBlank @Size(max=128) String token, @NotBlank @Size(min=12,max=72) String password) {}
    public record ProfileRequest(@NotBlank @Size(max=120) String name) {}

    @GetMapping("/session")
    public Session session(Authentication authentication, CsrfToken csrf) {
        return new Session(authentication == null ? null : PublicAccount.from(identity.current(authentication.getName())), csrf.getToken());
    }
    @PostMapping("/register")
    public Map<String,String> register(@Valid @RequestBody Registration data, HttpServletRequest request) {
        limiter.check("register-ip:" + request.getRemoteAddr(), 20);
        identity.register(data.name(), data.email(), data.password());
        return identity.mailEnabled() ? accepted() : Map.of("message", "Si la cuenta es nueva, ya puedes iniciar sesión con tu correo y contraseña. Este MVP no requiere confirmación por correo.");
    }
    @PostMapping("/login")
    public PublicAccount login(@Valid @RequestBody Login data, HttpServletRequest request, HttpServletResponse response) {
        limiter.check("login-ip:" + request.getRemoteAddr(), 40);
        Account account = identity.authenticate(data.email(), data.password());
        establish(account, request, response);
        return PublicAccount.from(account);
    }
    static void establish(Account account, HttpServletRequest request, HttpServletResponse response) {
        if (request.getSession(false) != null) request.changeSessionId();
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(UsernamePasswordAuthenticationToken.authenticated(account.id(), null, AuthorityUtils.createAuthorityList("ROLE_OWNER")));
        SecurityContextHolder.setContext(context);
        request.getSession().setAttribute("credentialVersion", account.credentialVersion());
        new HttpSessionSecurityContextRepository().saveContext(context, request, response);
        new org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository().saveToken(null, request, response);
    }
    @PostMapping("/verify")
    public Map<String,String> verify(@Valid @RequestBody TokenRequest data) {
        identity.consume(data.token(), "verify", null); return Map.of("message", "Correo confirmado; ya puedes iniciar sesión");
    }
    @PostMapping("/resend")
    public Map<String,String> resend(@Valid @RequestBody EmailRequest data, HttpServletRequest request) {
        limiter.check("mail-ip:" + request.getRemoteAddr(), 15);
        identity.requestToken(data.email(), "verify"); return accepted();
    }
    @PostMapping("/forgot")
    public Map<String,String> forgot(@Valid @RequestBody EmailRequest data, HttpServletRequest request) {
        limiter.check("mail-ip:" + request.getRemoteAddr(), 15);
        identity.requestToken(data.email(), "reset"); return accepted();
    }
    @PostMapping("/reset")
    public Map<String,String> reset(@Valid @RequestBody ResetRequest data, HttpServletRequest request) {
        limiter.check("reset-ip:" + request.getRemoteAddr(), 20);
        identity.consume(data.token(), "reset", data.password()); return Map.of("message", "Contraseña actualizada; inicia sesión nuevamente");
    }
    @PutMapping("/profile")
    public PublicAccount profile(@Valid @RequestBody ProfileRequest data, Authentication auth) {
        identity.profile(auth.getName(), data.name()); return PublicAccount.from(identity.current(auth.getName()));
    }
    @PostMapping("/revoke-sessions")
    public void revoke(Authentication auth) { identity.revoke(auth.getName()); }

    @PostMapping("/google-link")
    public Map<String,String> link(Authentication auth, HttpServletRequest request) {
        Account account = identity.current(auth.getName());
        request.getSession().setAttribute("googleLinkId", account.id());
        request.getSession().setAttribute("googleLinkVersion", account.credentialVersion());
        request.getSession().setAttribute("googleLinkExpires", java.time.Instant.now().plusSeconds(300));
        return Map.of("url", request.getContextPath() + "/oauth2/authorization/google");
    }

    private Map<String,String> accepted() { return Map.of("message", "Si corresponde, recibirás un enlace por correo"); }
}
