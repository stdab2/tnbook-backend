package com.tnbook.tnbook.controller;

import com.tnbook.tnbook.dtos.*;
import com.tnbook.tnbook.model.entity.User;
import com.tnbook.tnbook.service.AuthenticationService;
import com.tnbook.tnbook.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    private final JwtService jwtService;

    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        LoginResponseDto loginResponse = new LoginResponseDto(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        authenticationService.verifyUser(verifyUserDto);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Account verified successfully");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestBody ResendCodeDto resendCodeDto) {
        authenticationService.resendVerificationCode(resendCodeDto.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Verification code sent");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}
