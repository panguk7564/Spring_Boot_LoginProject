package MineAPI_KaKao.Security;

import MineAPI_KaKao.User.Uuser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class Securitconfig extends WebSecurityConfigurerAdapter { // -- 인증인가 환경설정

    @Bean
    public BCryptPasswordEncoder encodepass(){
        return new BCryptPasswordEncoder();
    } //-- 객체 등록

    @Bean
    public Uuser uuser(){return new Uuser();} //-- 객체 등록

    @Override
    protected void configure(HttpSecurity http) throws Exception{

        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/main","/mem","/img").authenticated() // -- 인증인가 이후 접근가능
                .anyRequest().permitAll() // -- 나머지 요청은 허가
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_processing")
                .defaultSuccessUrl("/");
        http.addFilter(new JwtAuthenticationFilter(authenticationManager())); // -- 보안 필터 정용
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception{

        return authenticationConfiguration.getAuthenticationManager();
    }
}
