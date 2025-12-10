package com.synapse.client.model.dto;

import com.synapse.client.enums.AcceptStatus;

public record InviteAcceptDTO(Long groupId, AcceptStatus status) {}