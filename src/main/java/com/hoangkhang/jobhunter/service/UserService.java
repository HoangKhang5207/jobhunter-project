package com.hoangkhang.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hoangkhang.jobhunter.domain.User;
import com.hoangkhang.jobhunter.domain.dto.Meta;
import com.hoangkhang.jobhunter.domain.dto.ResultPaginationDTO;
import com.hoangkhang.jobhunter.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User userRequest) {
        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());

        return this.userRepository.save(user);
    }

    public User handleUpdateUser(User userRequest) {
        User user = fetchUserById(userRequest.getId());
        if (user != null) {
            user.setName(userRequest.getName());
            user.setEmail(userRequest.getEmail());
            user.setPassword(userRequest.getPassword());
            return this.userRepository.save(user);
        }
        return user;
    }

    public void handleDeleteUser(Long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(Long id) {
        return this.userRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO fetchAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> page = this.userRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();

        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        result.setMeta(meta);
        result.setResult(page.getContent());

        return result;
    }

    public User handleGetUserByUsername(String username) {
        return this.userRepository.findByEmail(username);
    }
}
