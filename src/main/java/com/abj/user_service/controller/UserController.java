package com.abj.user_service.controller;

import com.abj.user_service.VO.ResponseTemplateVO;
import com.abj.user_service.entity.User;
import com.abj.user_service.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/")
    public User saveUser(@RequestBody User user){
        log.info("inside saveUser method of UserController");
        return userService.saveUser(user);
    }

    @GetMapping("/{id}")
    public ResponseTemplateVO getUserWitDepartment(@PathVariable("id") Long userId){
        log.info("inside getUserWitDepartment method of UserController");
        return userService.getUserWitDepartment(userId);
    }
}
