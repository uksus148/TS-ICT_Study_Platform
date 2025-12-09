package com.synapse.client.store;

import com.google.gson.*;
import com.synapse.client.model.User;

import java.lang.reflect.Type;

public class MembershipDeserializer implements JsonDeserializer<User> {
    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json == null || !json.isJsonObject()) return null;
        JsonObject memberObj = json.getAsJsonObject();

        if (memberObj.has("user") && memberObj.get("user").isJsonObject()) {
            JsonObject userJson = memberObj.get("user").getAsJsonObject();

            // Создаем клиента User и заполняем его данными из вложенного объекта
            User user = new User();

            if (has(userJson, "id")) user.setUser_id(userJson.get("id").getAsLong());
            else if (has(userJson, "userId")) user.setUser_id(userJson.get("userId").getAsLong());

            if (has(userJson, "name")) user.setName(userJson.get("name").getAsString());
            if (has(userJson, "email")) user.setEmail(userJson.get("email").getAsString());

            // (Опционально) Можно сохранить Роль где-то, но в модели User для этого может не быть поля.
            // String role = memberObj.get("role").getAsString();

            return user;
        }

        return null; // Если в Membership нет юзера
    }

    private boolean has(JsonObject obj, String memberName) {
        return obj.has(memberName) && !obj.get(memberName).isJsonNull();
    }
}