
package com.demo.project_management_system.config;
import com.demo.project_management_system.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService ;
    public SecurityConfig(UserService userService){
        this.userService = userService;
    }

	@Bean
    public SecurityFilterChain filterSecurity(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers("/list_users").authenticated()
                                .requestMatchers("/static/**").permitAll()
                                .requestMatchers("/admin/**").hasAuthority("ROLE_SYSTEM_ADMIN")
                                .requestMatchers("/project-manager/**").hasAuthority("ROLE_PROJECT_MANAGER")
                                .requestMatchers("/member/**").hasAuthority("ROLE_MEMBER")
                                .requestMatchers("/userList").hasAnyAuthority("ROLE_SYSTEM_ADMIN", "ROLE_PROJECT_MANAGER")

                                .anyRequest().permitAll()
                )
                .userDetailsService(userService)
                .csrf(AbstractHttpConfigurer :: disable)
                .formLogin(
                        form -> form
                                .loginPage("/")
                                .usernameParameter("email")
                                .passwordParameter("password")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/dashboard")
                                .permitAll()

                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                )
                .build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
