package rage.pitclient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.module.Module;
import rage.pitclient.packets.PacketEvent;
import rage.pitclient.packets.WrappedS3EPacketTeams;
import rage.pitclient.packets.WrappedS3EPacketTeams.Function;

public class ScoreboardManager {

	private Scoreboard scoreboard;
	
	public ScoreboardManager() {
		this.scoreboard = new Scoreboard();
	}
    
	public ArrayList<String> wdrList = new ArrayList<String>();
	public ArrayList<String> npcList = new ArrayList<String>();
	
	public ArrayList<ScorePlayerTeam> vanishedTeams = new ArrayList<>();
	public ArrayList<String> vanishedNames = new ArrayList<>();
	public HashMap<ScorePlayerTeam, HashMap<String,UUID>> vanishedUuids = new HashMap<>();

	@SubscribeEvent
	public void onRecieve(PacketEvent.Incoming.Pre event) {
		if (!PitClient.getClientUser().beta)
			return;
		if (event.getPacket() instanceof S3EPacketTeams) {
			WrappedS3EPacketTeams packet = new WrappedS3EPacketTeams((S3EPacketTeams) event.getPacket());
			
			S3EPacketTeams packetIn = (S3EPacketTeams) event.getPacket();
			
	        ScorePlayerTeam scoreplayerteam;
	        
	        if (packet.getChatColor() == EnumChatFormatting.RED) wdrList.add(packet.getFirstPlayer());	        
	        
	        if (packetIn.func_149307_h() == 0)
	        {
	        	scoreplayerteam = scoreboard.createTeam(packetIn.func_149312_c());
	        }
	        else
	        {
	            scoreplayerteam = scoreboard.getTeam(packetIn.func_149312_c());
	        }
	        
	        Object[] nm = scoreplayerteam.getMembershipCollection().toArray();
	        if (packet.getFunction() == Function.ADD_PLAYER && nm.length>0 && !npcList.contains((String)nm[0]) && ScorePlayerTeam.formatPlayerName(scoreplayerteam, (String) nm[0]).contains("[NPC]")) {
	        	npcList.add((String) nm[0]);
	        }
	        
	        if (packetIn.func_149307_h() == 0 || packetIn.func_149307_h() == 2)
	        {
	            scoreplayerteam.setTeamName(packetIn.func_149306_d());
	            scoreplayerteam.setNamePrefix(packetIn.func_149311_e());
	            scoreplayerteam.setNameSuffix(packetIn.func_149309_f());
	            scoreplayerteam.setChatFormat(EnumChatFormatting.func_175744_a(packetIn.func_179813_h()));
	            scoreplayerteam.func_98298_a(packetIn.func_149308_i());
	            Team.EnumVisible team$enumvisible = Team.EnumVisible.func_178824_a(packetIn.func_179814_i());

	            if (team$enumvisible != null)
	            {
	                scoreplayerteam.setNameTagVisibility(team$enumvisible);
	            }
	        }
	        
//	        if (scoreplayerteam.getNameTagVisibility() == EnumVisible.NEVER && packet.getFunction() == Function.ADD_PLAYER | packet.getFunction() == Function.CREATE_TEAM) {
//	        	npcList.add(packet.getFirstPlayer());
//	        }
	        
	        if (scoreplayerteam.getColorPrefix().contains("[SPEC")) {	        	
	        	if (packet.getFunction() == Function.ADD_PLAYER) {
	        		
		        	if (PitClient.getInstance().getManager().vanishMacro) {
		        		Minecraft.getMinecraft().thePlayer.sendChatMessage("/l");
		        	}
		        	
		        	for (Module mod : PitClient.moduleManager.getModules()) {
		        		if (mod.getDisableOnVanish() && mod.isEnabled()) PitClient.moduleManager.toggle(mod);
		        	}
	        		
					UUID uuid = MinecraftServer.getServer().getPlayerProfileCache().getGameProfileForUsername(packet.getFirstPlayer()).getId();
					
					Minecraft.getMinecraft().thePlayer.sendQueue.playerInfoMap.put(uuid, new NetworkPlayerInfo(new GameProfile(uuid , ScorePlayerTeam.formatPlayerName(scoreplayerteam, packet.getFirstPlayer()))));
					
					HashMap<String,UUID> mp = new HashMap<>();
					mp.put(packet.getFirstPlayer(), uuid);
					vanishedTeams.add(scoreplayerteam);
					vanishedUuids.put(scoreplayerteam, mp);
					vanishedNames.add(packet.getFirstPlayer());
					
					PitClient.commandManager.sendMessageWithPrefix(EnumChatFormatting.AQUA + "VANISHED " + EnumChatFormatting.GOLD + packet.getFirstPlayer());
				
	        	}
	        	
	        	if (packet.getFunction() == Function.REMOVE_PLAYER | packet.getFunction() == Function.REMOVE_TEAM) {
	        		vanishedTeams.remove(scoreplayerteam);
        			String name = (String) scoreplayerteam.getMembershipCollection().toArray()[0];
        			if (vanishedUuids.get(scoreplayerteam) != null) 
        				Minecraft.getMinecraft().thePlayer.sendQueue.playerInfoMap.remove(vanishedUuids.get(scoreplayerteam).get(name));
					
					vanishedTeams.remove(scoreplayerteam);
					vanishedUuids.remove(scoreplayerteam);
					vanishedNames.remove(name);
					String msg = ScorePlayerTeam.formatPlayerName(scoreplayerteam, name);
					msg = EnumChatFormatting.AQUA + "VANISHED " + name;
	        		PitClient.commandManager.sendMessageWithPrefix( msg + EnumChatFormatting.AQUA + " Left.");
	        	}
	        }
	        
	        if (packetIn.func_149307_h() == 0 || packetIn.func_149307_h() == 3)
	        {
	            for (String s : packetIn.func_149310_g())
	            {
	                scoreboard.addPlayerToTeam(s, packetIn.func_149312_c());
	            }
	        }

	        if (packetIn.func_149307_h() == 4)
	        {
	            for (String s1 : packetIn.func_149310_g())
	            {
	                scoreboard.removePlayerFromTeam(s1, scoreplayerteam);
	            }
	        }

	        if (packetIn.func_149307_h() == 1)
	        {
	            scoreboard.removeTeam(scoreplayerteam);
	        }
		}
	}
}
