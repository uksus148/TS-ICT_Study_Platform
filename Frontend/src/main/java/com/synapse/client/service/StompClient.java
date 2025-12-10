package com.synapse.client.service;

import javafx.application.Platform;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class StompClient {
    private static StompClient instance;
    private WebSocket webSocket;
    private final Map<String, Consumer<String>> subscriptions = new ConcurrentHashMap<>();

    private static final String WS_URL = "ws://localhost:8080/ws/websocket";

    private StompClient() {}

    public static synchronized StompClient getInstance() {
        if (instance == null) instance = new StompClient();
        return instance;
    }

    public void connect() {
        if (webSocket != null) return;

        HttpClient client = HttpClient.newHttpClient();
        client.newWebSocketBuilder()
                .buildAsync(URI.create(WS_URL), new WebSocketListener())
                .thenAccept(ws -> {
                    this.webSocket = ws;
                    String connectFrame = "CONNECT\naccept-version:1.1,1.0\nhost:localhost\n\n\0";
                    ws.sendText(connectFrame, true);
                    System.out.println("WebSocket Connected!");
                })
                .exceptionally(e -> {
                    System.err.println("WebSocket connection failed: " + e.getMessage());
                    return null;
                });
    }

    public void subscribe(String topic, Consumer<String> callback) {
        if (webSocket == null) {
            System.err.println("Cannot subscribe: WebSocket is not connected.");
            return;
        }

        String subId = "sub-" + topic.hashCode();
        subscriptions.put(subId, callback);

        String subscribeFrame = "SUBSCRIBE\nid:" + subId + "\ndestination:" + topic + "\n\n\0";
        webSocket.sendText(subscribeFrame, true);
        System.out.println("Subscribed to: " + topic);
    }

    private class WebSocketListener implements WebSocket.Listener {
        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            String message = data.toString();
            if (message.startsWith("MESSAGE")) {
                handleMessage(message);
            }

            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        private void handleMessage(String rawFrame) {

            String[] parts = rawFrame.split("\n\n", 2);
            if (parts.length < 2) return;

            String headers = parts[0];
            String body = parts[1].replace("\0", "");

            for (String line : headers.split("\n")) {
                if (line.startsWith("subscription:")) {
                    String subId = line.split(":")[1];
                    if (subscriptions.containsKey(subId)) {
                        Platform.runLater(() -> subscriptions.get(subId).accept(body));
                    }
                }
            }
        }
    }
}