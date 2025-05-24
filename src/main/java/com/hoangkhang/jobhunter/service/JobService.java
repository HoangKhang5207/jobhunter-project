package com.hoangkhang.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hoangkhang.jobhunter.domain.Company;
import com.hoangkhang.jobhunter.domain.Job;
import com.hoangkhang.jobhunter.domain.Skill;
import com.hoangkhang.jobhunter.domain.response.ResultPaginationDTO;
import com.hoangkhang.jobhunter.domain.response.job.ResCreateJobDTO;
import com.hoangkhang.jobhunter.domain.response.job.ResUpdateJobDTO;
import com.hoangkhang.jobhunter.repository.CompanyRepository;
import com.hoangkhang.jobhunter.repository.JobRepository;
import com.hoangkhang.jobhunter.repository.SkillRepository;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository,
            CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companyRepository = companyRepository;
    }

    public Job fetchJobById(Long id) {
        return this.jobRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO fetchAllJobs(Specification<Job> spec, Pageable pageable) {
        Page<Job> page = this.jobRepository.findAll(spec, pageable);

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

    public ResCreateJobDTO createJob(Job jobRequest) {
        // check skills
        if (jobRequest.getSkills() != null) {
            List<Long> skillIds = jobRequest.getSkills().stream().map(skill -> skill.getId()).toList();

            List<Skill> skills = this.skillRepository.findByIdIn(skillIds);
            jobRequest.setSkills(skills);
        }

        // check company
        if (jobRequest.getCompany() != null) {
            Optional<Company> company = this.companyRepository.findById(jobRequest.getCompany().getId());
            if (company.isPresent()) {
                jobRequest.setCompany(company.get());
            }
        }

        // save job
        Job job = this.jobRepository.save(jobRequest);

        // convert to ResCreateJobDTO
        ResCreateJobDTO resCreateJobDTO = new ResCreateJobDTO();
        resCreateJobDTO.setId(job.getId());
        resCreateJobDTO.setName(job.getName());
        resCreateJobDTO.setLocation(job.getLocation());
        resCreateJobDTO.setSalary(job.getSalary());
        resCreateJobDTO.setQuantity(job.getQuantity());
        resCreateJobDTO.setLevel(job.getLevel());
        resCreateJobDTO.setStartDate(job.getStartDate());
        resCreateJobDTO.setEndDate(job.getEndDate());
        resCreateJobDTO.setActive(job.isActive());
        resCreateJobDTO.setCreatedAt(job.getCreatedAt());
        resCreateJobDTO.setCreatedBy(job.getCreatedBy());

        // set skills
        if (job.getSkills() != null) {
            List<String> skillNames = job.getSkills().stream().map(skill -> skill.getName()).toList();
            resCreateJobDTO.setSkills(skillNames);
        }

        return resCreateJobDTO;
    }

    public ResUpdateJobDTO updateJob(Job jobRequest, Job jobInDB) {
        // check skills
        if (jobRequest.getSkills() != null) {
            List<Long> skillIds = jobRequest.getSkills().stream().map(skill -> skill.getId()).toList();

            List<Skill> skills = this.skillRepository.findByIdIn(skillIds);
            jobInDB.setSkills(skills);
        }

        // check company
        if (jobRequest.getCompany() != null) {
            Optional<Company> company = this.companyRepository.findById(jobRequest.getCompany().getId());
            if (company.isPresent()) {
                jobInDB.setCompany(company.get());
            }
        }

        // update correct information
        jobInDB.setName(jobRequest.getName());
        jobInDB.setLocation(jobRequest.getLocation());
        jobInDB.setSalary(jobRequest.getSalary());
        jobInDB.setQuantity(jobRequest.getQuantity());
        jobInDB.setLevel(jobRequest.getLevel());
        jobInDB.setStartDate(jobRequest.getStartDate());
        jobInDB.setEndDate(jobRequest.getEndDate());
        jobInDB.setActive(jobRequest.isActive());

        // update job
        Job job = this.jobRepository.save(jobInDB);

        // convert to ResUpdateJobDTO
        ResUpdateJobDTO resUpdateJobDTO = new ResUpdateJobDTO();
        resUpdateJobDTO.setId(job.getId());
        resUpdateJobDTO.setName(job.getName());
        resUpdateJobDTO.setLocation(job.getLocation());
        resUpdateJobDTO.setSalary(job.getSalary());
        resUpdateJobDTO.setQuantity(job.getQuantity());
        resUpdateJobDTO.setLevel(job.getLevel());
        resUpdateJobDTO.setStartDate(job.getStartDate());
        resUpdateJobDTO.setEndDate(job.getEndDate());
        resUpdateJobDTO.setActive(job.isActive());
        resUpdateJobDTO.setUpdatedAt(job.getUpdatedAt());
        resUpdateJobDTO.setUpdatedBy(job.getUpdatedBy());

        // set skills
        if (job.getSkills() != null) {
            List<String> skillNames = job.getSkills().stream().map(skill -> skill.getName()).toList();
            resUpdateJobDTO.setSkills(skillNames);
        }

        return resUpdateJobDTO;
    }

    public void deleteJob(Long id) {
        this.jobRepository.deleteById(id);
    }
}
