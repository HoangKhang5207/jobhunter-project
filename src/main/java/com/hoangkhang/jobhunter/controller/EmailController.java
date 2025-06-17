package com.hoangkhang.jobhunter.controller;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoangkhang.jobhunter.service.EmailService;
import com.hoangkhang.jobhunter.service.SubscriberService;
import com.hoangkhang.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("Send a simple email")
    // @Scheduled(cron = "*/30 * * * * *")
    // @Transactional
    public String sendSimpleEmail() {
        // this.emailService.sendEmailSync("hoangkhang16112003@gmail.com", "test send
        // email", "<h1> hello </h1>", false,
        // true);

        this.subscriberService.sendSubscriberEmailJobs();
        return "Email sent successfully!";
    }
}
