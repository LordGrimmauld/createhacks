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
public class AntiFallDmg {
	private static final Minecraft MC = Minecraft.getInstance();

	private AntiFallDmg() {
	}

	@SubscribeEvent
	public static void breakFall(TickEvent.ClientTickEvent clientTickEvent) {
		if (OverlayHackClient.contrapionFly.get().getValue() || !OverlayHackClient.preventFallDmg.get().getValue() || MC.player == null)
			return;
		AllPackets.channel.sendToServer(new ClientMotionPacket(MC.player.getDeltaMovement(), true, 0));
	}
}
