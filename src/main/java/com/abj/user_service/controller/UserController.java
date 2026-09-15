package com.abj.user_service.controller;

import com.abj.user_service.dto.UserRequestDTO;
import com.abj.user_service.dto.UserResponseDTO;
import com.abj.user_service.dto.UserUpdateRequestDTO;
import com.abj.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.abj.user_service.dto.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "User APIs",
        description = "APIs related to user management"
)
@RestController
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(
            summary = "Get users",
            description = "Fetches all users or filters them by departmentId"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users fetched successfully"
            )
    })
    @GetMapping({"/users", "/api/users"})
    public ResponseEntity<ResponseDTO<java.util.List<UserResponseDTO>>> getUsers(@RequestParam(value = "departmentId", required = false) Long departmentId) {
        log.info("inside getUsers method of UserController with departmentId={}", departmentId);
        if (departmentId != null) {
            return ResponseEntity.ok(ResponseDTO.success(userService.getUsersByDepartment(departmentId)));
        }
        return ResponseEntity.ok(ResponseDTO.success(userService.getAllUsers()));
    }

    @Operation(
            summary = "save User",
            description = "saves a new user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User saved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid User data"
            )
    })
    @PostMapping({"/users", "/api/users"})
    public ResponseEntity<ResponseDTO<UserResponseDTO>> saveUser(@Valid @RequestBody UserRequestDTO request){
        log.info("inside saveUser method of UserController");
        UserResponseDTO saved = userService.saveUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseDTO.success(saved));
    }

    @Operation(
            summary = "Get user by ID",
            description = "Fetches user details using the user ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User found successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @GetMapping({"/users/{id}", "/api/users/{id}"})
    public ResponseEntity<ResponseDTO<UserResponseDTO>> getUserById(@PathVariable("id") Long userId) {
        log.info("inside getUserById method of UserController");
        return ResponseEntity.ok(ResponseDTO.success(userService.getUserById(userId)));
    }

    @Operation(
            summary = "Update user",
            description = "Updates an existing user's name, email and department"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @PutMapping({"/users/{id}", "/api/users/{id}"})
    public ResponseEntity<ResponseDTO<UserResponseDTO>> updateUser(@PathVariable("id") Long userId,
                                     @Valid @RequestBody UserUpdateRequestDTO request) {
        log.info("inside updateUser method of UserController for userId={}", userId);
        return ResponseEntity.ok(ResponseDTO.success(userService.updateUser(userId, request)));
    }

    @Operation(
            summary = "Delete user",
            description = "Deletes a user by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @DeleteMapping({"/users/{id}", "/api/users/{id}"})
    public ResponseEntity<ResponseDTO<Void>> deleteUser(@PathVariable("id") Long userId) {
        log.info("inside deleteUser method of UserController for userId={}", userId);
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ResponseDTO.success(null));
    }
}
