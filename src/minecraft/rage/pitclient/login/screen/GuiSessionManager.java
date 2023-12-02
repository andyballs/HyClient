package rage.pitclient.login.screen;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import com.thealtening.auth.TheAlteningAuthentication;
import com.thealtening.auth.service.AlteningServiceType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonExt;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSliderExt;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import rage.pitclient.PitClient;
import rage.pitclient.login.Yggdrasil;

public class GuiSessionManager extends GuiScreen {
	
	private GuiButtonExt exitButton;
	private GuiButtonExt applyButton;
	private GuiButtonExt importButton;
	private GuiButtonExt authButton;
	
	private GuiSliderExt ratSlider;

	private GuiTextField nameField;
	private GuiTextField sessionField;
	
	public String name;
	public String session;
	
	private List list;
	
	private int nameLen = 250;
	private int sessionLen = 250;
	
	private TheAlteningAuthentication authService = TheAlteningAuthentication.mojang();
		
    public void initGui() {
    	this.drawDefaultBackground();
		exitButton = new GuiButtonExt(0, width / 2 - 100, height - 40, 200, 20, "Exit");
		applyButton = new GuiButtonExt(1, width / 2 - 50, height/2, 100, 20, "Apply Session");
		importButton = new GuiButtonExt(3, width / 2 - 25, height/2 + 25, 50, 20, "Import");
		authButton = new GuiButtonExt(4, width / 2 + 150, height - 40, 80, 20, authButton == null ? "Mojang Auth" : authButton.displayString);

		ratSlider = new GuiSliderExt(2, width / 2 - 250, height - 40, 100, 20, "Rat Speed: ", "", 1, 11, (ratSlider == null ? 7.0 : ((double)Math.round(ratSlider.getValue() * 10))/10), true, true);
		
		buttonList.add(ratSlider);
		buttonList.add(exitButton);
		buttonList.add(applyButton);
		buttonList.add(importButton);
		buttonList.add(authButton);

		list = new List(mc);
		ratList.clear();
		for (int i=0;i<=3;i++) ratList.add(new ResourceLocation("textures/gui/rat/rat"+i+"1.png"));

		nameField = new GuiTextField(-1, fontRendererObj, width/2 - nameLen/2, height / 2 - 40 - 7 - 30, nameLen, 14);
		sessionField = new GuiTextField(-1, fontRendererObj, width/2 - sessionLen/2, height / 2 - 40, sessionLen, 14);
		nameField.setFocused(true);
		nameField.setMaxStringLength(48);
		sessionField.setMaxStringLength(308);
		if (name != null) nameField.setText(name);
		if (session != null) sessionField.setText(session);
    }
	
	public void actionPerformed(GuiButton button) throws IOException {

		if (button.id == ratSlider.id) {
			RAT_SPEED = (12 - ratSlider.getValueInt());
		}
		
		if (button.id == authButton.id) {
			
			switch (authService.getService()) {
			case MOJANG:
				authService.updateService(AlteningServiceType.THEALTENING);
				authButton.displayString = "Altening Auth";
				break;
			case THEALTENING:
				authService.updateService(AlteningServiceType.MOJANG);
				authButton.displayString = "Mojang Auth";
				break;			
			}
			
		}
		
		if (button.id == exitButton.id) {
			mc.displayGuiScreen(new GuiSessionMainMenu());
		}
		
		if (button.id == applyButton.id) {
			login();
		}
		
		if (button.id == importButton.id) {
			try {
				String clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
				String[] split = clipboard.split(":");
				if (split.length != 2) {
					error("Invalid Clipboard");
					return;
				}
				nameField.setText(split[0]);
				sessionField.setText(split[1]);
				
				login();
				
			} catch (HeadlessException | UnsupportedFlavorException | IOException e) {
				error("Clipboard needs to be a string");
			}
		}
	}
	
