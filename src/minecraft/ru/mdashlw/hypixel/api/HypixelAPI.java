package ru.mdashlw.hypixel.api;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.mdashlw.hypixel.api.data.Guild;
import ru.mdashlw.hypixel.api.data.Player;
import ru.mdashlw.hypixel.api.exception.HypixelApiException;
import ru.mdashlw.hypixel.api.util.JsonUtils;

public final class HypixelAPI {

    private final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private final HttpClient httpClient = HttpClients.custom().setMaxConnTotal(24).setMaxConnPerRoute(24).build();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private String apiKey;

    public HypixelAPI(String apiKey) {
        this.apiKey = apiKey;
    }

    public CompletableFuture<Player> getPlayerByNameAsync(String name) {
        return CompletableFuture.supplyAsync(() -> {
            return this.getPlayerByName(name);
        }, this.executor);
    }

    public Player getPlayerByName(String name) {
        HttpGet request = new HttpGet("https://api.hypixel.net/player?key=" + this.apiKey + "&name=" + name);

        try {
            CloseableHttpResponse exception = (CloseableHttpResponse) this.httpClient.execute(request);
            Throwable throwable = null;

            Player player;

            try {
                JsonNode data = this.objectMapper.readTree(exception.getEntity().getContent());

                if (!data.get("success").asBoolean()) {
                    throw new HypixelApiException(JsonUtils.getOptionalText(data, "cause", "no cause"));
                }

                JsonNode playerData = data.get("player");

                if (playerData == null || playerData.isNull()) {
                    player = null;
                    return player;
                }

                player = new Player(playerData);
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (exception != null) {
                    if (throwable != null) {
                        try {
                            exception.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        exception.close();
                    }
                }

            }

            return player;
        } catch (IOException ioexception) {
            throw new UncheckedIOException(ioexception);
        }
    }

    public CompletableFuture<Guild> getGuildByPlayerAsync(String player) {
        return CompletableFuture.supplyAsync(() -> {
            return this.getGuildByPlayer(player);
        }, this.executor);
    }

    public Guild getGuildByPlayer(String player) {
        HttpGet request = new HttpGet("https://api.hypixel.net/guild?key=" + this.apiKey + "&player=" + player);

        try {
            CloseableHttpResponse exception = (CloseableHttpResponse) this.httpClient.execute(request);
            Throwable throwable = null;

            Guild guild;

            try {
                JsonNode data = this.objectMapper.readTree(exception.getEntity().getContent());

                if (!data.get("success").asBoolean()) {
                    throw new HypixelApiException(JsonUtils.getOptionalText(data, "cause", "no cause"));
                }

                JsonNode guildData = data.get("guild");

                if (guildData == null || guildData.isNull()) {
                    guild = null;
                    return guild;
                }

                guild = new Guild(guildData);
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (exception != null) {
                    if (throwable != null) {
                        try {
                            exception.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        exception.close();
                    }
                }

            }

            return guild;
        } catch (IOException ioexception) {
            throw new UncheckedIOException(ioexception);
        }
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
