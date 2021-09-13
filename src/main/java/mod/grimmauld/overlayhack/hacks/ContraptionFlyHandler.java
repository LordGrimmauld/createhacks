package mod.grimmauld.overlayhack.hacks;

import com.simibubi.create.content.contraptions.components.structureMovement.sync.ClientMotionPacket;
import com.simibubi.create.foundation.networking.AllPackets;
import mod.grimmauld.overlayhack.BuildConfig;
import mod.grimmauld.overlayhack.OverlayHackClient;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BuildConfig.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ContraptionFlyHandler {
	private static final Minecraft MC = Minecraft.getInstance();

	private ContraptionFlyHandler() {
	}

	@SubscribeEvent
	public static void performFly(TickEvent.ClientTickEvent clientTickEvent) {
		if (!OverlayHackClient.contrapionFly.get().getValue() || MC.player == null)
			return;
		Vector3d movement = getJumpVec();
		AllPackets.channel.sendToServer(new ClientMotionPacket(movement, true, 0));
		MC.player.setDeltaMovement(movement);
	}

	private static Vector3d getJumpVec() {
		Vector3d movement = Vector3d.ZERO;
		if (MC.options.keyJump.isDown()) {
			movement = movement.add(0, 1, 0);
		}
		if (MC.options.keyShift.isDown() && movement.x == 0 && movement.z == 0) {
			movement = movement.add(0, -1, 0);
		}
		return movement.y < -.1 ? movement : movement.add(asVec3d());
	}

	private static Vector3d asVec3d() {
		if (MC.player == null)
			return Vector3d.ZERO;
		Vector2f vector2f = MC.player.input.getMoveVector();
		float f = MC.player.getSpeed();
		float f2 = f * vector2f.x;
		float f3 = f * vector2f.y;
		float f4 = MathHelper.sin(MC.player.yRot * ((float) Math.PI / 180F));
		float f5 = MathHelper.cos(MC.player.yRot * ((float) Math.PI / 180F));
		return new Vector3d(f2 * f5 - f3 * f4, 0, f3 * f5 + f2 * f4).scale(OverlayHackClient.flySpeed.get().getValue());
	}
}
