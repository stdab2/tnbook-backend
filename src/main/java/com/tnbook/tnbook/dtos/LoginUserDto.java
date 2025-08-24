package com.tnbook.tnbook.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginUserDto {
    private String email;
    private String password;
}
