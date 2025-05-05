package com.hoangkhang.jobhunter.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hoangkhang.jobhunter.domain.User;
import com.hoangkhang.jobhunter.service.UserService;

@RestController
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> fetchUser(@PathVariable Long id) {
        // return ResponseEntity.ok(this.userService.fetchUserById(id));
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchUserById(id));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> fetchAllUsers() {
        // return ResponseEntity.ok(this.userService.fetchAllUsers());
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User userRequest) {
        String hashPassword = this.passwordEncoder.encode(userRequest.getPassword());
        userRequest.setPassword(hashPassword);
        User user = this.userService.handleCreateUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/users")
    public ResponseEntity<User> updateUser(@RequestBody User userRequest) {
        // return ResponseEntity.ok(this.userService.handleUpdateUser(userRequest));
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.handleUpdateUser(userRequest));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        this.userService.handleDeleteUser(id);
        // return ResponseEntity.ok("User deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body("User deleted successfully");
    }
}
