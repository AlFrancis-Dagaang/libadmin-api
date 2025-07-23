package com.app.filter;

import com.app.util.JsonUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

// Apply filter to all /v1/lms/* routes
@WebFilter("/v1/lms/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI();

        // Allow unauthenticated access to login (or other public endpoints)
        if (path.startsWith("/v1/lms/auth")) {
            chain.doFilter(request, response);
            return;
        }

        // Check for session and admin attribute
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("admin") != null) {
            chain.doFilter(request, response); // Continue
        } else {
            JsonUtil.writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }
}