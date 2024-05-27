package com.filehandling.serviceImpl;


import com.filehandling.model.User;
import com.filehandling.model.UserRole;
import com.filehandling.repository.UserRepository;
import com.filehandling.request.SignupRequest;
import com.filehandling.request.UserDto;
import com.filehandling.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDto createUser(SignupRequest signupRequest){
        User user=new User();

        user.setEmail(signupRequest.getEmail());
        user.setName(signupRequest.getName());
        user.setPassword(new BCryptPasswordEncoder().encode(signupRequest.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        User createuser=userRepository.save(user);

        UserDto userDto=new UserDto();
        userDto.setId(createuser.getId());

        return userDto;
    }

    @Override
    public boolean hasUserwithEmail(String email) {
        return userRepository.findFirstByEmail(email).isPresent();
    }

}
