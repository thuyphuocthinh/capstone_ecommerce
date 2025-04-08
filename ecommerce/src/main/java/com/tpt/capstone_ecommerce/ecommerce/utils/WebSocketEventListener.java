package com.tpt.capstone_ecommerce.ecommerce.utils;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        if (event.getUser() != null) {
            System.out.println("✅ WebSocket connected with user: " + event.getUser().getName());
        } else {
            System.out.println("⚠️ Connected user is null!");
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        if (event.getUser() != null) {
            System.out.println("👋 WebSocket disconnected: " + event.getUser().getName());
        } else {
            System.out.println("⚠️ Disconnected user is null!");
        }
    }

}
