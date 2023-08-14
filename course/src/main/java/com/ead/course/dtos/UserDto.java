package com.ead.course.dtos;

import com.ead.course.enums.UserStatus;
import com.ead.course.enums.UserType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class UserDto {

    private UUID userId;

    @NotBlank
    private String userName;

    @NotBlank
    private String email;

    private String fullName;

    @NotNull
    private UserStatus userStatus;

    @NotNull
    private UserType userType;

    @NotNull
    private String phoneNumber;

    @NotNull
    private String cpf;

    private String imageUrl;
}
