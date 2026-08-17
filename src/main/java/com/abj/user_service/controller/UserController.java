package com.abj.user_service.controller;

import com.abj.user_service.VO.ResponseTemplateVO;
import com.abj.user_service.entity.User;
import com.abj.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "User APIs",
        description = "APIs related to user management"
)
@RestController
@RequestMapping("/users")
@Slf4j

public class UserController {

    @Autowired
    private UserService userService;

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
    @PostMapping("/")
    public User saveUser(@RequestBody User user){
        log.info("inside saveUser method of UserController");
        return userService.saveUser(user);
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
    @GetMapping("/{id}")
    public ResponseTemplateVO getUserWitDepartment(@PathVariable("id") Long userId){
        log.info("inside getUserWitDepartment method of UserController");
        return userService.getUserWitDepartment(userId);
    }
}
