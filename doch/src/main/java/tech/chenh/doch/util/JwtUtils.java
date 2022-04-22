package tech.chenh.doch.util;

import tech.chenh.doch.exception.LoginException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class JwtUtils {
    
    public static final Integer EXPIRE_DAYS = 7;
    private static final String CLAIM_KEY = "username";
    private static final Algorithm ALGORITHM = Algorithm.HMAC256("Doch#Algorithm@2021");
    private static final JWTVerifier VERIFIER = JWT.require(ALGORITHM).build();
    
    public static String encode(String username) {
        return JWT.create()
                .withClaim(CLAIM_KEY, username)
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRE_DAYS * 24 * 60 * 60 * 1000L))
                .sign(ALGORITHM);
    }
    
    public static String decode(String token) throws LoginException {
        if (StringUtils.isBlank(token)) {
            throw new LoginException("请先登录");
        }
        try {
            return VERIFIER.verify(token).getClaim(CLAIM_KEY).asString();
        } catch (Throwable e) {
            throw new LoginException("用户登录已过期");
        }
    }
    
}
