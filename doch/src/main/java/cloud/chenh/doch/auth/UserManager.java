package cloud.chenh.doch.auth;

import org.springframework.stereotype.Component;

@Component
public class UserManager {
    
    private final ThreadLocal<String> username = new ThreadLocal<>();
    
    public void set(String user) {
        this.username.set(user);
    }
    
    public String get() {
        return username.get();
    }
    
}
