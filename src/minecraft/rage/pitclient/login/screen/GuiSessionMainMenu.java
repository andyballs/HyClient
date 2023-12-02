package rage.pitclient.login.screen;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.PitClient;

public class GuiSessionMainMenu extends GuiMainMenu {

	private GuiButtonSessionManager sessionButton;
	
	@Override
    public void initGui() {
		super.initGui();
		sessionButton = new GuiButtonSessionManager(15, width / 2 - 124, height / 4 + 72);
		buttonList.add(sessionButton);
    }
	
    @Override
	public void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 15) {
			mc.displayGuiScreen(PitClient.guiSessionManager);
		}
		super.actionPerformed(button);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		String s = "Current IGN: " + EnumChatFormatting.RED + mc.getSession().getUsername();
		
		this.drawString(fontRendererObj, s, width/2 - fontRendererObj.getStringWidth("Current IGN: ")+2, height/4 + 120, 16777215);
		
	}
}
