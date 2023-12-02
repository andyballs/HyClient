package rage.pitclient.module.modules.combat;

import org.lwjgl.input.Mouse;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.InputEvent.MouseInputEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;

@ModuleInfo(name = "Middle Click Friend", category = Category.COMBAT)
public class MiddleClickFriend extends Module {

	
	@SubscribeEvent
	public void onClick(MouseInputEvent event) {
		if (!enabled)
			return;
		if (Mouse.getEventButton() == 2 && Mouse.getEventButtonState()) {
			if (!(mc.objectMouseOver.entityHit instanceof EntityPlayer)) return;
			String name = mc.objectMouseOver.entityHit.getName();
			
			if (PitClient.friendManager.addOrRemoveFriend(name)) {
				PitClient.commandManager.sendModuleMessage(this, EnumChatFormatting.GOLD + name + EnumChatFormatting.GREEN + " has been added as a friend");
			} else {
				PitClient.commandManager.sendModuleMessage(this, EnumChatFormatting.GOLD + name + EnumChatFormatting.RED + " has been removed as a friend");
			}
		}
	}

}
