package com.hiekn.boot.autoconfigure.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class JwtToken {

    private JwtProperties jwtProperties;

    public JwtToken(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }


    public String createToken(Integer userId) {
        //签发时间
        Date iaDate = new Date();

        //过期时间
        Calendar nowTime = Calendar.getInstance();
        nowTime.add(Calendar.DATE, jwtProperties.getExpireDate());
        Date expireDate = nowTime.getTime();

        Map<String,Object> map = Maps.newHashMap();
        map.put("alg","HS256");
        map.put("typ","JWT");
        return JWT.create()
                .withHeader(map)
                .withClaim("userId",userId)
                .withExpiresAt(expireDate)
                .withIssuedAt(iaDate)
                .withIssuer(jwtProperties.getIssuer())
                .sign(getAlgorithm());
    }

    private String createNewToken(DecodedJWT jwt) {
        Date issuedAt = jwt.getIssuedAt();
        if(System.currentTimeMillis() - issuedAt.getTime() > jwtProperties.getRefreshInterval()){
            return createToken(jwt.getClaim("userId").asInt());
        }
        return null;
    }

    private Algorithm getAlgorithm(){
        try {
            return Algorithm.HMAC256(jwtProperties.getSecretKey());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("The Secret Character Encoding is not supported");
        }
    }


    public DecodedJWT checkToken(String token){
        JWTVerifier verifier = JWT.require(getAlgorithm()).build();
        DecodedJWT jwt = verifier.verify(token);

        //通过之后，检查是否要返回新token
        String newToken = createNewToken(jwt);
        if(newToken != null){
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            response.setHeader("Authorization",newToken);
        }
        return jwt;
    }


    public Integer getCurrentUserId() {
        return checkToken(getToken()).getClaim("userId").asInt();
    }


    public String getToken(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isBlank(authorization)){
            return null;
        }
        String[] str = authorization.split(" ");
        return str.length == 2?str[1]:null;
    }

}
