package com.hoangkhang.jobhunter.controller;

import java.util.List;

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

import com.hoangkhang.jobhunter.domain.Skill;
import com.hoangkhang.jobhunter.domain.response.ResultPaginationDTO;
import com.hoangkhang.jobhunter.exception.custom.IdInvalidException;
import com.hoangkhang.jobhunter.service.SkillService;
import com.hoangkhang.jobhunter.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/skills")
    @ApiMessage("Get all skills")
    public ResponseEntity<ResultPaginationDTO> getAllSkills(
            @Filter Specification<Skill> spec,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.fetchAllSkills(spec, pageable));
    }

    @PostMapping("/skills")
    @ApiMessage("Create a new skill")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody Skill skillRequest) throws IdInvalidException {
        // check if skill name exists
        if (skillRequest.getName() != null && this.skillService.isNameExists(skillRequest.getName())) {
            throw new IdInvalidException("Skill name = " + skillRequest.getName() + " already exists");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.createSkill(skillRequest));
    }

    @PutMapping("/skills")
    @ApiMessage("Update an existing skill")
    public ResponseEntity<Skill> updateSkill(@Valid @RequestBody Skill skillRequest) throws IdInvalidException {
        // check id
        Skill currentSkill = this.skillService.fetchSkillById(skillRequest.getId());
        if (currentSkill == null) {
            throw new IdInvalidException("Skill id = " + skillRequest.getId() + " not found");
        }

        // check if skill name exists
        if (skillRequest.getName() != null && this.skillService.isNameExists(skillRequest.getName())) {
            throw new IdInvalidException("Skill name = " + skillRequest.getName() + " already exists");
        }

        currentSkill.setName(skillRequest.getName());

        return ResponseEntity.status(HttpStatus.OK).body(this.skillService.updateSkill(currentSkill));
    }

    @DeleteMapping("/skills/{id}")
    @ApiMessage("Delete a skill")
    public ResponseEntity<Void> deleteSkill(@PathVariable("id") Long id) throws IdInvalidException {
        // check id
        Skill currentSkill = this.skillService.fetchSkillById(id);
        if (currentSkill == null) {
            throw new IdInvalidException("Skill id = " + id + " not found");
        }

        this.skillService.deleteSkill(id);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
