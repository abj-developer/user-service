package com.abj.user_service.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Schema(description = "User information")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Schema(
            description = "Unique identifier of user",
            example = "101"
    )
    private Long userId;

    @Schema(
            description = "User's first name",
            example = "Rahul"
    )
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
    private String email;

    @Schema(
            description = "Department to which user belongs",
            example = "101"
    )
    private Long departmentId;

}
