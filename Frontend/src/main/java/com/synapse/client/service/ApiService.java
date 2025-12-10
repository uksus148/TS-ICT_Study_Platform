package com.synapse.client.service;

import com.google.gson.*;
        import com.synapse.client.model.*;
        import com.synapse.client.model.dto.*; // Импортируем все DTO (включая новые инвайты)

        import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class ApiService {
    private static ApiService instance;
    private final HttpClient client;
    private final Gson gson;
    private String currentSessionId = null;

    private static final String BASE_URL = "http://localhost:8080";

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

    public static synchronized ApiService getInstance() {
        if (instance == null) instance = new ApiService();
        return instance;
    }

    private void extractCookie(HttpResponse<?> response) {
        Optional<String> cookieHeader = response.headers().firstValue("Set-Cookie");
        if (cookieHeader.isEmpty()) {
            cookieHeader = response.headers().firstValue("set-cookie");
        }

        cookieHeader.ifPresent(rawCookie -> this.currentSessionId = rawCookie.split(";")[0]);
    }

    private HttpRequest.Builder newRequestBuilder(String path) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path));

        if (currentSessionId != null) {
            builder.header("Cookie", currentSessionId);
        }
        return builder;
    }

    // --- AUTHENTICATION ---

    public CompletableFuture<User> loginUser(String email, String password) {
        LoginRequest requestDto = new LoginRequest(email, password);
        return sendAuthRequest("/auth/login", requestDto);
    }

    public CompletableFuture<User> registerUser(String username, String email, String password) {
        RegisterRequest requestDto = new RegisterRequest(username, email, password);
        return sendAuthRequest("/auth/register", requestDto);
    }

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

    public CompletableFuture<Void> logout() {
        HttpRequest request = newRequestBuilder("/auth/logout")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenAccept(r -> this.currentSessionId = null);
    }

    // --- GROUPS ---

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

    // --- TASKS ---

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

    // --- RESOURCES ---

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

    // --- INVITATIONS (NEW) ---

    public CompletableFuture<InviteCreateResponseDTO> createInvitation(Long groupId) {
        HttpRequest request = newRequestBuilder("/api/invitations/" + groupId)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return sendRequest(request, InviteCreateResponseDTO.class);
    }

    public CompletableFuture<InviteAcceptDTO> acceptInvitation(String token) {
        HttpRequest request = newRequestBuilder("/api/invitations/accept/" + token)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return sendRequest(request, InviteAcceptDTO.class);
    }

    public CompletableFuture<InviteValidateDTO> validateInvitation(String token) {
        HttpRequest request = newRequestBuilder("/api/invitations/validate/" + token)
                .GET()
                .build();
        return sendRequest(request, InviteValidateDTO.class);
    }

    // --- GROUP REQUESTS (OLD LOGIC - KEEP IF NEEDED) ---

    public CompletableFuture<GroupRequest[]> getMyRequests() {
        HttpRequest request = newRequestBuilder("/api/requests/my")
                .GET()
                .build();
        return sendRequest(request, GroupRequest[].class);
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

    public CompletableFuture<User> updateUser(User user) {
        String json = gson.toJson(user);

        HttpRequest request = newRequestBuilder("/api/users/" + user.getUser_id())
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return sendRequest(request, User.class);
    }

    // --- HELPER METHODS ---

    private <T> CompletableFuture<T> sendRequest(HttpRequest request, Class<T> responseType) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 300) {
                        // Можно добавить логирование ошибки здесь
                        System.err.println("Request failed: " + response.statusCode() + " | Body: " + response.body());
                        return null;
                    }
                    // Если ожидаемый тип Void, возвращаем null без попытки парсинга JSON
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