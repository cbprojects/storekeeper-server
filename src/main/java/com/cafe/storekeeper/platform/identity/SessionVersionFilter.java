package com.cafe.storekeeper.platform.identity;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class SessionVersionFilter extends OncePerRequestFilter {
    private final AccountRepository accounts;
    private final boolean mailEnabled;
    public SessionVersionFilter(AccountRepository accounts, boolean mailEnabled) {
        this.accounts = accounts; this.mailEnabled = mailEnabled;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            Account account = accounts.findById(auth.getName()).orElse(null);
            Object version = req.getSession(false) == null ? null : req.getSession(false).getAttribute("credentialVersion");
            if (account == null || (mailEnabled && !account.verified()) || !Long.valueOf(account.credentialVersion()).equals(version)) {
                if (req.getSession(false) != null) req.getSession(false).invalidate();
                SecurityContextHolder.clearContext();
                res.sendError(401); return;
            }
        }
        chain.doFilter(req, res);
    }
}
