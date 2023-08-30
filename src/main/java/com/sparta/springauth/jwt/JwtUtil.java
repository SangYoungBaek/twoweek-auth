package com.sparta.springauth.jwt;

import com.sparta.springauth.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    // Header KEY 값   Cookie의 네임값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    // 사용자 권한 값의 KEY , 권한을 구분하기 위한 권한을 가져오기 위한 키
    public static final String AUTHORIZATION_KEY = "auth";
    // Token 식별자, 규칙 ( 하나의 규약 ), 토큰값과 구분하기 위해 한칸 뛰어쓰기한다.
    public static final String BEARER_PREFIX = "Bearer ";
    // 토큰 만료시간
    private final long TOKEN_TIME = 60 * 60 * 1000L; // 60분

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private Key key;  // init 메서드에섭 변환이 된 키 값이 여기에 담긴다.
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    // 로그 설정
    public static final Logger logger = LoggerFactory.getLogger("JWT 관련 로그");
    // @Slf4j 를 선언해서 로깅을 선언할 수도 있다.


    @PostConstruct  // 딱한번 만 받아오면 되는 값을 사용할때 마다 요청을 새로요청하는 것을 방지
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // JWT 생성
    // 토큰 생성
    public String createToken(String username, UserRoleEnum role) {
        Date date = new Date();
        // BEARER_PREFIX  붙이는 코드
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID), 또는 PK 값 을 넣어도 상관없다.
                        .claim(AUTHORIZATION_KEY, role) // 사용자 권한  -> 권한 값을 그대로 넣는다. 나중에 키값을 쓰고 싶으면 claim에서 꺼내서 쓸 수 있다. key_value
                        .setExpiration(new Date(date.getTime() + TOKEN_TIME)) // 만료 시간  -> 현재시간 + TOKEN_TIME(60분)
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘, 제일 중요!!
                        .compact();
    }       // 무조건 이렇게 쓰는 건 아니다!!

    // 생성된 JWT를 Cookie에 저장
    // JWT Cookie 에 저장
    public void addJwtToCookie(String token, HttpServletResponse res) {
        try {
            token = URLEncoder.encode(token, "utf-8").replaceAll("\\+", "%20"); // Cookie Value 에는 공백이 불가능해서 encoding 진행

            Cookie cookie = new Cookie(AUTHORIZATION_HEADER, token); // Name-Value
            cookie.setPath("/");

            // Response 객체에 Cookie 추가
            res.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage());
        }
    }
    // 필요한 이유 !! : "Bearer "를 잘라내야하기 때문에 필요
    // Cookie에 들어있던 JWT 토큰을 Substring
    // JWT 토큰 substring
    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        logger.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }
    // JWT 검증
    // 토큰 검증
    public boolean validateToken(String token) {
        try {                       // 암호화 할때 썻던 key          // 받아온 토큰
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            logger.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.");
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
        }
        return false;
    }

    // JWT에서 사용자 정보 가져오기
    // 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        //                                                                          Body 부분에 있는 Clims를 가져온다.
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}









