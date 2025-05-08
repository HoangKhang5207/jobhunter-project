package com.hoangkhang.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hoangkhang.jobhunter.domain.Company;
import com.hoangkhang.jobhunter.domain.dto.CompanyDTO;
import com.hoangkhang.jobhunter.domain.dto.Meta;
import com.hoangkhang.jobhunter.domain.dto.ResultPaginationDTO;
import com.hoangkhang.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company fetchCompanyById(Long id) {
        return this.companyRepository.findById(id).orElse(null);
    }

    public ResultPaginationDTO fetchAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> page = this.companyRepository.findAll(spec, pageable);

        ResultPaginationDTO result = new ResultPaginationDTO();

        Meta meta = new Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        result.setMeta(meta);
        result.setResult(page.getContent());

        return result;
    }

    public Company handleCreateCompany(CompanyDTO companyRequest) {
        Company company = new Company();
        company.setName(companyRequest.getName());
        company.setAddress(companyRequest.getAddress());
        company.setLogo(companyRequest.getLogo());
        company.setDescription(companyRequest.getDescription());

        return this.companyRepository.save(company);
    }

    public Company handleUpdateCompany(CompanyDTO companyRequest) {
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
        this.companyRepository.deleteById(id);
    }
}
