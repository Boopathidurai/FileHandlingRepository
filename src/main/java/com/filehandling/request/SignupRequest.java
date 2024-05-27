package com.filehandling.request;

import lombok.Data;

@Data
public class SignupRequest {

    private Long id;
    private String email;
    private String password;
    private  String name;
}
