package com.example.generator.controller;

import com.example.generator.service.RsvpStreamService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class RsvpController {

    private final RsvpStreamService streamService;

    public RsvpController(RsvpStreamService streamService) {
        this.streamService = streamService;
    }

    @GetMapping(path = "/rsvp", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamRsvpEvents() {
        return streamService.addEmitter();
    }
}
