package tech.chenh.doch.data.api;

import tech.chenh.doch.auth.AuthFilter;
import tech.chenh.doch.data.model.Result;
import tech.chenh.doch.data.service.UserService;
import tech.chenh.doch.exception.InvalidDataException;
import tech.chenh.doch.exception.LoginException;
import tech.chenh.doch.exception.RegisterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("user")
public class UserApi {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("login")
    public Result<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            return Result.succeed(userService.login(username, password));
        } catch (LoginException e) {
            return Result.fail(e.getMessage());
        }
    }
    
    @PostMapping
    public Result<?> changeInfo(@RequestParam String username, @RequestParam String password) {
        try {
            return Result.succeed(userService.changeInfo(username, password));
        } catch (InvalidDataException e) {
            return Result.fail(e.getMessage());
        }
    }
    
    @PostMapping("register")
    public Result<?> register(@RequestParam String username, @RequestParam String password) {
        try {
            userService.register(username, password);
            return Result.succeed();
        } catch (RegisterException e) {
            return Result.fail(e.getMessage());
        }
    }
    
    @RequestMapping("require-login")
    public Result<?> requireLogin(HttpServletRequest request) {
        return Result.requireLogin((String) request.getAttribute(AuthFilter.MESSAGE));
    }
    
}
