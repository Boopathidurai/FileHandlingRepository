package com.filehandling.service;


import com.filehandling.request.SignupRequest;
import com.filehandling.request.UserDto;

public interface AuthService {

    public UserDto createUser(SignupRequest signupRequest);

    boolean hasUserwithEmail(String email);
}
