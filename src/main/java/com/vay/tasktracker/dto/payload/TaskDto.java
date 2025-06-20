package com.vay.tasktracker.dto.payload;

import java.time.Instant;

public record TaskDto(
        String title,
        String description,
        Instant expiryDate) {
}
