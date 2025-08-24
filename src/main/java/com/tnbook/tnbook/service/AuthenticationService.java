package com.tnbook.tnbook.service;

import com.tnbook.tnbook.dtos.LoginUserDto;
import com.tnbook.tnbook.dtos.RegisterUserDto;
import com.tnbook.tnbook.dtos.VerifyUserDto;
import com.tnbook.tnbook.model.entity.User;
import com.tnbook.tnbook.model.enums.AuthProvider;
import com.tnbook.tnbook.repository.UserRepository;
import com.tnbook.tnbook.exception.AuthenticationException;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@AllArgsConstructor
@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;

    public User signup(RegisterUserDto registerUserDto) {

            Optional<User> optionalUser = userRepository.findByEmail(registerUserDto.getEmail());

            if (optionalUser.isPresent()) {
                throw new AuthenticationException(HttpStatus.BAD_REQUEST, "This email is already in use");
            }

            User user = new User(registerUserDto.getEmail(), this.bCryptPasswordEncoder.encode(registerUserDto.getPassword()), AuthProvider.LOCAL);
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationExpiration(LocalDateTime.now().plusMinutes(30));
            user.setEnabled(false);
            sendVerificationEmail(user);
            return userRepository.save(user);

    }

    public User authenticate(LoginUserDto loginUserDto) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserDto.getEmail(), loginUserDto.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new AuthenticationException(HttpStatus.BAD_REQUEST, "The email address or password is incorrect");
        } catch (DisabledException e) {
            throw new AuthenticationException(HttpStatus.FORBIDDEN, "User account is not verified");
        } catch (LockedException e) {
            throw new AuthenticationException(HttpStatus.LOCKED, "User account is locked");
        }

        System.out.println("pass");
        return userRepository.findByEmail(loginUserDto.getEmail())
                .orElseThrow(
                        () -> new AuthenticationException(HttpStatus.BAD_REQUEST, "The email address or password is incorrect")
                );
    }

    public void verifyUser(VerifyUserDto verifyUserDto) {
        Optional<User> optionalUser = userRepository.findByEmail(verifyUserDto.getEmail());
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.getVerificationExpiration().isBefore(LocalDateTime.now())) {
                throw new AuthenticationException(HttpStatus.BAD_REQUEST, "This verification code is expired");
            }
            if(user.getVerificationCode().equals(verifyUserDto.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationExpiration(null);
                userRepository.save(user);
            } else {
                throw new AuthenticationException(HttpStatus.BAD_REQUEST, "Invalid verification code");
            }
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if(user.isEnabled()) {
                throw new AuthenticationException(HttpStatus.BAD_REQUEST, "Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationExpiration(LocalDateTime.now().plusMinutes(30));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    public void sendVerificationEmail(User user) {
        String subject = "Account verification";
        String verificationCode = user.getVerificationCode();
        String body = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            this.emailService.sendVerificationEmail(user.getEmail(), subject, body);
        } catch(MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(999999) + 100000;
        return String.valueOf(code);
    }
}
