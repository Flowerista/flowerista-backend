package ua.flowerista.shop.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ua.flowerista.shop.auditing.ApplicationAuditAware;
import ua.flowerista.shop.repositories.UserRepository;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig implements WebMvcConfigurer {


	  private final UserRepository repository;

	  @Bean
	  public UserDetailsService userDetailsService() {
	    return username -> repository.findByEmail(username)
	        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
	  }

	  @Bean
	  public AuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	    authProvider.setUserDetailsService(userDetailsService());
	    authProvider.setPasswordEncoder(passwordEncoder());
	    return authProvider;
	  }

	  @Bean
	  public AuditorAware<Integer> auditorAware() {
	    return new ApplicationAuditAware();
	  }

	  @Bean
	  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
	    return config.getAuthenticationManager();
	  }

	  @Bean
	  public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	  }

	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/js/**")
				.addResourceLocations("classpath:/static/js/");
		registry.addResourceHandler("/css/**")
				.addResourceLocations("classpath:/static/css/");
	}

}
