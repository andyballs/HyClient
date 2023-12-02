package rage.pitclient.packets;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.Team.EnumVisible;
import net.minecraft.util.EnumChatFormatting;

public class WrappedS3EPacketTeams {
	
	S3EPacketTeams packetIn;
	
	public WrappedS3EPacketTeams(S3EPacketTeams packetIn) {
		this.packetIn = packetIn;
	}
	
	public String getRegisteredName() {
		return packetIn.func_149312_c();
	}
	
	public String getTeamName() {
		return packetIn.func_149306_d();
	}
	
	public String getNamePrefix() {
		return packetIn.func_149311_e();
	}
	
	public String getNameSuffix() { 
		return packetIn.func_149309_f();
	}
	
	public EnumChatFormatting getChatColor() { 
		return EnumChatFormatting.func_175744_a(packetIn.func_179813_h());
	}
	
	public int getColorIndex() {
		return packetIn.func_179813_h();
	}
	
	public boolean getFriendlyFireEnabled() { 
		return (packetIn.func_149308_i() & 1) > 0;
	}
	
	public EnumVisible getVisibility() {
		return Team.EnumVisible.func_178824_a(packetIn.func_179814_i());
	}
	
	public ArrayList<String> getPlayersArray() {
		ArrayList<String> set = new ArrayList<String>();
		
		packetIn.func_149310_g().forEach(set::add);
		
		return set;
	}
	
	public Collection<String> getPlayers() {
		return packetIn.func_149310_g();
	}
	
	public String getFirstPlayer() {
		if (getPlayers().size() == 0) return "";
		return getPlayersArray().get(0);
	}
	
	public Function getFunction() {
		if (packetIn.func_149307_h() >= 0 && packetIn.func_149307_h() <= 4) {
			return Function.values()[packetIn.func_149307_h()];
		}
		return Function.UNKNOWN;
	}
	

	public enum Function {
		CREATE_TEAM,
		REMOVE_TEAM,
		UPDATE_TEAM,
		ADD_PLAYER,
		REMOVE_PLAYER,
		UNKNOWN;
	}
}
