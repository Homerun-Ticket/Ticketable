package com.example.ticketable.common.config;

import com.example.ticketable.common.filter.JwtAuthenticationFilter;
import com.example.ticketable.domain.member.role.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
	
	private final JwtAuthenticationFilter authenticationFilter;
	
	@Bean
	public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.addFilterBefore(authenticationFilter, SecurityContextHolderAwareRequestFilter.class)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.logout(AbstractHttpConfigurer::disable)
			.rememberMe(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/login.html").permitAll()
				.requestMatchers("/point-charge.html").permitAll()
				.requestMatchers("/api/v1/auth/**").permitAll()
				.requestMatchers("/actuator/prometheus").permitAll()
				.requestMatchers(HttpMethod.GET,"/api/v1/games/**").authenticated()
				.requestMatchers("/api/v1/games/**").hasAuthority(MemberRole.Authority.ADMIN)
				.requestMatchers("/api/v1/stadiums/**").hasAuthority(MemberRole.Authority.ADMIN)
				.requestMatchers("/api/v1/sections/**").hasAuthority(MemberRole.Authority.ADMIN)
				.requestMatchers("/api/v1/seats/**").hasAuthority(MemberRole.Authority.ADMIN)

				.anyRequest().authenticated()
			);
		
		return http.build();
	}
}
