package com.sbbc.rch.realchatapp.DTO;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {

    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, name = "is_online")
    private boolean isOnline;
}
