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

import com.hoangkhang.jobhunter.domain.Role;
import com.hoangkhang.jobhunter.domain.response.ResultPaginationDTO;
import com.hoangkhang.jobhunter.exception.custom.IdInvalidException;
import com.hoangkhang.jobhunter.service.RoleService;
import com.hoangkhang.jobhunter.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles")
    @ApiMessage("Get all roles")
    public ResponseEntity<ResultPaginationDTO> getAllRoles(
            @Filter Specification<Role> spec,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.fetchAllRoles(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Get a role by id")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") Long id) throws IdInvalidException {
        Role role = this.roleService.fetchRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Role id = " + id + " not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(role);
    }

    @PostMapping("/roles")
    @ApiMessage("Create a new role")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role roleRequest)
            throws IdInvalidException {
        // check name exist
        if (this.roleService.existByName(roleRequest.getName())) {
            throw new IdInvalidException("Role with name = " + roleRequest.getName() + " already exists");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.roleService.createRole(roleRequest));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role roleRequest)
            throws IdInvalidException {
        // check id
        if (this.roleService.fetchRoleById(roleRequest.getId()) == null) {
            throw new IdInvalidException("Role id = " + roleRequest.getId() + " not found");
        }

        // // check exist name
        // if (this.roleService.existByName(roleRequest.getName())) {
        // throw new IdInvalidException("Role with name = " + roleRequest.getName() + "
        // already exists");
        // }

        return ResponseEntity.status(HttpStatus.OK).body(this.roleService.updateRole(roleRequest));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") Long id) throws IdInvalidException {
        // check id
        if (this.roleService.fetchRoleById(id) == null) {
            throw new IdInvalidException("Role id = " + id + " not found");
        }

        this.roleService.deleteRole(id);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
