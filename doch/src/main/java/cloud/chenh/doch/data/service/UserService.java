package cloud.chenh.doch.data.service;

import cloud.chenh.doch.auth.UserManager;
import cloud.chenh.doch.data.base.BaseService;
import cloud.chenh.doch.data.entity.User;
import cloud.chenh.doch.data.repo.UserRepository;
import cloud.chenh.doch.exception.InvalidDataException;
import cloud.chenh.doch.exception.LoginException;
import cloud.chenh.doch.exception.RegisterException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class UserService extends BaseService<User> {
    
    private final Map<String, User> loginCache = new ConcurrentHashMap<>();
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private UserRepository userRepository;
    
    private static String genToken(User user) {
        return String.format("%d@%s@%s",
                new Date().getTime() + 30 * 24 * 60 * 60 * 1000L,
                user.getUsername(),
                BCrypt.gensalt()
        );
    }
    
    private static Date getTokenExpire(String token) {
        try {
            return new Date(Long.parseLong(token.split("@")[0]));
        } catch (Exception e) {
            return new Date(0);
        }
    }
    
    @Override
    public UserRepository getRepository() {
        return userRepository;
    }
    
    public synchronized void register(String username, String password) throws RegisterException {
        if (StringUtils.isBlank(username) || username.length() < 6 || username.length() > 32) {
            throw new RegisterException("请输入6到32位用户名");
        }
        
        if (StringUtils.isBlank(password) || password.length() < 6 || password.length() > 32) {
            throw new RegisterException("请输入6到32位密码");
        }
        
        if (getRepository().existsByUsername(username)) {
            throw new RegisterException("用户名已被使用");
        }
        
        String salt = BCrypt.gensalt();
        password = BCrypt.hashpw(password, salt);
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setSalt(salt);
        user.setOperateAt(new Date());
        save(user);
    }
    
    public User login(String username, String password) throws LoginException {
        if (StringUtils.isBlank(username) || username.length() < 6 || username.length() > 32) {
            throw new LoginException("不正确的用户名或密码");
        }
        
        if (StringUtils.isBlank(password) || password.length() < 6 || password.length() > 32) {
            throw new LoginException("不正确的用户名或密码");
        }
        
        User user = getRepository().findFirstByUsername(username);
        if (user == null) {
            throw new LoginException("不正确的用户名或密码");
        }
        
        String inputPassword = BCrypt.hashpw(password, user.getSalt());
        if (!user.getPassword().equals(inputPassword)) {
            throw new LoginException("不正确的用户名或密码");
        }
        
        String token = genToken(user);
        user.setToken(token);
        user.setOperateAt(new Date());
        return save(user);
    }
    
    public User login(String token) throws LoginException {
        if (StringUtils.isBlank(token)) {
            throw new LoginException("请先登录");
        }
        
        if (getTokenExpire(token).before(new Date())) {
            throw new LoginException("用户登录已过期");
        }
        
        User user = findForLoginByToken(token);
        
        if (user == null) {
            throw new LoginException("用户登录已过期");
        }
        
        return user;
    }
    
    private User findForLoginByToken(String token) {
        User user = loginCache.get(token);
        if (user == null) {
            user = getRepository().findFirstByToken(token);
            
            if (user != null) {
                user.setOperateAt(new Date());
                user = save(user);
                loginCache.put(token, user);
                
                new Thread(() -> {
                    try {
                        TimeUnit.MINUTES.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        loginCache.remove(token);
                    }
                }).start();
            }
        }
        
        return user;
    }
    
    public User changeInfo(String username, String password) throws InvalidDataException {
        if (StringUtils.isBlank(username) || username.length() < 6 || username.length() > 32) {
            throw new InvalidDataException("请输入6到32位用户名");
        }
        if (StringUtils.isBlank(password) || password.length() < 6 || password.length() > 32) {
            throw new InvalidDataException("请输入6到32位密码");
        }
        User user = userManager.get();
        if (user == null) {
            throw new InvalidDataException("找不到该用户");
        }
        if (getRepository().existsByUsernameAndIdNot(username, user.getId())) {
            throw new InvalidDataException("该用户名已被使用");
        }
        user = findById(user.getId());
        if (user == null) {
            throw new InvalidDataException("找不到该用户");
        }
        user.setUsername(username);
        user.setPassword(BCrypt.hashpw(password, user.getSalt()));
        user.setToken(genToken(user));
        return save(user);
    }
    
    public User findByUsername(String username) {
        return getRepository().findFirstByUsername(username);
    }
    
    public void clean() {
        getRepository().deleteAllByOperateAtBefore(
                new Date(System.currentTimeMillis() - 12 * 30 * 24 * 60 * 60 * 1000L)
        );
    }
    
    
}
