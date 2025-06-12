package com.hoangkhang.jobhunter.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.hoangkhang.jobhunter.domain.Permission;
import com.hoangkhang.jobhunter.domain.Role;
import com.hoangkhang.jobhunter.domain.User;
import com.hoangkhang.jobhunter.exception.custom.PermissionException;
import com.hoangkhang.jobhunter.service.UserService;
import com.hoangkhang.jobhunter.util.SecurityUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PermissionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    @Transactional
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String requestURI = request.getRequestURI();
        String httpMethod = request.getMethod();

        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path: " + path);
        System.out.println(">>> requestURI: " + requestURI);
        System.out.println(">>> httpMethod: " + httpMethod);

        // check permission
        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        if (email != null && !email.isEmpty()) {
            User user = this.userService.handleGetUserByUsername(email);
            if (user != null) {
                Role role = user.getRole();
                if (role != null) {
                    List<Permission> permissions = role.getPermissions();
                    boolean hasPermission = permissions.stream()
                            .anyMatch(permission -> permission.getApiPath().equals(path)
                                    && permission.getMethod().equalsIgnoreCase(httpMethod));

                    if (!hasPermission) {
                        throw new PermissionException("Bạn không có quyền truy cập vào endpoint này");
                    }
                } else {
                    throw new PermissionException("Bạn không có quyền truy cập vào endpoint này");
                }
            }
        }

        return true;
    }

}
