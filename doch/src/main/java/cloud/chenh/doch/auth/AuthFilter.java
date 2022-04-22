package cloud.chenh.doch.auth;

import cloud.chenh.doch.exception.LoginException;
import cloud.chenh.doch.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Order(1)
@Component
public class AuthFilter implements Filter {

    public static final String TOKEN = "Token";
    public static final String MESSAGE = "Message";

    @Value("${doch.auth.patterns}")
    private String[] authPatterns;

    @Autowired
    private UserManager userManager;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        if (authPatterns == null || authPatterns.length <= 0) {
            succeed(servletRequest, servletResponse, chain);
            return;
        }

        // 判断是否需要拦截
        var request = (HttpServletRequest) servletRequest;
        var servletPath = request.getServletPath();
        var matcher = new AntPathMatcher();
        boolean inList = false;
        for (var pattern : authPatterns) {
            if (matcher.match(pattern, servletPath)) {
                inList = true;
                break;
            }
        }

        if (!inList) {
            succeed(servletRequest, servletResponse, chain);
            return;
        }

        var token = request.getHeader(TOKEN);
        try {
            JwtUtils.decode(token);
            userManager.set(JwtUtils.decode(token));
            succeed(servletRequest, servletResponse, chain);
        } catch (LoginException e) {
            fail(servletRequest, servletResponse, e);
        }
    }

    private void succeed(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(servletRequest, servletResponse);
    }

    private void fail(ServletRequest servletRequest, ServletResponse servletResponse, Exception e) throws IOException, ServletException {
        servletRequest.setAttribute(MESSAGE, e.getMessage());
        servletRequest.getRequestDispatcher("/user/require-login").forward(servletRequest, servletResponse);
    }

}
