package cloud.chenh.doch.data.service;

import cloud.chenh.doch.auth.UserManager;
import cloud.chenh.doch.data.base.BaseService;
import cloud.chenh.doch.data.entity.User;
import cloud.chenh.doch.data.repo.UserRepository;
import cloud.chenh.doch.exception.InvalidDataException;
import cloud.chenh.doch.exception.LoginException;
import cloud.chenh.doch.exception.RegisterException;
import cloud.chenh.doch.util.JwtUtils;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService extends BaseService<User> {
    
    private final Map<String, User> loginCache = new ConcurrentHashMap<>();
    
    @Autowired
    private UserManager userManager;
    
    @Autowired
    private UserRepository userRepository;
    
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
        
        password = Md5Crypt.apr1Crypt(password, username);
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setOperateAt(new Date());
        save(user);
    }
    
    public String login(String username, String password) throws LoginException {
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
        
        String inputPassword = Md5Crypt.apr1Crypt(password, username);
        if (!user.getPassword().equals(inputPassword)) {
            throw new LoginException("不正确的用户名或密码");
        }
        
        user.setOperateAt(new Date());
        save(user);
        
        return JwtUtils.encode(username);
    }
    
    public User changeInfo(String username, String password) throws InvalidDataException {
        if (StringUtils.isBlank(username) || username.length() < 6 || username.length() > 32) {
            throw new InvalidDataException("请输入6到32位用户名");
        }
        
        if (StringUtils.isBlank(password) || password.length() < 6 || password.length() > 32) {
            throw new InvalidDataException("请输入6到32位密码");
        }
        
        User user = findByUsername(userManager.get());
        if (user == null) {
            throw new InvalidDataException("找不到该用户");
        }
        
        if (getRepository().existsByUsernameAndIdNot(username, user.getId())) {
            throw new InvalidDataException("用户名已被使用");
        }
        
        user.setUsername(username);
        user.setPassword(Md5Crypt.apr1Crypt(password, username));
        return save(user);
    }
    
    public User findByUsername(String username) {
        return getRepository().findFirstByUsername(username);
    }
    
    public void clean() {
        getRepository().deleteAllByOperateAtBefore(
                new Date(System.currentTimeMillis() - JwtUtils.EXPIRE_DAYS * 2 * 24 * 60 * 60 * 1000L)
        );
    }
    
    
}
