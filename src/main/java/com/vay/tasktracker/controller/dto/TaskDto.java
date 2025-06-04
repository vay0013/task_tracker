package com.vay.tasktracker.controller.dto;

import java.time.Instant;

public record TaskDto(
        String title,
        String description,
        Instant expiryDate) {
}
