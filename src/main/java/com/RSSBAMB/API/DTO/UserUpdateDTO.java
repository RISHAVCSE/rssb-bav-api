package com.RSSBAMB.API.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user update requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private String email;
    private String firstName;
    private String lastName;
    private String roles;
}

