package com.tnbook.tnbook.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDto {
    private String token;
    private long expiresIn;
}
