package com.hoangkhang.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoangkhang.jobhunter.domain.User;
import com.hoangkhang.jobhunter.domain.response.ResCreateUserDTO;
import com.hoangkhang.jobhunter.domain.response.ResUpdateUserDTO;
import com.hoangkhang.jobhunter.domain.response.ResUserDTO;
import com.hoangkhang.jobhunter.domain.response.ResultPaginationDTO;
import com.hoangkhang.jobhunter.exception.custom.IdInvalidException;
import com.hoangkhang.jobhunter.service.UserService;
import com.hoangkhang.jobhunter.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch user by id")
    public ResponseEntity<ResUserDTO> fetchUser(@PathVariable Long id) throws IdInvalidException {
        User user = this.userService.fetchUserById(id);
        if (user == null) {
            throw new IdInvalidException("User với Id = " + id + " không tồn tại");
        }
        // return ResponseEntity.ok(this.userService.fetchUserById(id));
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("Fetch all users")
    public ResponseEntity<ResultPaginationDTO> fetchAllUsers(
            @Filter Specification<User> spec,
            Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUsers(spec, pageable));
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createUser(@Valid @RequestBody User userRequest)
            throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(userRequest.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException(
                    "Email " + userRequest.getEmail() + " đã tồn tại. Vui lòng sử dụng email khác");
        }

        String hashPassword = this.passwordEncoder.encode(userRequest.getPassword());
        userRequest.setPassword(hashPassword);
        User user = this.userService.handleCreateUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(user));
    }

    @PutMapping("/users")
    @ApiMessage("Update user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User userRequest) throws IdInvalidException {
        User user = this.userService.handleUpdateUser(userRequest);
        if (user == null) {
            throw new IdInvalidException("User với Id = " + userRequest.getId() + " không tồn tại");
        }

        // return ResponseEntity.ok(this.userService.handleUpdateUser(userRequest));
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUpdateUserDTO(user));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete user by id")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) throws IdInvalidException {
        User user = this.userService.fetchUserById(id);
        if (user == null) {
            throw new IdInvalidException("User với Id = " + id + " không tồn tại");
        }

        this.userService.handleDeleteUser(id);
        // return ResponseEntity.ok("User deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
