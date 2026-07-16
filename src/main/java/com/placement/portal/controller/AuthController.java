package com.placement.portal.controller;

import com.placement.portal.dto.LoginRequest;
import com.placement.portal.dto.SignupRequest;
import com.placement.portal.dto.UserResponse;
import com.placement.portal.model.CompanyProfile;
import com.placement.portal.model.StudentProfile;
import com.placement.portal.model.User;
import com.placement.portal.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Persist SecurityContext to HttpSession for subsequent REST calls
            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            User user = userService.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found after authentication!"));

            StudentProfile studentProfile = userService.getStudentProfile(user).orElse(null);
            CompanyProfile companyProfile = userService.getCompanyProfile(user).orElse(null);

            return ResponseEntity.ok(new UserResponse(user, studentProfile, companyProfile));
        } catch (Exception ex) {
            return ResponseEntity.status(401).body("Invalid email or password.");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        try {
            User user = userService.registerUser(signupRequest);
            return ResponseEntity.ok("User registered successfully! Please log in.");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(500).body("Error occurred during registration: " + ex.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        User user = userService.findByEmail(principal.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        StudentProfile studentProfile = userService.getStudentProfile(user).orElse(null);
        CompanyProfile companyProfile = userService.getCompanyProfile(user).orElse(null);

        return ResponseEntity.ok(new UserResponse(user, studentProfile, companyProfile));
    }
}
