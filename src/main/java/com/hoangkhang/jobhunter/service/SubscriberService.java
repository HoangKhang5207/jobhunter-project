package com.hoangkhang.jobhunter.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.hoangkhang.jobhunter.domain.Job;
import com.hoangkhang.jobhunter.domain.Skill;
import com.hoangkhang.jobhunter.domain.Subscriber;
import com.hoangkhang.jobhunter.domain.response.email.ResEmailJob;
import com.hoangkhang.jobhunter.repository.JobRepository;
import com.hoangkhang.jobhunter.repository.SkillRepository;
import com.hoangkhang.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository,
            JobRepository jobRepository, EmailService emailService) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    // @Scheduled(cron = "*/30 * * * * *")
    // public void testCron() {
    // System.out.println("Test cron job running...");
    // }

    public boolean isExistByEmail(String email) {
        return this.subscriberRepository.existsByEmail(email);
    }

    public Subscriber fetchSubscriberByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }

    public Subscriber fetchSubscriberById(Long id) {
        return this.subscriberRepository.findById(id).orElse(null);
    }

    public Subscriber createSubscriber(Subscriber subscriber) {
        // check skills
        if (subscriber.getSkills() != null) {
            List<Long> skillIds = subscriber.getSkills().stream()
                    .map(skill -> skill.getId())
                    .toList();

            List<Skill> skills = this.skillRepository.findByIdIn(skillIds);
            subscriber.setSkills(skills);
        }

        return this.subscriberRepository.save(subscriber);
    }

    public Subscriber updateSubscriber(Subscriber subsDB, Subscriber subsRequest) {
        // check skills
        if (subsRequest.getSkills() != null) {
            List<Long> skillIds = subsRequest.getSkills().stream()
                    .map(skill -> skill.getId())
                    .toList();

            List<Skill> skills = this.skillRepository.findByIdIn(skillIds);
            subsDB.setSkills(skills);
        }

        return this.subscriberRepository.save(subsDB);
    }

    public void sendSubscriberEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && !listSubs.isEmpty()) {
            for (Subscriber subscriber : listSubs) {
                List<Skill> listSkills = subscriber.getSkills();
                if (listSkills != null && !listSkills.isEmpty()) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && !listJobs.isEmpty()) {

                        List<ResEmailJob> arr = listJobs.stream()
                                .map(job -> {
                                    ResEmailJob resJob = new ResEmailJob();
                                    resJob.setName(job.getName());
                                    resJob.setSalary(job.getSalary());
                                    resJob.setCompany(new ResEmailJob.CompanyEmail(job.getCompany().getName()));
                                    resJob.setSkills(job.getSkills().stream()
                                            .map(skill -> new ResEmailJob.SkillEmail(skill.getName()))
                                            .toList());
                                    return resJob;
                                })
                                .toList();

                        this.emailService.sendEmailFromTemplateSync(
                                subscriber.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay!", "job",
                                subscriber.getName(),
                                arr);
                    }
                }
            }
        }
    }
}
