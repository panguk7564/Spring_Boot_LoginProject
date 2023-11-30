package MineAPI_KaKao.Security;

import MineAPI_KaKao.User.Uuser;
import MineAPI_KaKao.Util.StringArrayConverter;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
@RequiredArgsConstructor
public class Tokengiver {

    private static final Long exp = 1000L * 60 * 60;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
    public static final String SECRET = "SECRET_KEY";


    public static String createjwt(Uuser uuser){
        StringArrayConverter stringArrayConverter = new StringArrayConverter();

        String roles = stringArrayConverter.convertToDatabaseColumn(
                uuser.getRoles()
        );


        String jwt = JWT.create()
                .withSubject(uuser.getName())
                .withExpiresAt(new Date(System.currentTimeMillis() + exp))
                .withClaim("id", uuser.getId())
                .withClaim("roles",roles)
                .sign(Algorithm.HMAC512(SECRET));
        
        return TOKEN_PREFIX + jwt;
    }

    public String resolveToken(HttpServletRequest request){
        return request.getHeader("Authorization");
    }

    public static DecodedJWT verify(String jwt) throws SignatureVerificationException, TokenExpiredException {


        DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC512(SECRET))
                .build()
                .verify(jwt);
        System.out.println("토큰이 싱싱하구마");
        return decodedJWT;
    }

    private String getUsername(String accessToken) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();
    }


}
