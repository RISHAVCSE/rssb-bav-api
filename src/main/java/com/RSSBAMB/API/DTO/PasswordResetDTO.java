package com.RSSBAMB.API.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for password reset requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDTO {
    private String newPassword;
}