	private void login() {
		name = nameField.getText();
		session = sessionField.getText();
		if (name != null && session != null) {
			if (session.length() == 308) {
				PitClient.setSession(name, session);
				mc.displayGuiScreen(new GuiSessionMainMenu());
			} else {
				String[] ret = Yggdrasil.login(name, session);
				if (ret.length < 3) {
					error(ret[0]);
				} else {
					System.out.println(ret[0]);
					System.out.println(ret[1]);
					System.out.println(ret[2]);
					Minecraft.getMinecraft().session = new Session(ret[0], ret[1], ret[2], "mojang");
					mc.displayGuiScreen(new GuiSessionMainMenu());
				}
			}
		}
	}
	
	private String CURRENT_ERROR;
	private long errorTime;
	
	private void error(String error) {
		CURRENT_ERROR = error;
		errorTime = System.currentTimeMillis() + 2000;
	}
    
	private String NAME_STRING = "Username or UUID:";
	private String SESSION_STRING = "Session or Password:";
	
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        list.drawScreen(mouseX, mouseY, partialTicks);
        drawRat();
        nameField.drawTextBox();
        sessionField.drawTextBox();
        
        this.drawCenteredString(fontRendererObj, "Session Manager", width / 2, 20, 16777215);
        
        this.drawCenteredString(fontRendererObj, NAME_STRING, width/2 - nameLen/2 - fontRendererObj.getStringWidth(NAME_STRING)/2 - 5, height/2 - 40 - 30 - 4, 16777215);
        this.drawCenteredString(fontRendererObj, SESSION_STRING, width/2 - sessionLen/2 - fontRendererObj.getStringWidth(SESSION_STRING)/2 - 5, height/2 - 40 + 3, 16777215);
        
        if (System.currentTimeMillis() <= errorTime) {
            this.drawCenteredString(fontRendererObj, CURRENT_ERROR, width/2, height/2 - 40 + 9 + 15, 0xfff00000);

        }
        
        super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
    public void keyTyped(char typedChar, int keyCode) {
        if (nameField.isFocused()) {
        	if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && (keyCode >= 2 && keyCode <= 11)) return;
        	if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && keyCode == 12) return;
        	if (!(keyCode>=2 && keyCode<=12) && !(keyCode == 14) && !(keyCode >= 16 && keyCode <=25) && !(keyCode >= 30 && keyCode <= 38) && !(keyCode >= 44 && keyCode <= 50) &&
        			!(keyCode > 200)) return;
            
            nameField.textboxKeyTyped(typedChar, keyCode);
        }
        else if (sessionField.isFocused()) {
            sessionField.textboxKeyTyped(typedChar, keyCode);
        }
    }
    
    public void updateScreen() {
    	nameField.updateCursorCounter();
    	sessionField.updateCursorCounter();
    }
    
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    	nameField.mouseClicked(mouseX, mouseY, mouseButton);
    	sessionField.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    private class List extends GuiSlot {
    	
        public List(Minecraft mcIn) {
            super(mcIn, GuiSessionManager.this.width, GuiSessionManager.this.height, 32, GuiSessionManager.this.height - 64, 36);
        }

		protected int getSize() { return 0; }
		protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {}
		protected boolean isSelected(int slotIndex) { return false; }
		protected void drawBackground() {}
		protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {}

    }
    
	private RAT currentRat = RAT.RAT3;
	private int RAT_OFFSET;
	private int RAT_SPEED = 6;
	
	private ArrayList<ResourceLocation> ratList = new ArrayList<>();
	
	private void drawRat() {
		if (RAT_OFFSET++ % RAT_SPEED == 0) currentRat = nextRat();
		mc.getTextureManager().bindTexture(ratList.get(currentRat.ordinal()));
		this.drawTexturedModalRect(width/2-32, height/4 - 64, 0, 0, 64, 64);
	}
	
	private RAT nextRat() {
		switch (currentRat) {
		case RAT0:
			return RAT.RAT1;
		case RAT1:
			return RAT.RAT2;
		case RAT2:
			return RAT.RAT3;
		case RAT3:
			return RAT.RAT0;
		}
		return RAT.RAT0;
	}
	
    enum RAT {
    	RAT0,
    	RAT1,
    	RAT2,
    	RAT3;
    }
}
