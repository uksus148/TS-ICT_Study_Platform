package com.synapse.client.service;

import com.google.gson.*;
import com.synapse.client.model.Group;
import com.synapse.client.model.Resource;
import com.synapse.client.model.Task;
import com.synapse.client.model.User;
import com.synapse.client.store.MembershipDeserializer;

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

    private static final String BASE_URL = "http://localhost:8080/api";

    private ApiService() {
        this.client = HttpClient.newHttpClient();

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
                .uri(URI.create(BASE_URL + "/studyGroups"))
                .GET()
                .build();

        return sendRequest(request, Group[].class);
    }

    public CompletableFuture<Group> createGroup(Group group) {
        Long userId = group.getCreated_by();
        if (userId == null) userId = 1L;
        // TODO: Remove after add profile

        String json = gson.toJson(group);
        String requestUrl = BASE_URL + "/studyGroups/" + userId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return sendRequest(request, Group.class);
    }

    public CompletableFuture<Group> updateGroup(Group group) {
        String json = gson.toJson(group);
        String requestUrl = BASE_URL + "/studyGroups/" + group.getGroup_id();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return sendRequest(request, Group.class);
    }

    public CompletableFuture<Void> deleteGroup(Long groupId) {
        String requestUrl = BASE_URL + "/studyGroups/" + groupId;

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
                .uri(URI.create(BASE_URL + "/tasks"))
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
        String requestUrl = BASE_URL + "/tasks/" + userId + "/" + groupId;

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
                .uri(URI.create(BASE_URL + "/tasks/" + task.getTask_id())) // ID в URL
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return sendRequest(request, Task.class);
    }

    public CompletableFuture<User[]> getGroupMembers(Long groupId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/memberships/group/" + groupId))
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
                .uri(URI.create(BASE_URL + "/tasks/" + id))
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
        String requestUrl = BASE_URL + "/resources/" + userId + "/" + groupId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return sendRequest(request, Resource.class);
    }

    public CompletableFuture<Resource[]> getGroupResources(Long groupId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/resources/group/" + groupId))
                .GET()
                .build();

        return sendRequest(request, Resource[].class);
    }

    public CompletableFuture<User> registerUser(User user) {
        String json = gson.toJson(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/auth/register"))
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

    private <T> CompletableFuture<T> sendRequest(HttpRequest request, Class<T> responseType) {
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() >= 300) {
                        System.err.println("Server Error: " + response.body());
                        return null;
                    }
                    return gson.fromJson(response.body(), responseType);
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    return null;
                });
    }
}