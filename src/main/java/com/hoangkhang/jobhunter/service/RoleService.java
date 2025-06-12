package com.hoangkhang.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hoangkhang.jobhunter.domain.Permission;
import com.hoangkhang.jobhunter.domain.Role;
import com.hoangkhang.jobhunter.domain.response.ResultPaginationDTO;
import com.hoangkhang.jobhunter.repository.PermissionRepository;
import com.hoangkhang.jobhunter.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository,
            PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role fetchRoleById(Long id) {
        return this.roleRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO fetchAllRoles(Specification<Role> spec, Pageable pageable) {
        Page<Role> page = this.roleRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        result.setMeta(meta);
        result.setResult(page.getContent());

        return result;
    }

    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    public Role createRole(Role role) {
        // check permissions
        if (role.getPermissions() != null) {
            List<Long> permissionIds = role.getPermissions().stream()
                    .map(p -> p.getId())
                    .toList();

            List<Permission> permissions = this.permissionRepository.findByIdIn(permissionIds);
            role.setPermissions(permissions);
        }

        return this.roleRepository.save(role);
    }

    public Role updateRole(Role role) {
        Role roleDB = this.fetchRoleById(role.getId());

        // check permissions
        if (role.getPermissions() != null) {
            List<Long> permissionIds = role.getPermissions().stream()
                    .map(p -> p.getId())
                    .toList();

            List<Permission> permissions = this.permissionRepository.findByIdIn(permissionIds);
            role.setPermissions(permissions);
        }

        roleDB.setName(role.getName());
        roleDB.setDescription(role.getDescription());
        roleDB.setActive(role.isActive());
        roleDB.setPermissions(role.getPermissions());

        roleDB = this.roleRepository.save(roleDB);

        return roleDB;
    }

    public void deleteRole(Long id) {
        this.roleRepository.deleteById(id);
    }
}
