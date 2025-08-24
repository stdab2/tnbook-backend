package com.tnbook.tnbook.dtos;

import com.tnbook.tnbook.model.enums.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;
    private String email;
    private AuthProvider authProvider;
    /*private String name;
    private String imageUrl;*/
}
