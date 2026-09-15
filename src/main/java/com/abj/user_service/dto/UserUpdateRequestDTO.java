package com.abj.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequestDTO {

    @Schema(
            description = "Full name of the user",
            example = "Rahul Sharma"
    )
    @Size(min = 2, max = 100, message = "name must be between 2 and 100 characters")
    private String name;

    @Schema(
            description = "Email address of the user",
            example = "rahul@example.com"
    )
    @Email(message = "email must be a valid email address")
    private String email;

    @Schema(
            description = "Department ID of the user",
            example = "101"
    )
    private Long departmentId;
}
