package rc.bootsecurity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import rc.bootsecurity.db.UserRepository;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{

	private UserPrincipalDetailsService userPrincipalDetailsService;
    private UserRepository userRepository;

    public SecurityConfiguration(UserPrincipalDetailsService userPrincipalDetailsService, UserRepository userRepository) {
        this.userPrincipalDetailsService = userPrincipalDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // remove csrf and state in session because in jwt we do not need them
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // add jwt filters (1. authentication, 2. authorization)
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(),  this.userRepository))
                .authorizeRequests()
                // configure access rules
                .antMatchers(HttpMethod.POST, "/login").permitAll()
                .antMatchers("/api/public/management/*").hasRole("MANAGER")
                .antMatchers("/api/public/admin/*").hasRole("ADMIN")
                .anyRequest().authenticated();
    }

	//http security parameter untuk authorize request, only into aunthenticated - ke login page,
	//apa pun requestnya harus ke Authentication dlu

	/*
	 * teknik ini digunakan untuk form login
	 * 
	 * @Override protected void configure(HttpSecurity http) throws Exception { http
	 * .authorizeRequests() .antMatchers("/index.html").permitAll()
	 * .antMatchers("/profile/**").authenticated()
	 * .antMatchers("/admin/**").hasRole("ADMIN")
	 * .antMatchers("/management/**").hasAnyRole("MANAGER","ADMIN")
	 * .antMatchers("/api/public/test1").hasAuthority("ACCESS_TEST1")
	 * .antMatchers("/api/public/test2").hasAuthority("ACCESS_TEST2")
	 * .antMatchers("/api/public/users").hasRole("ADMIN") .and() .formLogin()
	 * .loginProcessingUrl("/signin") .loginPage("/login") .permitAll() .and()
	 * .logout().logoutRequestMatcher(new
	 * AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login") .and()
	 * .rememberMe().tokenValiditySeconds(2592000).key("mySecreT") ; }
	 */
	//password encoder
	@Bean
	PasswordEncoder passwordEncoder () {
		return new BCryptPasswordEncoder();
	}

	
	@Bean
	DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
		daoAuthenticationProvider.setUserDetailsService(this.userPrincipalDetailsService);
		return daoAuthenticationProvider;
	}


	// to enable https 
	// add certificate: (A)self signed atau (B)buy 
	// (A).\keytool -genkey -alias bootsecurity -storetype PKCS12 -keyalg RSA -keysize 2048 -keystore bootsecurity.p12 -validity 3650
	// modify app.properties
	// add @Bean for servletWebServerFactory
}
