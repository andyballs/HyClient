package rage.pitclient;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class HypixelApi {
	private Gson gson = new Gson();
	private ExecutorService es = Executors.newFixedThreadPool(3);

	public void getHypixelApiAsync(String apiKey, String method, HashMap<String, String> args,
			Consumer<JsonObject> consumer) {
		getHypixelApiAsync(apiKey, method, args, consumer, () -> {

		});
	}

	public void getHypixelApiAsync(String apiKey, String method, HashMap<String, String> args,
			Consumer<JsonObject> consumer, Runnable error) {
		getApiAsync(generateApiUrl(apiKey.trim(), method, args), consumer, error);
	}

	public void getApiAsync(String urlS, Consumer<JsonObject> consumer, Runnable error) {
		this.es.submit(() -> {
			try {
				consumer.accept(getApiSync(urlS));
			} catch (Exception e) {
				error.run();
			}
		});
	}

	public void getPitPandaAsync(Integer nonce, Consumer<JsonObject> consumer) {
		getPitPandaAsync(nonce, consumer, () -> {
		});
	}

	public void getPitPandaAsync(Integer nonce, Consumer<JsonObject> consumer, Runnable error) {
		getPitPandaAsync("https://pitpanda.rocks/api/itemsearch/nonce" + Integer.toString(nonce), consumer, error, "");
	}

	public void getPitPandaAsync(String urlS, Consumer<JsonObject> consumer, Runnable error, String r) {
		es.submit(() -> {
			try {
				consumer.accept(getPandaApiSync(urlS));
			} catch (Exception e) {
				e.printStackTrace();
				error.run();
			}
		});
	}

	public JsonObject getPandaApiSync(String urlS) throws IOException {
		URL url = new URL(urlS);
		URLConnection connection = url.openConnection();
		connection.setRequestProperty("User-Agent",
				"Mozilla 5.0 (Windows; U; " + "Windows NT 5.1; en-US; rv:1.8.0.11) ");
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		String response = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
		JsonObject json = (JsonObject) this.gson.fromJson(response, JsonObject.class);
		return json;
	}

	public JsonObject getApiSync(String urlS) throws IOException {
		URL url = new URL(urlS);
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		String response = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);
		JsonObject json = (JsonObject) this.gson.fromJson(response, JsonObject.class);
		return json;
	}

	public String generateApiUrl(String apiKey, String method, HashMap<String, String> args) {
		StringBuilder url = new StringBuilder("https://api.hypixel.net/" + method + "?key=" + apiKey);
		for (Map.Entry<String, String> entry : args.entrySet()) {
			url.append("&").append(entry.getKey()).append("=").append(entry.getValue());
		}
		return url.toString();
	}
}