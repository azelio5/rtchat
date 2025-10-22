package com.sbbc.rch.realchatapp.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {
    private String jwtToken;
    private UserDTO userDTO;
}
