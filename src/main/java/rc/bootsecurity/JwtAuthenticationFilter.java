package rc.bootsecurity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import rc.bootsecurity.model.LoginViewModel;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	private AuthenticationManager authenticationManager;
	
	public JwtAuthenticationFilter (AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;		
	}
	
	 //trigger when user attempt to login
	 
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException { 

		LoginViewModel credentials = null;
		try {
			credentials = new ObjectMapper().readValue(request.getInputStream(), LoginViewModel.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//create login token 
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken
				(credentials.getUsername(), credentials.getPassword(), new ArrayList<>());
		
		//Authentication User
		Authentication auth = authenticationManager.authenticate(authenticationToken);
		return auth;
		
	}
	
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException { 

		UserPrincipal principal = (UserPrincipal) authResult.getPrincipal();
		
		//create the jwt
		String token = JWT.create()
				.withSubject(principal.getUsername())
				.withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
				.sign(HMAC512(JwtProperties.SECRET.getBytes()));
		
		//add token in response
		
		response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + token);
		
	}

}
