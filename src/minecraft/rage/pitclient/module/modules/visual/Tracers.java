package rage.pitclient.module.modules.visual;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import rage.pitclient.PitClient;
import rage.pitclient.eventbus.event.SubscribeEvent;
import rage.pitclient.events.EntityViewRenderEvent;
import rage.pitclient.events.RenderPlayerEvent;
import rage.pitclient.module.Category;
import rage.pitclient.module.Module;
import rage.pitclient.module.ModuleInfo;
import rage.pitclient.settings.Setting;
import rage.pitclient.util.RenderUtils;

@ModuleInfo(name = "Tracers", category = Category.VISUAL)
public class Tracers extends Module {
	
	private Setting renderOption = regOptionTip("Render Tracers","Determines what players will be have tracers", "All Players", "Warning List", "Warning and Darks");
	private Setting renderHitbox = regBooleanTip("Render Hitbox", true, "Renders the players hitbox as well");
	private Setting expandByHitbox = regBooleanTip("Factor in Hitboxes", false, "Expands players hitbox by the current setting in Hitboxes");
	
	@SubscribeEvent
	public void onRender(RenderPlayerEvent.Post event) {
		if (!enabled)
			return;
		if (!(event.entityPlayer instanceof EntityPlayer))
			return;
		if (event.entityPlayer.getName().equals(Minecraft.getMinecraft().thePlayer.getName()))
			return;
		if (!renderTracer(event.entityPlayer.getName()))
			return;
		
        double x = (event.entityPlayer.lastTickPosX + (event.entityPlayer.posX - event.entityPlayer.lastTickPosX) * (double)event.partialRenderTick) - event.renderer.getRenderManager().viewerPosX;
        double y = (event.entityPlayer.lastTickPosY + (event.entityPlayer.posY - event.entityPlayer.lastTickPosY) * (double)event.partialRenderTick) - event.renderer.getRenderManager().viewerPosY;
        double z = (event.entityPlayer.lastTickPosZ + (event.entityPlayer.posZ - event.entityPlayer.lastTickPosZ) * (double)event.partialRenderTick) - event.renderer.getRenderManager().viewerPosZ;

		renderBoundingBox(event.entityPlayer, x, y, z, event.partialRenderTick);                
	}
	
	private boolean renderTracer(String player) {
		
		if (PitClient.friendManager.isEnemy(EnumChatFormatting.getTextWithoutFormattingCodes(player))) return true;
		if (PitClient.friendManager.isFriend(EnumChatFormatting.getTextWithoutFormattingCodes(player))) return true;
		
		switch (renderOption.getValString().toLowerCase()) {
		case "all players":
			return true;
		case "warning list":
			return PitClient.getInstance().getManager().currentPlayerMap.get(player) != null;
		case "warning and darks":
			return PitClient.getInstance().getManager().currentPlayerMap.get(player) != null | 
					PitClient.getInstance().getManager().darkMap.get(player) != null;
		default:
			return false;
		}
	}
	
	@SubscribeEvent
	public void onFogRender(EntityViewRenderEvent.FogDensity event) {
		if (!enabled)
			return;
		event.density = 0f;
		event.setCanceled(true);
	}
	
    private void renderBoundingBox(Entity entityIn, double x, double y, double z, float partialTicks)
    {
    	RenderUtils.startDrawing();
        GlStateManager.depthMask(false);
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.disableCull();
        GlStateManager.disableBlend();
        GlStateManager.disableDepth();
    	
        float red;
        float green;
        float blue;
        float alpha = 0.5F;
        
        String tracer = PitClient.getInstance().getManager().tracerMap.get(entityIn.getName());
        
        if (tracer != null && tracer.length() == 1 && "0123456789abcdef".contains(tracer)) {
        	
        	int i = Minecraft.getMinecraft().fontRendererObj.getColorCode(tracer.charAt(0));
        	
            red = (float)(i >> 16) / 255.0F;
            green = (float)(i >> 8 & 255) / 255.0F;
            blue = (float)(i & 255) / 255.0F;
        } else {
            float distanceValue = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entityIn) > 40 ? 1 : Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entityIn) / 40;

            red = 1-distanceValue;
            green = distanceValue;
            blue = 0;
        }
        
        if (PitClient.friendManager.isFriend(EnumChatFormatting.getTextWithoutFormattingCodes(entityIn.getName()))) {
        	red =  85/255;
        	green = 85/255;
        	blue = 1f;
        }
        
        if (PitClient.friendManager.isEnemy(EnumChatFormatting.getTextWithoutFormattingCodes(entityIn.getName()))) {
        	red = 170/255f;
        	green = 0f;
        	blue = 0f;
        }
    	
    	
        //Tracer
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        worldrenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
        
        worldrenderer.pos(0, Minecraft.getMinecraft().thePlayer.getEyeHeight(), 0).color(
        		red,
        		green,
        		blue,
        		alpha).endVertex();
        
        worldrenderer.pos(x, y + entityIn.getEyeHeight()/2, z).color(
        		red,
        		green,
        		blue,
        		alpha).endVertex();
        
        tessellator.draw();
        
        if (renderHitbox.getValBoolean()) {
        	//Bounding Box
            
    		double extend = 0;
    		if (PitClient.moduleManager.getModuleByName("Hitboxes").isEnabled() && expandByHitbox.getValBoolean()) {
    			extend = getExternalSetting("Hitbox Extend").getValDouble();
    		}
            AxisAlignedBB axisalignedbb = entityIn.getEntityBoundingBox().expand(extend, extend, extend);
            AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX - 0.05 - entityIn.posX + x, axisalignedbb.minY - entityIn.posY + y, axisalignedbb.minZ - 0.05 - entityIn.posZ + z, axisalignedbb.maxX + 0.05 - entityIn.posX + x, axisalignedbb.maxY + 0.1 - entityIn.posY + y, axisalignedbb.maxZ + 0.05 - entityIn.posZ + z);
            
            GlStateManager.color(red,
            		green,
            		blue,
            		alpha);
            
            RenderGlobal.drawSelectionBoundingBox(axisalignedbb1);
        }

        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        RenderUtils.stopDrawing();
        
    }
    
    
    
}
