package com.hoangkhang.jobhunter.domain.response.user;

import java.time.Instant;

import com.hoangkhang.jobhunter.domain.enums.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResUserDTO {
    private Long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant createdAt;
    private Instant updatedAt;
    private CompanyUser company;

    @Getter
    @Setter
    public static class CompanyUser {
        private Long id;
        private String name;
    }
}
