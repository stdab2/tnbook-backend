package com.tnbook.tnbook.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterUserDto {
    private String email;
    private String password;
}
