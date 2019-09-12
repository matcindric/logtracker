package com.eccos.nadzorniservis.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Autowired
    UserDetailsService userDetailsService;
    
    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        
        http
            .csrf()
                .disable()
            .authorizeRequests()
            //.anyRequest().hasAnyRole("ADMIN", "USER")
                //.antMatchers("/admin/**").hasRole("ADMIN")
                //.antMatchers("/korisnik/**").hasRole("USER")
                //.antMatchers("/anonymous*").anonymous()
                .antMatchers("/javax.faces.resource/**").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                //.antMatchers("/**").permitAll()
                .antMatchers("/logtracker").permitAll()
                .antMatchers("/logtracker/odaziv").permitAll()              
                .antMatchers("/css/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/login.xhtml").permitAll()
                //.antMatchers("/iznimke.xhtml").permitAll()
            .anyRequest().authenticated()
                .and()
            //.httpBasic() //mozda ne treba
              //  .and()
            .formLogin()
                .loginPage("/login.xhtml")
                .defaultSuccessUrl("/home.xhtml", true)
                .failureUrl("/login.xhtml?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .and()
            .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                //.logoutUrl("/app-logout")
                .logoutSuccessUrl("/login.xhtml")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);
        
        http.userDetailsService(userDetailsService);
        http.headers().frameOptions().disable(); // ova linija mora biti
        http.headers().frameOptions().sameOrigin();
    }
    
    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(userDetailsService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
