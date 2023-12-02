package rage.pitclient;

import java.util.Arrays;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

public class ClientUser {
	
	public String username;
	public int uid;
	public String discordid;
	public JsonArray permissions;
	public String hwid;
	
	public boolean beta;
	
	public ClientUser(String username, int uid, String discordid, JsonArray permissions, String hwid) {
		this.username = username;
		this.uid = uid;
		this.discordid = discordid;
		this.permissions = permissions;
		this.hwid = hwid;
		this.beta = hasBeta();
	}
	
	/*
	 * 
	 * USER - 0
	 * BETA - 1
	 * MOD - 2
	 * ADMIN - 3
	 * OWNER - 4
	 * 
	 */
	
	private String[] perms = { "USER", "BETA", "MODERATOR", "ADMIN", "OWNER" };
	
	
	public int getHighestPerm() {
		int max = 0;
		for (JsonElement perm : permissions) {
			for (int i=0; i < perms.length; i++) {
				if (perm.getAsString().equalsIgnoreCase(perms[i]) && i > max) max = i;
			}
		}
		return max;
	}
	
	private boolean hasBeta() {
		for (JsonElement perm : permissions) {
			if (perm.getAsString().equalsIgnoreCase("beta")) return true;
		}
		return false;
	}
}
