package com.tnbook.tnbook.dtos;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResendCodeDto {
    private String email;
}
