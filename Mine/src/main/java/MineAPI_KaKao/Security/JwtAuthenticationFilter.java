package MineAPI_KaKao.Security;

import MineAPI_KaKao.User.CustomUserDetails;
import MineAPI_KaKao.User.Uuser;
import MineAPI_KaKao.Util.StringArrayConverter;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    // ** Http 요청이 발생할 때마다 호출되는 메서드.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String prefixJwt = request.getHeader(Tokengiver.HEADER);
        System.out.println("필터 작동중"+ "  토큰:"+prefixJwt);


        if(prefixJwt == null) {
            chain.doFilter(request, response);
            return;
        }

        // ** Bearer 제거.
        String jwt = prefixJwt.replace(Tokengiver.TOKEN_PREFIX, "");

        try {
            log.debug("토근 있음.");

            // ** 토큰 검증
            DecodedJWT decodedJWT = Tokengiver.verify(jwt);

            // ** 사용자 정보 추출.
            int id = decodedJWT.getClaim("id").asInt();
            String roles = decodedJWT.getClaim("roles").asString();

            // ** 권한 정보를 문자열 리스트로 변환.
            StringArrayConverter stringArrayConverter = new StringArrayConverter();
            List<String> rolesList = stringArrayConverter.convertToEntityAttribute(roles);

            // ** 추출한 정보로 유저를 생성.
            Uuser user = Uuser.builder().id(id).roles(rolesList).build();
            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            // ** Spring Security 가 인증 정보를 관리하는데 사용.
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    customUserDetails,
                    customUserDetails.getPassword(),
                    customUserDetails.getAuthorities()
            );
            // ** SecurityContext에 저장.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("인증 객체 생성");
            System.out.println("인증성공");
        }
        catch (SignatureVerificationException sve) {
            log.debug("토큰 검증 실패");
        }
        catch (TokenExpiredException tee) {
            log.debug("토큰 사용 만료");
        } finally {
            // ** 필터로 응답을 넘긴다.
            chain.doFilter(request, response);
        }
    }
}
























