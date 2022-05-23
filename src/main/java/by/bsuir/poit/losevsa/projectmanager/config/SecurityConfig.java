package by.bsuir.poit.losevsa.projectmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
            .antMatchers("/*").permitAll()
            .and().formLogin().permitAll()
            .loginPage("/login")
            .loginProcessingUrl("/perform-login")
            .usernameParameter("username")
            .passwordParameter("password")
            .defaultSuccessUrl("/");
    }

}
