package com.hoangkhang.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hoangkhang.jobhunter.domain.Subscriber;
import com.hoangkhang.jobhunter.exception.custom.IdInvalidException;
import com.hoangkhang.jobhunter.service.SubscriberService;
import com.hoangkhang.jobhunter.util.SecurityUtil;
import com.hoangkhang.jobhunter.util.annotation.ApiMessage;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a new subscriber")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriberRequest)
            throws IdInvalidException {
        // check email exist
        boolean isEmailExist = this.subscriberService.isExistByEmail(subscriberRequest.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Subscriber with email = " + subscriberRequest.getEmail() + " already exists");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.subscriberService.createSubscriber(subscriberRequest));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update a subscriber (skill)")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriberRequest)
            throws IdInvalidException {
        // check id
        Subscriber currentSub = this.subscriberService.fetchSubscriberById(subscriberRequest.getId());
        if (currentSub == null) {
            throw new IdInvalidException("Subscriber id = " + subscriberRequest.getId() + " not found");
        }

        return ResponseEntity.status(HttpStatus.OK)
                .body(this.subscriberService.updateSubscriber(currentSub, subscriberRequest));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skills")
    public ResponseEntity<Subscriber> getSubscriberSkills()
            throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().orElse("");

        return ResponseEntity.status(HttpStatus.OK).body(
                this.subscriberService.fetchSubscriberByEmail(email));
    }
}
