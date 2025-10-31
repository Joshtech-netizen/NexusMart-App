package com.nexusmart.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String testEndpoint() {
        return "🚀 Day 1: NexusMart Backend is running successfully!";
    }
}