package com.tnbook.tnbook.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyUserDto {
    private String email;
    private String verificationCode;
}
