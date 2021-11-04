package toniqx.vailware.modules.combat;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import toniqx.vailware.main.event.Event;
import toniqx.vailware.main.event.listeners.EventRotationMotion;
import toniqx.vailware.main.settings.impl.NumberSetting;
import toniqx.vailware.main.util.impl.Timer;
import toniqx.vailware.modules.Module;
import toniqx.vailware.modules.misc.Targets;

public class Aimbot extends Module {
	
	public Timer timer = new Timer();
	public NumberSetting range = new NumberSetting("Range", 4, 1, 6, 0.1);
	
	public Aimbot(){
		super("Aimbot", "", Keyboard.KEY_NONE, Category.COMBAT);
		this.addSettings(range);
	}
	
	public void onEvent(Event e){
		if(e instanceof EventRotationMotion) {
			if(e.isPre()) {
				super.mname = ChatFormatting.GRAY + "R" + " " + range.getValue() + "   ";
				EventRotationMotion event = (EventRotationMotion)e;
				List<Entity> targets = mc.theWorld.loadedEntityList.stream().filter(EntityLivingBase.class::isInstance).collect(Collectors.toList());
				targets = targets.parallelStream().filter(entity -> entity.getDistanceToEntity(mc.thePlayer) < range.getValue() && entity != mc.thePlayer && !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0).collect(Collectors.toList());
				targets.sort(Comparator.comparingDouble(entity -> ((EntityLivingBase)entity).getDistanceToEntity(mc.thePlayer)));
				if(Targets.target.is("Player")) {
					targets = targets.stream().filter(EntityPlayer.class::isInstance).collect(Collectors.toList());	
				}
				else if(Targets.target.is("Animal")) {
					targets = targets.stream().filter(EntityAnimal.class::isInstance).collect(Collectors.toList());	
				}
				else if(Targets.target.is("Mob")) {
					targets = targets.stream().filter(EntityMob.class::isInstance).collect(Collectors.toList());	
				}
				else if(Targets.target.is("All")) {
				}
				
				if(!targets.isEmpty()) {
					Entity target = targets.get(0);
					
					mc.thePlayer.rotationYaw = (getRotations(target)[0]);
					mc.thePlayer.rotationPitch = (getRotations(target)[1]);
					
				}
			}
		}
		
	}
	
	public float[] getRotations(Entity e) {
			double deltaX = e.posX + (e.posX - e.lastTickPosX) - mc.thePlayer.posX,
			deltaY = e.posY - 3.5 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
			deltaZ = e.posZ + (e.posZ - e.lastTickPosZ) - mc.thePlayer.posZ,
			distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));
			
		float yaw =	(float) Math.toDegrees(-Math.atan(deltaX / deltaZ)),
		      pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));
		
		if(deltaX < 0 && deltaZ < 0) {
			yaw = (float) (90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
		}else if(deltaX > 0 && deltaZ < 0) {
			yaw = (float) (-90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
		}
		
		return new float[] { yaw, pitch };
	}
}
