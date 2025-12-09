package com.synapse.client.service;

import com.google.gson.*;
import com.synapse.client.model.Group;
import com.synapse.client.model.Resource;
import com.synapse.client.model.Task;
import com.synapse.client.model.User;
import com.synapse.client.model.dto.RegisterRequest;
import com.synapse.client.store.MembershipDeserializer;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

public class ApiService {
    private static ApiService instance;
    private final HttpClient client;
    private final Gson gson;
    private final CookieManager cookieManager;

    private static final String BASE_URL = "http://localhost:8080";

    private ApiService() {
        this.cookieManager = new CookieManager();
        this.cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.client = HttpClient.newBuilder()
                .cookieHandler(this.cookieManager)
                .build();

        this.gson = new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskDeserializer())
                .registerTypeAdapter(Group.class, new GroupDeserializer())
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, ctx) ->
                        LocalDate.parse(json.getAsString()))
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, type, ctx) ->
                        new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, ctx) -> {
                    try { return LocalDateTime.parse(json.getAsString()); }
                    catch (Exception e) { return java.time.Instant.ofEpochMilli(json.getAsLong())
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDateTime(); }
                })
                .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, type, ctx) ->
                        new JsonPrimitive(src.toString()))
                .create();
    }

    public static synchronized ApiService getInstance() {
        if (instance == null) instance = new ApiService();
        return instance;
    }

    public CompletableFuture<Group[]> getAllGroups() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/studyGroups"))
                .GET()
                .build();

        return sendRequest(request, Group[].class);
    }

    public CompletableFuture<Group> createGroup(Group group) {
        Long userId = group.getCreated_by();
        if (userId == null) userId = 1L;
        // TODO: Remove after add profile

        String json = gson.toJson(group);
        String requestUrl = BASE_URL + "/api/studyGroups/" + userId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return sendRequest(request, Group.class);
    }

    public CompletableFuture<Group> updateGroup(Group group) {
        String json = gson.toJson(group);
        String requestUrl = BASE_URL + "/api/studyGroups/" + group.getGroup_id();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return sendRequest(request, Group.class);
    }

    public CompletableFuture<Void> deleteGroup(Long groupId) {
        String requestUrl = BASE_URL + "/api/studyGroups/" + groupId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .DELETE()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() >= 300) {
                        throw new RuntimeException("Error: " + response.statusCode());
                    }
                });
    }

    public CompletableFuture<Task[]> getAllTasks() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/tasks"))
                .GET()
                .build();

        return sendRequest(request, Task[].class);
    }

    public CompletableFuture<Task> createTask(Task task) {
        Long userId = task.getCreated_by();
        Long groupId = task.getGroup_id();

        if (userId == null) userId = 1L;
        // TODO: After Create Profile change to system error
        if (groupId == null) {
            System.err.println("Error: Group ID is null during creation!");
            return CompletableFuture.failedFuture(new RuntimeException("Group ID is missing"));
        }

        String json = gson.toJson(task);
        String requestUrl = BASE_URL + "/api/tasks/" + groupId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return sendRequest(request, Task.class);
    }

    public CompletableFuture<Task> updateTask(Task task) {
        String json = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/tasks/" + task.getTask_id())) // ID в URL
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return sendRequest(request, Task.class);
    }

    public CompletableFuture<User[]> getGroupMembers(Long groupId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/memberships/group/" + groupId))
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 300) {
                        System.err.println("Error loading members: " + response.statusCode());
                        return new User[0];
                    }

                    JsonArray jsonArray = JsonParser.parseString(response.body()).getAsJsonArray();
                    User[] users = new User[jsonArray.size()];
                    MembershipDeserializer md = new MembershipDeserializer();

                    for(int i=0; i<jsonArray.size(); i++) {
                        users[i] = md.deserialize(jsonArray.get(i), User.class, null);
                    }
                    return users;
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return new User[0];
                });
    }

    public CompletableFuture<Void> deleteTask(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/tasks/" + id))
                .DELETE()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() >= 300) {
                        throw new RuntimeException("Error: " + response.statusCode());
                    }
                });
    }

    public CompletableFuture<Resource> createResource(Resource resource) {
        Long userId = resource.getCreated_by();
        Long groupId = resource.getGroup_id();

        if (userId == null) userId = 1L;
        if (groupId == null) {
            System.err.println("Error: Group ID is missing for resource creation");
            return CompletableFuture.failedFuture(new RuntimeException("Group ID required"));
        }

        String json = gson.toJson(resource);
        String requestUrl = BASE_URL + "/api/resources/" + userId + "/" + groupId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return sendRequest(request, Resource.class);
    }

    public CompletableFuture<Resource[]> getGroupResources(Long groupId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/resources/group/" + groupId))
                .GET()
                .build();

        return sendRequest(request, Resource[].class);
    }

    public CompletableFuture<User> registerUser(String username, String email, String password) {
        RegisterRequest requestDto = new RegisterRequest(username, email, password);
        String json = gson.toJson(requestDto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return sendRequest(request, User.class);
    }

    public CompletableFuture<User> loginUser(String email, String password) {
        String json = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return sendRequest(request, User.class);
    }

    public CompletableFuture<Void> logout() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/logout"))
                .POST(HttpRequest.BodyPublishers.noBody()) // Тело не нужно
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                .thenAccept(response -> {
                    if (response.statusCode() >= 300) {
                        System.err.println("Logout warning: Server returned " + response.statusCode());
                    } else {
                        System.out.println("Server session invalidated.");
                    }
                })
                .exceptionally(e -> {
                    System.err.println("Logout warning: Server unreachable");
                    return null;
                });
    }

    private <T> CompletableFuture<T> sendRequest(HttpRequest request, Class<T> responseType) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 300) {
                        System.err.println("❌ API Error URL: " + request.uri());
                        System.err.println("❌ Status Code: " + response.statusCode());
                        System.err.println("❌ Response Body: " + response.body()); // <--- ВОТ ЭТО САМОЕ ВАЖНОЕ
                        return null; // Возвращаем null, чтобы вызвать ошибку на UI
                    }
                    return gson.fromJson(response.body(), responseType);
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
}