package com.hoangkhang.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoangkhang.jobhunter.domain.Permission;
import com.hoangkhang.jobhunter.domain.response.ResultPaginationDTO;
import com.hoangkhang.jobhunter.exception.custom.IdInvalidException;
import com.hoangkhang.jobhunter.service.PermissionService;
import com.hoangkhang.jobhunter.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @GetMapping("/permissions")
    @ApiMessage("Get all permissions")
    public ResponseEntity<ResultPaginationDTO> getAllPermissions(
            @Filter Specification<Permission> spec,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.permissionService.fetchAllPermissions(spec, pageable));
    }

    @PostMapping("/permissions")
    @ApiMessage("Create a new permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permissionRequest)
            throws IdInvalidException {
        // check id exist
        boolean isIdExist = this.permissionService.isPermissionExist(permissionRequest);
        if (isIdExist) {
            throw new IdInvalidException("Permission already exists");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.permissionService.createPermission(permissionRequest));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update a permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permissionRequest)
            throws IdInvalidException {
        // check id
        if (this.permissionService.fetchPermissionById(permissionRequest.getId()) == null) {
            throw new IdInvalidException("Permission id = " + permissionRequest.getId() + " not found");
        }

        // check exist by module, apiPath and method
        if (this.permissionService.isPermissionExist(permissionRequest)) {
            if (this.permissionService.isSameName(permissionRequest))
                throw new IdInvalidException("Permission already exists");
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.permissionService.updatePermission(permissionRequest));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete a permission")
    public ResponseEntity<Void> deletePermission(@PathVariable("id") Long id) throws IdInvalidException {
        // check id
        if (this.permissionService.fetchPermissionById(id) == null) {
            throw new IdInvalidException("Permission id = " + id + " not found");
        }

        this.permissionService.deletePermission(id);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
