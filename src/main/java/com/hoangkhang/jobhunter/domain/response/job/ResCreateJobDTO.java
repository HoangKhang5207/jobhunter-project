package com.hoangkhang.jobhunter.domain.response.job;

import java.time.Instant;
import java.util.List;

import com.hoangkhang.jobhunter.domain.enums.LevelEnum;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCreateJobDTO {

    private long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private LevelEnum level;
    private Instant startDate;
    private Instant endDate;
    private boolean active;
    private Instant createdAt;
    private String createdBy;
    private List<String> skills;
}
