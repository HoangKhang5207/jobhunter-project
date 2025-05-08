package com.hoangkhang.jobhunter.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyDTO {
    private Long id;

    @NotBlank(message = "Trường name không được để trống")
    private String name;

    private String address;

    private String logo;

    private String description;
}
