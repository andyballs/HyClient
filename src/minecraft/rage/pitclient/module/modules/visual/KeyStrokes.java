package rage.pitclient.module.modules.visual;

import java.awt.Color;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.ScaledResolution;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.ClickGuiSettingEvent;
import rage.pitclient.events.InputEvent;
import rage.pitclient.events.RenderGameOverlayEvent;
import rage.pitclient.events.RenderGameOverlayEvent.ElementType;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;
import rage.pitclient.util.RenderUtils;
import rage.pitclient.util.TextRenderUtils;

@ModuleInfo(name = "Keystrokes", category = Category.VISUAL)
public class KeyStrokes extends Module {
	
	private Setting offset = regSlider("Offset", 2, 0, 15, true);
	private Setting xSet = regSlider("X", 50, 0, 1000, true);
	private Setting ySet = regSlider("Y", 50, 0, 1000, true);
	
	private Key W = new Key("W", Keyboard.KEY_W, 1, 0);
	private Key A = new Key("A", Keyboard.KEY_A, 0, 1);
	private Key S = new Key("S", Keyboard.KEY_S, 1, 1);
	private Key D = new Key("D", Keyboard.KEY_D, 2, 1);
	
	private Button LEFT_MOUSE = new Button("L", 0, 0, 2);
	private Button RIGHT_MOUSE = new Button("R", 1, 1, 2);
	
	private float keyWidth = 30;
	private float keyHeight = 30;
	
	private float btnWidth = 45;
	private float btnHeight = 30;
	
	private Key[] keyList = { W, A, S, D };
	
	private Button[] btnList = { LEFT_MOUSE, RIGHT_MOUSE };
	
	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Post event) {
		if (event.type != ElementType.ALL)
			return;
		if (!enabled)
			return;
		
		ScaledResolution sr = event.resolution;
		
		for (Key key : keyList) {
			float keyX = (float) (xSet.getValDouble() + key.xScale * keyWidth + (key.xScale * offset.getValDouble()));
			float keyY = (float) (ySet.getValDouble() + key.yScale * keyHeight + (key.yScale * offset.getValDouble()));
			
			RenderUtils.drawBorderedRect(keyX, keyY, keyX + keyWidth, keyY + keyHeight, 1, Color.darkGray.getRGB(), new Color(255,255,255,100).getRGB());
			
			if (Keyboard.isKeyDown(key.keyCode)) RenderUtils.drawBorderedRect(keyX, keyY, keyX + keyWidth, keyY + keyHeight, 3, 0, new Color(70,70,70,150).getRGB());
			
			TextRenderUtils.drawStringCentered(key.display, mc.fontRendererObj, keyX + keyWidth/2, keyY + keyHeight/2, true, 0xa0a0a0);
		}		
		
		for (Button btn : btnList) {
			
			float btnX = (float) (xSet.getValDouble() + btn.xScale * btnWidth + btn.xScale * offset.getValDouble()*2);
			float btnY = (float) (ySet.getValDouble() + btn.yScale * btnHeight + btn.yScale * offset.getValDouble());

			RenderUtils.drawBorderedRect(btnX, btnY, btnX + btnWidth, btnY + btnHeight, 1, Color.darkGray.getRGB(), new Color(255,255,255,100).getRGB());
			
			if (Mouse.isButtonDown(btn.mouseCode)) RenderUtils.drawBorderedRect(btnX, btnY, btnX + btnWidth, btnY + btnHeight, 3, 0, new Color(70,70,70,150).getRGB());
			
			TextRenderUtils.drawStringCentered(btn.display, mc.fontRendererObj, btnX + btnWidth/2, btnY + btnHeight/2 - 6, true, 0xa0a0a0);
			TextRenderUtils.drawStringCentered(btn.getCPS() + " CPS", mc.fontRendererObj, btnX + btnWidth/2, btnY + btnHeight/2 + 6, true, 0xa0a0a0);
		}	
	}
	
	@SubscribeEvent
	public void onSetChange(ClickGuiSettingEvent event) {
		if (event.setting == xSet) {
			ScaledResolution sr = new ScaledResolution(mc);
			xSet.setMax(sr.getScaledWidth());
		}
		if (event.setting == ySet) {
			ScaledResolution sr = new ScaledResolution(mc);
			ySet.setMax(sr.getScaledHeight());
		}
	}
	
	@SubscribeEvent
	public void onClick(InputEvent.MouseInputEvent event) {
		if (!Mouse.getEventButtonState())
			return;
		
		for (Button btn : btnList) {
			if (btn.mouseCode == Mouse.getEventButton()) btn.addClick();
		}
	}
	
	class Key {
		
		public String display;
		public int keyCode;
		public double xScale, yScale;
		
		public Key(String display, int keyCode, double xScale, double yScale) {
			this.display = display;
			this.keyCode = keyCode;
			this.xScale = xScale;
			this.yScale = yScale;
		}
	}
	
	class Button {
		
		public String display;
		public int mouseCode;
		public double xScale, yScale;
		
		public Button(String display, int mouseCode, double xScale, double yScale) {
			this.display = display;
			this.mouseCode = mouseCode;
			this.xScale = xScale;
			this.yScale = yScale;
		}
		
		private ArrayList<Long> clickList = new ArrayList<>();
		
		public void addClick() {
			clickList.add(System.currentTimeMillis());
		}
		
		private void checkList() {
			ArrayList<Long> checked = new ArrayList<>();
			long curr = System.currentTimeMillis();
			for (long click : clickList) {
				if (click + 1000 > curr) checked.add(click);
			}
			clickList = checked;
		}
		
		public int getCPS() {
			checkList();
			return clickList.size();
		}
		
	}
}
