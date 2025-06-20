package com.hoangkhang.jobhunter.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.hoangkhang.jobhunter.domain.Company;
import com.hoangkhang.jobhunter.domain.Job;
import com.hoangkhang.jobhunter.domain.Resume;
import com.hoangkhang.jobhunter.domain.User;
import com.hoangkhang.jobhunter.domain.response.ResultPaginationDTO;
import com.hoangkhang.jobhunter.domain.response.resume.ResCreateResumeDTO;
import com.hoangkhang.jobhunter.domain.response.resume.ResFetchResumeDTO;
import com.hoangkhang.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import com.hoangkhang.jobhunter.exception.custom.IdInvalidException;
import com.hoangkhang.jobhunter.service.ResumeService;
import com.hoangkhang.jobhunter.service.UserService;
import com.hoangkhang.jobhunter.util.SecurityUtil;
import com.hoangkhang.jobhunter.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    @Autowired
    private FilterBuilder filterBuilder;

    public ResumeController(ResumeService resumeService, UserService userService) {
        this.resumeService = resumeService;
        this.userService = userService;
    }

    @GetMapping("/resumes")
    @ApiMessage("Get all resumes")
    public ResponseEntity<ResultPaginationDTO> getAllResumes(
            @Filter Specification<Resume> spec,
            Pageable pageable) {
        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        User currUser = this.userService.handleGetUserByUsername(email);
        if (currUser != null) {
            Company userCompany = currUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && !companyJobs.isEmpty()) {
                    arrJobIds = companyJobs.stream().map(e -> e.getId()).toList();
                }
            }
        }

        Specification<Resume> jobInSpec = filterSpecificationConverter
                .convert(filterBuilder.field("job").in(filterBuilder.input(arrJobIds)).get());

        Specification<Resume> finalSpec = jobInSpec.and(spec);

        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.fetchAllResumes(finalSpec, pageable));
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get a resume by id")
    public ResponseEntity<ResFetchResumeDTO> getResumeById(@PathVariable("id") Long id) throws IdInvalidException {
        // check id
        Resume currentResume = this.resumeService.fetchResumeById(id);
        if (currentResume == null) {
            throw new IdInvalidException("Resume id = " + id + " not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.getResume(currentResume));
    }

    @PostMapping("/resumes")
    @ApiMessage("Create a new resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resumeRequest)
            throws IdInvalidException {
        // check id exist
        boolean isIdExist = this.resumeService.checkResumeExistByUserAndJob(resumeRequest);
        if (!isIdExist) {
            throw new IdInvalidException("User id/Job id not exist");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.createResume(resumeRequest));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update a resume (status)")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resumeRequest)
            throws IdInvalidException {
        // check id
        Resume currentResume = this.resumeService.fetchResumeById(resumeRequest.getId());
        if (currentResume == null) {
            throw new IdInvalidException("Resume id = " + resumeRequest.getId() + " not found");
        }

        currentResume.setStatus(resumeRequest.getStatus());

        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.updateResume(currentResume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete a resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") Long id) throws IdInvalidException {
        // check id
        Resume currentResume = this.resumeService.fetchResumeById(id);
        if (currentResume == null) {
            throw new IdInvalidException("Resume id = " + id + " not found");
        }

        this.resumeService.deleteResume(id);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Fetch resumes by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable) {

        return ResponseEntity.status(HttpStatus.OK).body(this.resumeService.fetchResumeByUser(pageable));
    }
}
