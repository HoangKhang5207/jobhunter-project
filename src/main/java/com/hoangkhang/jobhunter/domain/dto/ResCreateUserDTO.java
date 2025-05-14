package com.hoangkhang.jobhunter.domain.dto;

import java.time.Instant;

import com.hoangkhang.jobhunter.domain.enums.GenderEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateUserDTO {

    private Long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant createdAt;
}
