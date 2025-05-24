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

import com.hoangkhang.jobhunter.domain.Job;
import com.hoangkhang.jobhunter.domain.response.ResultPaginationDTO;
import com.hoangkhang.jobhunter.domain.response.job.ResCreateJobDTO;
import com.hoangkhang.jobhunter.domain.response.job.ResUpdateJobDTO;
import com.hoangkhang.jobhunter.exception.custom.IdInvalidException;
import com.hoangkhang.jobhunter.service.JobService;
import com.hoangkhang.jobhunter.util.annotation.ApiMessage;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs")
    @ApiMessage("Get all jobs")
    public ResponseEntity<ResultPaginationDTO> getAllJobs(
            @Filter Specification<Job> spec,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.fetchAllJobs(spec, pageable));
    }

    @GetMapping("/jobs/{id}")
    @ApiMessage("Get a job by id")
    public ResponseEntity<Job> getJobById(@PathVariable("id") Long id) throws IdInvalidException {
        // check id
        Job currentJob = this.jobService.fetchJobById(id);
        if (currentJob == null) {
            throw new IdInvalidException("Job id = " + id + " not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(currentJob);
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a new job")
    public ResponseEntity<ResCreateJobDTO> createJob(@Valid @RequestBody Job jobRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.createJob(jobRequest));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update an existing job")
    public ResponseEntity<ResUpdateJobDTO> updateJob(@Valid @RequestBody Job jobRequest) throws IdInvalidException {
        // check id
        Job currentJob = this.jobService.fetchJobById(jobRequest.getId());
        if (currentJob == null) {
            throw new IdInvalidException("Job id = " + jobRequest.getId() + " not found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.jobService.updateJob(jobRequest, currentJob));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete a job")
    public ResponseEntity<Void> deleteJob(@PathVariable("id") Long id) throws IdInvalidException {
        // check id
        Job currentJob = this.jobService.fetchJobById(id);
        if (currentJob == null) {
            throw new IdInvalidException("Job id = " + id + " not found");
        }

        this.jobService.deleteJob(id);

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
