package com.synapse.client.service;

import com.google.gson.*;
import com.synapse.client.model.Group;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class GroupDeserializer implements JsonDeserializer<Group> {
    @Override
    public Group deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull() || !json.isJsonObject()) {
            return null;
        }

        JsonObject jsonObject = json.getAsJsonObject();
        Group group = new Group();

        if (has(jsonObject, "groupId")) group.setGroup_id(jsonObject.get("groupId").getAsLong());
        else if (has(jsonObject, "id")) group.setGroup_id(jsonObject.get("id").getAsLong());
        else if (has(jsonObject, "group_id")) group.setGroup_id(jsonObject.get("group_id").getAsLong());

        if (has(jsonObject, "name")) group.setName(jsonObject.get("name").getAsString());
        if (has(jsonObject, "description")) group.setDescription(jsonObject.get("description").getAsString());

        if (has(jsonObject, "createdBy")) {
            JsonElement createdByElement = jsonObject.get("createdBy");
            if (createdByElement.isJsonObject()) {
                JsonObject userObj = createdByElement.getAsJsonObject();

                if (has(userObj, "userId")) {
                    group.setCreated_by(userObj.get("userId").getAsLong());
                } else if (has(userObj, "id")) {
                    group.setCreated_by(userObj.get("id").getAsLong());
                }
            } else if (createdByElement.isJsonPrimitive()) {
                group.setCreated_by(createdByElement.getAsLong());
            }
        }

        if (has(jsonObject, "createdAt")) {
            group.setCreated_at(parseDateTime(jsonObject.get("createdAt")));
        }

        return group;
    }

    private boolean has(JsonObject obj, String memberName) {
        return obj.has(memberName) && !obj.get(memberName).isJsonNull();
    }

    private LocalDateTime parseDateTime(JsonElement element) {
        try {
            long millis = element.getAsLong();
            return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            try { return LocalDateTime.parse(element.getAsString()); } catch (Exception ex) { return null; }
        }
    }
}