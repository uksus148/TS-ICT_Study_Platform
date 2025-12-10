package com.synapse.client.model.dto;

import com.synapse.client.enums.AcceptStatus;

public record InviteValidateDTO(String groupName, AcceptStatus valid) {}