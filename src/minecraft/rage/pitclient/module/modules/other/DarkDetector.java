package rage.pitclient.module.modules.other;

import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.TickEvent.ClientTickEvent;
import rage.pitclient.events.TickEvent.Phase;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;

@ModuleInfo(name = "Dark Detector", tooltip = "Detects players using Dark Pants in your lobby", category = Category.OTHER)
public class DarkDetector extends Module {

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
			
			Iterator<?> iter = PitClient.getInstance().getManager().darkMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> user = (Entry<String, String>) iter.next();
				if (mc.theWorld.getPlayerEntityByName(user.getKey()) == null) {
					PitClient.commandManager.sendModuleMessage(this, EnumChatFormatting.GOLD + "Darks left! " + EnumChatFormatting.AQUA + user.getKey());

					iter.remove();
				}
			}
			
			mc.theWorld.playerEntities.forEach(player -> {
				ItemStack pants = player.getCurrentArmor(1);
				if (pants == null) return;
				
				String name = pants.getTooltip(player, false).get(0);
				int index = player.getDisplayName().getFormattedText().indexOf(player.getName());
				if (index >= 2) {
					String playerName = "\u00A7"+player.getDisplayName().getFormattedText().replace(" ", "").substring(index-2, index + player.getName().length()-1);

					if (name.contains("Tier II Dark Pants") | name.contains("Tier II Evil Pants")) {
						if (!PitClient.getInstance().getManager().darkMap.containsKey(player.getName())) {
							
							String dark = pants.getTagCompound().getCompoundTag("ExtraAttributes").getTagList("CustomEnchants", 10).getCompoundTagAt(1).getString("Key");
							String displayName = "\u00A7" + Dark.getDisplayFromName(dark);
							
							PitClient.commandManager.sendModuleMessage(this, EnumChatFormatting.GOLD + "Darks joined! " + displayName + EnumChatFormatting.RESET + " - " + playerName);
							PitClient.getInstance().getManager().darkMap.put(player.getName(), displayName);
						}
					} else if (PitClient.getInstance().getManager().darkMap.containsKey(player.getName())) {
						PitClient.commandManager.sendModuleMessage(this, EnumChatFormatting.GOLD + "Took off darks " + playerName);
						PitClient.getInstance().getManager().darkMap.remove(player.getName());
					}
				}
			});
		}
	}
	
	private enum Dark {
		SPITE             ("spite","cSpite"),
	    SANGUISUGE        ("sanguisuge", "dSanguisuge"),
	    MISERY            ("misery", "cMisery"),
	    NEEDLESS_SUFFERING("needless_suffering", "dNeedless Suffering"),
	    MIND_ASSAULT      ("mind_assault", "dMind Assault"),
	    HEDGE_FUND        ("hedge_fund", "dHedge Fund"),
	    GOLDEN_HANDCUFFS  ("golden_handcuffs", "eGolden Handcuffs"),
	    HEARTRIPPER       ("heartripper", "dHeartripper"),
	    VENOM             ("venom", "aVenom"),
	    NOSTALGIA         ("nostalgia", "dNostalgia"),
	    LYCANTHROPY       ("lycanthropy", "dLycanthropy"),
	    GRIM_REAPER       ("grim_reaper", "dGrim Reaper");

		String name;
		String displayName;
		
		Dark(String name, String display) {
			this.name = name;
			this.displayName = display;
		}
		
		public static String getDisplayFromName(String inputName) {
			for (Dark dark : Dark.values()) {
				if (dark.name.equals(inputName)) {
					return dark.displayName;
				}
			}
			return "";
		}
	}
}
