package com.synapse.client.service;

import javafx.application.Platform;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * A lightweight STOMP (Simple Text Oriented Messaging Protocol) client implementation over WebSocket.
 * <p>
 * This class handles real-time bidirectional communication with the backend. It allows the application to:
 * <ul>
 * <li><b>Connect</b> to the Spring Boot WebSocket endpoint.</li>
 * <li><b>Subscribe</b> to specific topics (e.g., group updates).</li>
 * <li><b>Receive</b> and route asynchronous messages to the appropriate callback functions.</li>
 * </ul>
 * It uses the native {@link java.net.http.WebSocket} client introduced in Java 11.
 */
public class StompClient {

    private static StompClient instance;
    private WebSocket webSocket;

    // Thread-safe map to store active subscriptions: Subscription ID -> Callback Function
    private final Map<String, Consumer<String>> subscriptions = new ConcurrentHashMap<>();

    private static final String WS_URL = "ws://localhost:8080/ws/websocket";

    private StompClient() {}

    /**
     * Returns the singleton instance of the StompClient.
     * Ensures only one active WebSocket connection exists for the application.
     *
     * @return The global StompClient instance.
     */
    public static synchronized StompClient getInstance() {
        if (instance == null) instance = new StompClient();
        return instance;
    }

    /**
     * Initiates the WebSocket connection to the server.
     * <p>
     * 1. Builds a standard WebSocket connection.
     * 2. Once connected, sends a STOMP <b>CONNECT</b> frame to perform the handshake.
     * <p>
     * The connection happens asynchronously.
     */
    public void connect() {
        if (webSocket != null) return; // Already connected

        HttpClient client = HttpClient.newHttpClient();
        client.newWebSocketBuilder()
                .buildAsync(URI.create(WS_URL), new WebSocketListener())
                .thenAccept(ws -> {
                    this.webSocket = ws;
                    System.out.println("WebSocket Connected!");

                    // Send STOMP CONNECT Frame
                    // \0 is the null byte, required to terminate STOMP frames
                    String connectFrame = "CONNECT\naccept-version:1.1,1.0\nhost:localhost\n\n\0";
                    ws.sendText(connectFrame, true);
                })
                .exceptionally(e -> {
                    System.err.println("WebSocket connection failed: " + e.getMessage());
                    return null;
                });
    }

    /**
     * Subscribes to a specific STOMP destination (topic).
     *
     * @param topic    The destination string (e.g., "/topic/group/1").
     * @param callback A function to execute when a message arrives for this topic.
     * The function receives the message body as a String.
     */
    public void subscribe(String topic, Consumer<String> callback) {
        if (webSocket == null) {
            System.err.println("Cannot subscribe: WebSocket is not connected.");
            return;
        }

        // Generate a unique ID for this subscription based on the topic
        String subId = "sub-" + topic.hashCode();
        subscriptions.put(subId, callback);

        System.out.println("Subscribed to: " + topic);

        // Send STOMP SUBSCRIBE Frame
        String subscribeFrame = "SUBSCRIBE\nid:" + subId + "\ndestination:" + topic + "\n\n\0";
        webSocket.sendText(subscribeFrame, true);
    }

    /**
     * Internal listener implementation to handle incoming WebSocket events.
     */
    private class WebSocketListener implements WebSocket.Listener {

        /**
         * Invoked when a text message is received from the server.
         * Used to parse STOMP frames.
         */
        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            String message = data.toString();

            // We only care about MESSAGE frames (actual data pushed from server)
            if (message.startsWith("MESSAGE")) {
                handleMessage(message);
            } else if (message.startsWith("CONNECTED")) {
                System.out.println("STOMP Handshake success.");
            } else if (message.startsWith("ERROR")) {
                System.err.println("STOMP Error received: " + message);
            }

            return WebSocket.Listener.super.onText(webSocket, data, last);
        }

        /**
         * Parses a raw STOMP MESSAGE frame.
         * <p>
         * A typical STOMP frame looks like:
         * <pre>
         * COMMAND
         * header1:value1
         * header2:value2
         *
         * Body content...^@
         * </pre>
         * This method splits headers and body, finds the 'subscription' header,
         * and routes the body to the correct callback.
         */
        private void handleMessage(String rawFrame) {
            // Split headers and body by the first empty line (\n\n)
            String[] parts = rawFrame.split("\n\n", 2);
            if (parts.length < 2) return;

            String headers = parts[0];
            String body = parts[1].replace("\0", ""); // Remove null terminator

            // Find the subscription ID in the headers to know who this message is for
            for (String line : headers.split("\n")) {
                if (line.startsWith("subscription:")) {
                    String subId = line.split(":")[1];

                    if (subscriptions.containsKey(subId)) {
                        // Execute the callback on the JavaFX Application Thread
                        // to allow safe UI updates (e.g., showing notifications).
                        Platform.runLater(() -> subscriptions.get(subId).accept(body));
                    }
                }
            }
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.err.println("WebSocket Error: " + error.getMessage());
        }
    }
}