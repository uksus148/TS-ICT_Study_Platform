package com.synapse.client.service;

import com.google.gson.*;
import com.synapse.client.model.*;
import com.synapse.client.model.dto.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Centralized service for handling all HTTP REST API communication.
 * <p>
 * This class implements the <b>Singleton</b> pattern to ensure a single instance manages
 * the {@link HttpClient}, connection pooling, and session state (Cookies).
 * <p>
 * Key features:
 * <ul>
 * <li><b>Asynchronous I/O:</b> All public methods return {@link CompletableFuture}, ensuring
 * that network requests do not block the JavaFX Application Thread.</li>
 * <li><b>JSON Serialization:</b> Uses {@link Gson} with custom adapters for Java 8 Time API.</li>
 * <li><b>Session Management:</b> Automatically extracts and attaches 'JSESSIONID' cookies.</li>
 * </ul>
 */
public class ApiService {
    private static ApiService instance;
    private final HttpClient client;
    private final Gson gson;
    private String currentSessionId = null;

    private static final String BASE_URL = "http://localhost:8080";

    /**
     * Private constructor to enforce Singleton pattern.
     * <p>
     * Initializes the {@link HttpClient} and configures a {@link Gson} instance
     * with custom TypeAdapters to correctly serialize and deserialize
     * {@link LocalDate} and {@link LocalDateTime} objects to/from JSON strings.
     */
    private ApiService() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();

        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, ctx) -> {
                    String s = json.getAsString();
                    if (s.contains("T")) {
                        return LocalDate.parse(s.split("T")[0]);
                    }
                    return LocalDate.parse(s);
                })
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, type, ctx) ->
                        new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, ctx) -> {
                    try {
                        return LocalDateTime.parse(json.getAsString());
                    } catch (Exception e) {
                        return java.time.Instant.ofEpochMilli(json.getAsLong())
                                .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
                    }
                })
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, type, ctx) ->
                        new JsonPrimitive(src.toString()))
                .create();
    }

    /**
     * Returns the global singleton instance of the ApiService.
     *
     * @return The active ApiService instance.
     */
    public static synchronized ApiService getInstance() {
        if (instance == null) instance = new ApiService();
        return instance;
    }

    /**
     * Extracts the 'Set-Cookie' header from the HTTP response and stores it
     * for subsequent requests to maintain the user's session.
     */
    private void extractCookie(HttpResponse<?> response) {
        Optional<String> cookieHeader = response.headers().firstValue("Set-Cookie");
        if (cookieHeader.isEmpty()) {
            cookieHeader = response.headers().firstValue("set-cookie");
        }

        cookieHeader.ifPresent(rawCookie -> this.currentSessionId = rawCookie.split(";")[0]);
    }

    /**
     * Helper method to create a new HTTP Request Builder with the session cookie attached.
     *
     * @param path The API endpoint path (e.g., "/api/tasks").
     * @return A configured HttpRequest.Builder.
     */
    private HttpRequest.Builder newRequestBuilder(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path));

        if (currentSessionId != null) {
            builder.header("Cookie", currentSessionId);
        }
        return builder;
    }

    // ==========================================
    // AUTHENTICATION
    // ==========================================

    /**
     * Authenticates a user with email and password.
     *
     * @param email    User's email.
     * @param password User's password.
     * @return A Future containing the authenticated {@link User} object, or null if failed.
     */
    public CompletableFuture<User> loginUser(String email, String password) {
        LoginRequest requestDto = new LoginRequest(email, password);
        return sendAuthRequest("/auth/login", requestDto);
    }

    /**
     * Registers a new user account.
     *
     * @param username Display name.
     * @param email    Unique email address.
     * @param password Account password.
     * @return A Future containing the newly created {@link User} object.
     */
    public CompletableFuture<User> registerUser(String username, String email, String password) {
        RegisterRequest requestDto = new RegisterRequest(username, email, password);
        return sendAuthRequest("/auth/register", requestDto);
    }

    /**
     * Internal helper for sending Login/Register requests.
     * Handles JSON serialization and Cookie extraction upon success.
     */
    private CompletableFuture<User> sendAuthRequest(String endpoint, Object dto) {
        String json = gson.toJson(dto);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 300) {
                        return null;
                    }
                    extractCookie(response);

                    return gson.fromJson(response.body(), User.class);
                });
    }

    /**
     * Logs out the current user by invalidating the session on the server
     * and clearing the local session ID.
     */
    public CompletableFuture<Void> logout() {
        HttpRequest request = newRequestBuilder("/auth/logout")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenAccept(r -> this.currentSessionId = null);
    }

    // ==========================================
    // GROUPS MANAGEMENT
    // ==========================================

    /**
     * Fetches all study groups the current user belongs to.
     *
     * @return A Future containing an array of {@link Group} objects.
     */
    public CompletableFuture<Group[]> getAllGroups() {
        HttpRequest request = newRequestBuilder("/api/studyGroups/my-groups")
                .GET()
                .build();
        return sendRequest(request, Group[].class);
    }

    public CompletableFuture<Group> createGroup(Group group) {
        long userId = group.getCreated_by() != null ? group.getCreated_by() : 1L;
        String json = gson.toJson(group);

        HttpRequest request = newRequestBuilder("/api/studyGroups/" + userId)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequest(request, Group.class);
    }

    public CompletableFuture<Group> updateGroup(Group group) {
        String json = gson.toJson(group);
        HttpRequest request = newRequestBuilder("/api/studyGroups/" + group.getGroup_id())
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequest(request, Group.class);
    }

    public CompletableFuture<Void> deleteGroup(Long groupId) {
        HttpRequest request = newRequestBuilder("/api/studyGroups/" + groupId)
                .DELETE()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() >= 300) {
                        throw new RuntimeException("Error deleting group: " + response.statusCode());
                    }
                });
    }

    // ==========================================
    // TASKS MANAGEMENT
    // ==========================================

    /**
     * Fetches all tasks associated with the user across all groups.
     */
    public CompletableFuture<Task[]> getAllTasks() {
        HttpRequest request = newRequestBuilder("/api/tasks")
                .GET()
                .build();
        return sendRequest(request, Task[].class);
    }

    public CompletableFuture<Task> createTask(Task task) {
        Long groupId = task.getGroup_id();
        if (groupId == null) return CompletableFuture.failedFuture(new RuntimeException("Group ID missing"));

        String json = gson.toJson(task);
        HttpRequest request = newRequestBuilder("/api/tasks/" + groupId)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequest(request, Task.class);
    }

    public CompletableFuture<Task> updateTask(Task task) {
        String json = gson.toJson(task);
        HttpRequest request = newRequestBuilder("/api/tasks/" + task.getTask_id())
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequest(request, Task.class);
    }

    public CompletableFuture<Void> deleteTask(Long id) {
        HttpRequest request = newRequestBuilder("/api/tasks/" + id)
                .DELETE()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() >= 300) {
                        throw new RuntimeException("Error deleting task: " + response.statusCode());
                    }
                });
    }

    // ==========================================
    // RESOURCES & MEMBERS
    // ==========================================

    /**
     * Creates a new resource (File or Link) within a specific group.
     */
    public CompletableFuture<Resource> createResource(Resource resource) {
        Long groupId = resource.getGroup_id();

        if (groupId == null) return CompletableFuture.failedFuture(new RuntimeException("Group ID required"));

        String json = gson.toJson(resource);
        HttpRequest request = newRequestBuilder("/api/resources/" + groupId)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return sendRequest(request, Resource.class);
    }

    public CompletableFuture<Resource[]> getGroupResources(Long groupId) {
        HttpRequest request = newRequestBuilder("/api/resources/group/" + groupId)
                .GET()
                .build();
        return sendRequest(request, Resource[].class);
    }

    /**
     * Fetches the list of members for a specific group.
     */
    public CompletableFuture<User[]> getGroupMembers(Long groupId) {
        HttpRequest request = newRequestBuilder("/api/studyGroups/" + groupId + "/members")
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 300) {
                        return new User[0];
                    }
                    try {
                        return gson.fromJson(response.body(), User[].class);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return new User[0];
                    }
                });
    }

    /**
     * Removes a member from a group (Kick functionality).
     */
    public CompletableFuture<Void> removeMember(Long groupId, Long userId) {
        String path = "/api/studyGroups/" + groupId + "/members/" + userId;

        HttpRequest request = newRequestBuilder(path)
                .DELETE()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenAccept(response -> {
                    if (response.statusCode() >= 300) {
                        throw new RuntimeException("Failed to remove member. Status: " + response.statusCode());
                    }
                });
    }

    // ==========================================
    // INVITATIONS (TOKEN SYSTEM)
    // ==========================================

    /**
     * Requests the backend to generate a new unique invitation token for a group.
     */
    public CompletableFuture<InviteCreateResponseDTO> createInvitation(Long groupId) {
        HttpRequest request = newRequestBuilder("/api/invitations/" + groupId)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return sendRequest(request, InviteCreateResponseDTO.class);
    }

    /**
     * Attempts to join a group using an invitation token.
     */
    public CompletableFuture<InviteAcceptDTO> acceptInvitation(String token) {
        HttpRequest request = newRequestBuilder("/api/invitations/accept/" + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return sendRequest(request, InviteAcceptDTO.class);
    }

    /**
     * Validates a token without joining, useful for showing a preview of the group.
     */
    public CompletableFuture<InviteValidateDTO> validateInvitation(String token) {
        HttpRequest request = newRequestBuilder("/api/invitations/validate/" + token)
                .GET()
                .build();
        return sendRequest(request, InviteValidateDTO.class);
    }

    // ==========================================
    // MISC / LEGACY
    // ==========================================

    /**
     * Retrieves pending group requests (Legacy implementation).
     * Currently returns an empty list to prevent 404 errors if backend support is missing.
     */
    public CompletableFuture<GroupRequest[]> getMyRequests() {
        return CompletableFuture.completedFuture(new GroupRequest[0]);
    }

    public CompletableFuture<Void> respondToRequest(Long requestId, boolean accept) {
        String action = accept ? "accept" : "reject";
        HttpRequest request = newRequestBuilder("/api/requests/" + requestId + "/" + action)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenAccept(response -> {
                    if (response.statusCode() >= 300) {
                        throw new RuntimeException("Action failed: " + response.statusCode());
                    }
                });
    }

    /**
     * Updates user profile information.
     */
    public CompletableFuture<User> updateUser(User user) {
        String json = gson.toJson(user);

        HttpRequest request = newRequestBuilder("/api/users/" + user.getUser_id())
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return sendRequest(request, User.class);
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================

    /**
     * Generic helper method to send an async HTTP request and deserialize the JSON response.
     *
     * @param request      The prepared HttpRequest.
     * @param responseType The Class of the expected response object.
     * @param <T>          The type of the response object.
     * @return A Future containing the deserialized object, or null on error.
     */
    private <T> CompletableFuture<T> sendRequest(HttpRequest request, Class<T> responseType) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 300) {
                        System.err.println("Request failed: " + response.statusCode() + " | Body: " + response.body());
                        return null;
                    }
                    if (responseType == Void.class) {
                        return null;
                    }
                    try {
                        return gson.fromJson(response.body(), responseType);
                    } catch (JsonSyntaxException e) {
                        System.err.println("JSON Parse Error: " + e.getMessage());
                        return null;
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
}