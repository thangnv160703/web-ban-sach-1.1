package practice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import practice.serviceImpl.CustomUserDetailsServiceImpl;

@Configuration
public class SecurityConfig {
	
	@Autowired
	private LoginSuccessHandler loginSuccessHandler;
	
	@Bean
	public UserDetailsService userDetailsService(){
		return new CustomUserDetailsServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests()
		.requestMatchers("/admin/**", "/book/**", "/account/**", "/genre/**", "/order/**").hasRole("ADMIN")
		.requestMatchers("/user/**", "/cart/**", "/booksforsale/comment/*", "/history/**").hasRole("USER")
		.requestMatchers("/account-info/**", "/changeinfo/**").hasAnyRole("ADMIN", "USER")
		.requestMatchers("/", "/register/**", "/booksforsale/**", "/test").permitAll()
		.anyRequest().authenticated()
		.and()
		.formLogin().loginPage("/login").successHandler(loginSuccessHandler).permitAll()
		.and()
		.logout().logoutUrl("/logout").permitAll().logoutSuccessUrl("/");
		
		http.csrf().disable();
		
		return http.build();
	}
	
	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers("/books/**");
	}

}
