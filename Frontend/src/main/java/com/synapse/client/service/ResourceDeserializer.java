package com.synapse.client.service;

import com.google.gson.*;
import com.synapse.client.model.Resource;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class ResourceDeserializer implements JsonDeserializer<Resource> {

    @Override
    public Resource deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull() || !json.isJsonObject()) {
            return null;
        }

        JsonObject obj = json.getAsJsonObject();
        Resource resource = new Resource();

        if (has(obj, "id")) resource.setResource_id(obj.get("id").getAsLong());
        else if (has(obj, "resourceId")) resource.setResource_id(obj.get("resourceId").getAsLong());
        else if (has(obj, "resource_id")) resource.setResource_id(obj.get("resource_id").getAsLong());

        if (has(obj, "title")) {
            resource.setName(obj.get("title").getAsString());
        } else if (has(obj, "name")) {
            resource.setName(obj.get("name").getAsString());
        }

        if (has(obj, "type")) {
            resource.setType(obj.get("type").getAsString());
        }

        if (has(obj, "pathOrUrl")) {
            resource.setPath(obj.get("pathOrUrl").getAsString());
        } else if (has(obj, "path")) {
            resource.setPath(obj.get("path").getAsString());
        }

        if (has(obj, "uploadedBy")) {
            JsonElement userElement = obj.get("uploadedBy");
            if (userElement.isJsonObject()) {
                JsonObject userObj = userElement.getAsJsonObject();
                if (has(userObj, "id")) resource.setCreated_by(userObj.get("id").getAsLong());
                else if (has(userObj, "userId")) resource.setCreated_by(userObj.get("userId").getAsLong());
            }
        }

        if (has(obj, "studyGroup")) {
            JsonElement groupElement = obj.get("studyGroup");
            if (groupElement.isJsonObject()) {
                JsonObject groupObj = groupElement.getAsJsonObject();
                if (has(groupObj, "groupId")) resource.setGroup_id(groupObj.get("groupId").getAsLong());
                else if (has(groupObj, "id")) resource.setGroup_id(groupObj.get("id").getAsLong());
            }
        }

        if (has(obj, "uploadedAt")) {
            resource.setCreated_at(parseDateTime(obj.get("uploadedAt")));
        } else if (has(obj, "createdAt")) { // На всякий случай
            resource.setCreated_at(parseDateTime(obj.get("createdAt")));
        }

        return resource;
    }

    private boolean has(JsonObject obj, String memberName) {
        return obj.has(memberName) && !obj.get(memberName).isJsonNull();
    }

    private LocalDateTime parseDateTime(JsonElement element) {
        try {
            long millis = element.getAsLong();
            return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(element.getAsString());
            } catch (Exception ex) {
                return null;
            }
        }
    }
}