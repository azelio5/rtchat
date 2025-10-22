package com.sbbc.rch.realchatapp.DTO;


import com.sbbc.rch.realchatapp.model.User;

public class Util {
    public static UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
               // .roles(user.getRoles())
                .build();
    }
}
