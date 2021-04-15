package cloud.chenh.doch.auth;

import cloud.chenh.doch.data.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserManager {
    
    private final ThreadLocal<User> user = new ThreadLocal<>();
    
    public void set(User user) {
        this.user.set(user);
    }
    
    public User get() {
        return user.get();
    }
    
}
