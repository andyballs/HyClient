package ru.mdashlw.hypixel.api.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;

public final class JsonUtils {

    public static JsonNode getOptionalObject(JsonNode data, String field) {
        JsonNode node = data.get(field);

        return (JsonNode) (node == null ? JsonNodeFactory.instance.objectNode() : node);
    }

    public static String getOptionalText(JsonNode data, String field) {
        return getOptionalText(data, field, (String) null);
    }

    public static String getOptionalText(JsonNode data, String field, String fallback) {
        JsonNode node = data.get(field);

        return node == null ? fallback : node.asText();
    }

    public static int getOptionalInt(JsonNode data, String field) {
        JsonNode node = data.get(field);

        return node == null ? 0 : node.asInt();
    }

    public static long getOptionalLong(JsonNode data, String field) {
        JsonNode node = data.get(field);

        return node == null ? 0L : node.asLong();
    }

    public static double getOptionalDouble(JsonNode data, String field) {
        JsonNode node = data.get(field);

        return node == null ? 0.0D : node.asDouble();
    }

    public static byte[] getByteArray(JsonNode data, String field) {
        JsonNode node = data.get(field);

        if (node != null && node.isArray()) {
            byte[] bytes = new byte[node.size()];

            for (int i = 0; i < node.size(); ++i) {
                bytes[i] = (byte) node.get(i).asInt();
            }

            return bytes;
        } else {
            return null;
        }
    }
}
