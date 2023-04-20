package practice.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import practice.model.CustomUserDetails;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		String redirectURL = request.getContextPath();
		String role = userDetails.getRole();
		
		if(role.equals("ADMIN")) {
			redirectURL = "admin";
		}
		else {
			redirectURL = "user";
		}
		response.sendRedirect(redirectURL);
	}
	
}
