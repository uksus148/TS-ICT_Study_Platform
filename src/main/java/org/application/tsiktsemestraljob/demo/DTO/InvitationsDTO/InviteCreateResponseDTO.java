package org.application.tsiktsemestraljob.demo.DTO.InvitationsDTO;

import java.time.LocalDateTime;

public record InviteCreateResponseDTO(String token, LocalDateTime expiresAt) {}
