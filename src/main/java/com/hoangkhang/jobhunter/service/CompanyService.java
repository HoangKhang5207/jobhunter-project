package com.hoangkhang.jobhunter.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hoangkhang.jobhunter.domain.Company;
import com.hoangkhang.jobhunter.domain.User;
import com.hoangkhang.jobhunter.domain.request.ReqCompanyDTO;
import com.hoangkhang.jobhunter.domain.response.ResultPaginationDTO;
import com.hoangkhang.jobhunter.repository.CompanyRepository;
import com.hoangkhang.jobhunter.repository.UserRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company fetchCompanyById(Long id) {
        return this.companyRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO fetchAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> page = this.companyRepository.findAll(spec, pageable);

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

    public Company handleCreateCompany(ReqCompanyDTO companyRequest) {
        Company company = new Company();
        company.setName(companyRequest.getName());
        company.setAddress(companyRequest.getAddress());
        company.setLogo(companyRequest.getLogo());
        company.setDescription(companyRequest.getDescription());

        return this.companyRepository.save(company);
    }

    public Company handleUpdateCompany(ReqCompanyDTO companyRequest) {
        Company company = fetchCompanyById(companyRequest.getId());
        if (company != null) {
            company.setName(companyRequest.getName());
            company.setAddress(companyRequest.getAddress());
            company.setLogo(companyRequest.getLogo());
            company.setDescription(companyRequest.getDescription());

            return this.companyRepository.save(company);
        }
        return null;
    }

    public void handleDeleteCompany(Long id) {
        Company company = fetchCompanyById(id);

        if (company != null) {
            // fetch all users of company
            List<User> users = this.userRepository.findByCompany(company);
            // delete all users of company
            this.userRepository.deleteAll(users);
        }

        this.companyRepository.deleteById(id);
    }
}
