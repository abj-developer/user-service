package com.abj.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User information")
public class UserRequestDTO {

    @Schema(
            description = "User's first name",
            example = "Rahul"
    )
    @NotBlank(message = "firstName must not be blank")
    @Size(min = 2, max = 100, message = "firstName must be between 2 and 100 characters")
    private String firstName;

    @Schema(
            description = "User's last name",
            example = "Sharma"
    )
    private String lastName;

    @Schema(
            description = "User email address",
            example = "rahul@example.com"
    )
    @NotBlank(message = "email must not be blank")
    @Email(message = "email must be a valid email address")
    private String email;

    @Schema(
            description = "Department to which user belongs",
            example = "101"
    )
    @NotNull(message = "departmentId must not be null")
    private Long departmentId;
}
