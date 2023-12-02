package rage.pitclient.module.modules.other;

import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.TickEvent.ClientTickEvent;
import rage.pitclient.events.TickEvent.Phase;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;

@ModuleInfo(name = "Inventory Cleaner", tooltip = "Automatically drops selected items out of your inventory", category = Category.OTHER)
public class InvCleaner extends Module {

	private Setting dropIron = regBoolean("Iron Armor", true);
	private Setting dropWheat = regBoolean("Wheat", true);	
	private Setting dropDiamond = regBooleanTip("Diamond Armor", true, "Ignores leggings and boots");
	private Setting requireStreak = regBooleanTip("Require Streak", true, "Requires a streak to drop items");

	private int ticks;
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (!enabled)
			return;
		if (event.phase != Phase.END)
			return;
		if (mc.theWorld == null)
			return;
		
		ticks++;
		
		if (ticks % 20 == 0) {
			
			boolean flag = false;
			Scoreboard scoreboard = mc.theWorld.getScoreboard();
			
			if (scoreboard == null) return;
			
	        for (ScorePlayerTeam team : scoreboard.getTeams()) {
	        	if (team.getTeamName() == null) continue;
	        	if (!team.getTeamName().startsWith("team_")) continue;
	        	if ((team.getColorPrefix() + " " + team.getColorSuffix()).contains("Streak")) {
	        		flag = true;
	        	}
	        }
	        
	        if (requireStreak.getValBoolean() && !flag) return;
    		
	        int i = -1;
			for (ItemStack item : mc.thePlayer.inventory.mainInventory) {
				i++;
				if (item == null) continue;
				if (item.getItem() instanceof ItemArmor) {
					if (dropIron.getValBoolean() && item.getUnlocalizedName().contains("Iron")) {
						drop(item, i);
					}
					if (dropDiamond.getValBoolean() && item.getUnlocalizedName().contains("Diamond") && !item.getUnlocalizedName().contains("leggings") && !item.getUnlocalizedName().contains("boots")) {
						drop(item, i);
					}
				}
				if (item.getUnlocalizedName().equals("item.wheat") && dropWheat.getValBoolean()) {
					drop(item, i);
				}
				
			}
		}
	}
	
	private void drop(ItemStack item, int i) {
		PitClient.commandManager.sendModuleMessage(this,EnumChatFormatting.GOLD + "Dropping " + EnumChatFormatting.RESET + item.getDisplayName());
		
		int slot = (9>i && i>-1) ? i+36 : i;
		mc.playerController.windowClick(0, slot, 1, 4, mc.thePlayer);
	}
}
