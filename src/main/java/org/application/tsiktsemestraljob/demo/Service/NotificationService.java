package org.application.tsiktsemestraljob.demo.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendToGroup(Long groupId, String message) {
        messagingTemplate.convertAndSend("/topic/group/" + groupId, message);
    }

    public void sendToUser(Long userId, String message) {
        messagingTemplate.convertAndSend("/queue/user/" + userId, message);
    }
}
