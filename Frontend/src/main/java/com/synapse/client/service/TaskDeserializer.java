package com.synapse.client.service;

import com.google.gson.*;
import com.synapse.client.TaskStatus;
import com.synapse.client.model.Task;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class TaskDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Task task = new Task();

        if (has(jsonObject, "id")) task.setTask_id(jsonObject.get("id").getAsLong());
        if (has(jsonObject, "title")) task.setTitle(jsonObject.get("title").getAsString());
        if (has(jsonObject, "description")) task.setDescription(jsonObject.get("description").getAsString());

        if (has(jsonObject, "status")) {
            String statusStr = jsonObject.get("status").getAsString();
            try {
                task.setStatus(TaskStatus.valueOf(statusStr));
            } catch (Exception e) {
                task.setStatus(TaskStatus.IN_PROGRESS); // Если пришел неизвестный статус
            }
        }

        if (has(jsonObject, "studyGroup")) {
            JsonObject groupObj = jsonObject.get("studyGroup").getAsJsonObject();
            if (has(groupObj, "groupId")) {
                task.setGroup_id(groupObj.get("groupId").getAsLong());
            }
        }

        if (has(jsonObject, "createdBy")) {
            JsonObject userObj = jsonObject.get("createdBy").getAsJsonObject();
            if (has(userObj, "user_id")) {
                task.setCreated_by(userObj.get("user_id").getAsLong());
            }
        }

        if (has(jsonObject, "deadline")) {
            task.setDeadline(parseDate(jsonObject.get("deadline")));
        }
        if (has(jsonObject, "createdAt")) {
            task.setCreated_at(parseDateTime(jsonObject.get("createdAt")));
        }

        return task;
    }

    private boolean has(JsonObject obj, String memberName) {
        return obj.has(memberName) && !obj.get(memberName).isJsonNull();
    }

    private LocalDate parseDate(JsonElement element) {
        try {
            long millis = element.getAsLong();
            return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            try {
                return LocalDate.parse(element.getAsString().substring(0, 10));
            } catch (Exception ex) { return null; }
        }
    }

    private LocalDateTime parseDateTime(JsonElement element) {
        try {
            long millis = element.getAsLong();
            return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(element.getAsString());
            } catch (Exception ex) { return null; }
        }
    }
}