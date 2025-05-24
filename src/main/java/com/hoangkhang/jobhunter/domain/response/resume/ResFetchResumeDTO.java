package com.hoangkhang.jobhunter.domain.response.resume;

import java.time.Instant;
import com.hoangkhang.jobhunter.domain.enums.ResumeSateEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResFetchResumeDTO {

    private Long id;
    private String email;
    private String url;
    private ResumeSateEnum status;

    private Instant createdAt;
    private Instant updatedAt;

    private String createdBy;
    private String updatedBy;

    private String companyName;
    private UserResume user;
    private JobResume job;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserResume {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class JobResume {
        private Long id;
        private String name;
    }
}
