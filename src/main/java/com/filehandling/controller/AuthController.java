package com.filehandling.controller;


import com.filehandling.model.User;
import com.filehandling.repository.UserRepository;
import com.filehandling.request.AuthenticationRequest;
import com.filehandling.request.SignupRequest;
import com.filehandling.request.UserDto;
import com.filehandling.service.AuthService;
import com.filehandling.utils.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@AllArgsConstructor
public class AuthController {


    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final UserRepository userRepository;

    private  final JwtUtil jwtUtil;

    private final AuthService authService;

    public static final String TOKEN_PREFIX="Bearer";
    public static final String HEADER_STRING="Authorization";

    public void createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest,
                                          HttpServletResponse response) throws IOException {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                    authenticationRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(" Incorrect username and Password");
        }

        final UserDetails userDetails=userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        Optional<User> optionalUser=userRepository.findFirstByEmail(userDetails.getUsername());
        final String jwt=jwtUtil.generateToken(userDetails.getUsername());

        if(optionalUser.isPresent()){
            response.getWriter().write(new JSONObject()
                    .put("userId", optionalUser.get().getId())
                    .put("role",optionalUser.get().getRole())
                    .toString()
            );
            response.addHeader(HEADER_STRING,TOKEN_PREFIX+jwt);
        }
    }

    public ResponseEntity<?> signupUser(@RequestBody SignupRequest signupRequest){
        if(authService.hasUserwithEmail(signupRequest.getEmail())){
            return new ResponseEntity<>("user already exists", HttpStatus.NOT_ACCEPTABLE);
        }
        UserDto userDto=authService.createUser(signupRequest);
        return new ResponseEntity<>(userDto,HttpStatus.OK);
    }
}
