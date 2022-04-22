package tech.chenh.doch.data.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Result<T> implements Serializable {
    
    private Integer code;
    
    private String message;
    
    private T data;
    
    public Result() {
    }
    
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public static <T> Result<T> succeed(String message, T data) {
        return new Result<>(Code.SUCCEED, message, data);
    }
    
    public static <T> Result<T> succeed(T data) {
        return new Result<>(Code.SUCCEED, "SUCCEED", data);
    }
    
    public static <T> Result<T> succeed() {
        return new Result<>(Code.SUCCEED, "SUCCEED", null);
    }
    
    public static <T> Result<T> fail(String message, T data) {
        return new Result<>(Code.FAIL, message, data);
    }
    
    public static <T> Result<T> fail(String message) {
        return new Result<>(Code.FAIL, message, null);
    }
    
    public static <T> Result<T> requireLogin(String message) {
        return new Result<>(Code.REQUIRE_LOGIN, message, null);
    }
    
    public interface Code {
        
        int SUCCEED = 200;
        int FAIL = 500;
        
        int REQUIRE_LOGIN = 300;
        
    }
    
}
