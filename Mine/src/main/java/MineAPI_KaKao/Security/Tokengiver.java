package MineAPI_KaKao.Security;

import MineAPI_KaKao.User.Uuser;
import MineAPI_KaKao.Util.StringArrayConverter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
@RequiredArgsConstructor
public class Tokengiver { // -- jwt 발급

    private static final Long exp = 1000L * 60 * 60; //-- 토큰의 유효기간
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization"; // -- 토큰 첨부 헤더 명
    public static final String SECRET = "SECRET_KEY"; //-- 시크릿 키


    public static String createjwt(Uuser uuser){  // -- 유저 정보를 바탕으로 토큰 생성
        StringArrayConverter stringArrayConverter = new StringArrayConverter();

        String roles = stringArrayConverter.convertToDatabaseColumn(
                uuser.getRoles()
        );

        String jwt = JWT.create() // -- 유저 객체의 정보가담긴 jwt 생성
                .withSubject(uuser.getName())
                .withExpiresAt(new Date(System.currentTimeMillis() + exp))
                .withClaim("id", uuser.getId())
                .withClaim("roles",roles)
                .sign(Algorithm.HMAC512(SECRET));
        
        return TOKEN_PREFIX + jwt;
    }

    public String resolveToken(HttpServletRequest request){
        return request.getHeader("Authorization");
    } //-- 토큰 검증

    public static DecodedJWT verify(String jwt) throws SignatureVerificationException, TokenExpiredException {

        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                .build()
                .verify(jwt);
        System.out.println("토큰이 싱싱하구마");
        return decodedJWT;
    }

    private String getUsername(String accessToken) { // -- 토큰에서 유저 정보 추출
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();
    }
}