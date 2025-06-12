package com.hoangkhang.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hoangkhang.jobhunter.domain.Job;
import com.hoangkhang.jobhunter.domain.Resume;
import com.hoangkhang.jobhunter.domain.User;
import com.hoangkhang.jobhunter.domain.response.ResultPaginationDTO;
import com.hoangkhang.jobhunter.domain.response.resume.ResCreateResumeDTO;
import com.hoangkhang.jobhunter.domain.response.resume.ResFetchResumeDTO;
import com.hoangkhang.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import com.hoangkhang.jobhunter.repository.JobRepository;
import com.hoangkhang.jobhunter.repository.ResumeRepository;
import com.hoangkhang.jobhunter.repository.UserRepository;
import com.hoangkhang.jobhunter.util.SecurityUtil;
import com.turkraft.springfilter.converter.FilterSpecification;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;
import com.turkraft.springfilter.parser.FilterParser;
import com.turkraft.springfilter.parser.node.FilterNode;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;

    @Autowired
    private FilterParser filterParser;

    @Autowired
    private FilterSpecificationConverter filterSpecificationConverter;

    public ResumeService(ResumeRepository resumeRepository, JobRepository jobRepository,
            UserRepository userRepository) {
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
    }

    public Resume fetchResumeById(Long id) {
        return this.resumeRepository.findById(id).orElse(null);
    }

    public ResFetchResumeDTO getResume(Resume resume) {
        ResFetchResumeDTO resFetchResumeDTO = new ResFetchResumeDTO();
        resFetchResumeDTO.setId(resume.getId());
        resFetchResumeDTO.setEmail(resume.getEmail());
        resFetchResumeDTO.setUrl(resume.getUrl());
        resFetchResumeDTO.setStatus(resume.getStatus());
        resFetchResumeDTO.setCreatedAt(resume.getCreatedAt());
        resFetchResumeDTO.setUpdatedAt(resume.getUpdatedAt());
        resFetchResumeDTO.setCreatedBy(resume.getCreatedBy());
        resFetchResumeDTO.setUpdatedBy(resume.getUpdatedBy());

        if (resume.getUser() != null) {
            resFetchResumeDTO.setUser(new ResFetchResumeDTO.UserResume(resume.getUser().getId(),
                    resume.getUser().getName()));
        }

        if (resume.getJob() != null) {
            resFetchResumeDTO.setJob(new ResFetchResumeDTO.JobResume(resume.getJob().getId(),
                    resume.getJob().getName()));
            resFetchResumeDTO.setCompanyName(resume.getJob().getCompany().getName());
        }

        return resFetchResumeDTO;
    }

    public ResultPaginationDTO fetchAllResumes(Specification<Resume> spec, Pageable pageable) {
        Page<Resume> page = this.resumeRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        result.setMeta(meta);

        List<ResFetchResumeDTO> resumes = page.getContent().stream().map(item -> getResume(item)).toList();

        result.setResult(resumes);

        return result;
    }

    public ResultPaginationDTO fetchResumeByUser(Pageable pageable) {
        // query builder
        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        FilterNode node = this.filterParser.parse("email = '" + email + "'");
        FilterSpecification<Resume> spec = this.filterSpecificationConverter.convert(node);
        Page<Resume> page = this.resumeRepository.findAll(spec, pageable);

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

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        // check user by id
        if (resume.getUser() == null)
            return false;

        Optional<User> userOptional = this.userRepository.findById(resume.getUser().getId());
        if (userOptional.isEmpty())
            return false;

        // check job by id
        if (resume.getJob() == null)
            return false;

        Optional<Job> jobOptional = this.jobRepository.findById(resume.getJob().getId());
        if (jobOptional.isEmpty())
            return false;

        return true;
    }

    public ResCreateResumeDTO createResume(Resume resumeRequest) {
        Resume resume = this.resumeRepository.save(resumeRequest);

        ResCreateResumeDTO resCreateResumeDTO = new ResCreateResumeDTO();
        resCreateResumeDTO.setId(resume.getId());
        resCreateResumeDTO.setCreatedAt(resume.getCreatedAt());
        resCreateResumeDTO.setCreatedBy(resume.getCreatedBy());

        return resCreateResumeDTO;
    }

    public ResUpdateResumeDTO updateResume(Resume resumeRequest) {
        Resume resume = this.resumeRepository.save(resumeRequest);

        ResUpdateResumeDTO resUpdateResumeDTO = new ResUpdateResumeDTO();
        resUpdateResumeDTO.setUpdatedAt(resume.getUpdatedAt());
        resUpdateResumeDTO.setUpdatedBy(resume.getUpdatedBy());

        return resUpdateResumeDTO;
    }

    public void deleteResume(Long id) {
        this.resumeRepository.deleteById(id);
    }
}
