package MineAPI_KaKao.Security;

import MineAPI_KaKao.User.UReposit;
import MineAPI_KaKao.User.UserDto;
import MineAPI_KaKao.User.Uuser;
import lombok.AllArgsConstructor;
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
public class Securitconfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder encodepass(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public Uuser uuser(){return new Uuser();}

    @Override
    protected void configure(HttpSecurity http) throws Exception{

        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/main","/mem","/img").authenticated()
                .anyRequest().permitAll()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_processing")
                .defaultSuccessUrl("/");
        http.addFilter(new JwtAuthenticationFilter(authenticationManager()));
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception{

        return authenticationConfiguration.getAuthenticationManager();
    }
}
