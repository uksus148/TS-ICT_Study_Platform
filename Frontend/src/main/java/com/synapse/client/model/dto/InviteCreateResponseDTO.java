package com.synapse.client.model.dto;

import java.time.LocalDateTime;

public record InviteCreateResponseDTO(String token, LocalDateTime expiresAt) {}