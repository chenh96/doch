package cloud.chenh.doch.auth;

import cloud.chenh.doch.data.entity.User;
import cloud.chenh.doch.data.service.UserService;
import cloud.chenh.doch.exception.LoginException;
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
    private UserService userService;
    
    @Autowired
    private UserManager userManager;
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        if (authPatterns == null || authPatterns.length <= 0) {
            succeed(servletRequest, servletResponse, chain);
            return;
        }
        
        // 判断是否需要拦截
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String servletPath = request.getServletPath();
        AntPathMatcher matcher = new AntPathMatcher();
        boolean inList = false;
        for (String pattern : authPatterns) {
            if (matcher.match(pattern, servletPath)) {
                inList = true;
                break;
            }
        }
        
        if (!inList) {
            succeed(servletRequest, servletResponse, chain);
            return;
        }
        
        String token = request.getHeader(TOKEN);
        try {
            User user = userService.login(token);
            userManager.set(user);
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
