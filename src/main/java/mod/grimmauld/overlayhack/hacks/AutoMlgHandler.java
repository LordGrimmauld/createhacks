package mod.grimmauld.overlayhack.hacks;

import mod.grimmauld.overlayhack.BuildConfig;
import mod.grimmauld.overlayhack.OverlayHackClient;
import net.minecraft.block.BlockState;
import net.minecraft.block.RailBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEquipable;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = BuildConfig.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class AutoMlgHandler {

	private AutoMlgHandler() {
	}

	private static final Minecraft MC = Minecraft.getInstance();
	private static boolean searchingMountableEntity = false;
	private static boolean placedRail = true;

	public static void resetConstants() {
		searchingMountableEntity = false;
		placedRail = false;
	}

	@SubscribeEvent
	public static void performMlg(TickEvent.ClientTickEvent clientTickEvent) {
		if (!OverlayHackClient.autoMlg.get().getValue())
			return;

		ClientPlayerEntity player = MC.player;
		if (player == null)
			return;
		ClientWorld level = MC.level;
		PlayerController controller = MC.gameMode;

		// not falling
		if (level == null || MC.gameMode == null || player.isOnGround() || player.isInvulnerable() || player.isCreative() || !player.attackable() ||
			player.isInWaterOrBubble() || player.fallDistance < 3 || player.isPassenger()) {
			resetConstants();
			return;
		}

		float reachDistance = controller.getPickRange();
		Vector3d reachVec = new Vector3d(reachDistance, reachDistance, reachDistance);
		Vector3d eyePos = player.getEyePosition(1);
		BlockRayTraceResult r = level.clip(new RayTraceContext(eyePos, eyePos.subtract(0, reachDistance, 0), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, player));

		// no ground in reach = no reason to mlg
		if (r.getType() == RayTraceResult.Type.MISS) {
			return;
		}

		BlockPos groundPos = r.getBlockPos();
		BlockState groundState = level.getBlockState(groundPos);
		BlockPos toPlaceAt = groundPos.above();
		BlockState replaceState = level.getBlockState(toPlaceAt);

		NonNullList<ItemStack> items = player.inventory.items;

		// search for saddles and stuff
		int saddleSlot = -1;
		int boatSlot = -1;
		int railSlot = -1;
		int minecartSlot = -1;
		for (int slot = 0; slot < 9; slot++) {
			Item item = items.get(slot).getItem();
			if (item instanceof SaddleItem && saddleSlot == -1) {
				saddleSlot = slot;
			}
			if (item instanceof BoatItem && boatSlot == -1) {
				boatSlot = slot;
			}
			if (item.equals(Items.MINECART) && minecartSlot == -1) {
				minecartSlot = slot;
			}
			if (item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof RailBlock && railSlot == -1) {
				railSlot = slot;
			}
		}

		// try to ride entities
		List<Entity> rideable = level.getEntities(player, new AxisAlignedBB(eyePos.add(reachVec), eyePos.subtract(reachVec)), e -> e.position().distanceTo(eyePos) < reachDistance);
		searchingMountableEntity = true;
		for (Entity e : rideable) {
			if (e instanceof IEquipable && ((IEquipable) e).isSaddleable() && !((IEquipable) e).isSaddled() && saddleSlot >= 0) {
				runWithSlotSelected(saddleSlot, () -> controller.interact(player, e, Hand.MAIN_HAND));
			}
			controller.interact(player, e, Hand.MAIN_HAND);
			if (player.isPassenger()) {
				searchingMountableEntity = false;
				return;
			}
		}

		// place rail
		if (minecartSlot >= 0 && railSlot >= 0 && !placedRail) {
			if(runWithSlotSelected(railSlot, () -> {
				player.connection.send(new CEntityActionPacket(player, CEntityActionPacket.Action.PRESS_SHIFT_KEY));
				ActionResultType result = controller.useItemOn(player, level, Hand.MAIN_HAND, r);
				player.connection.send(new CEntityActionPacket(player, CEntityActionPacket.Action.RELEASE_SHIFT_KEY));
				placedRail = true;
				return result;
			}) == ActionResultType.SUCCESS);
				return;
		}

		// place minecart
		if (minecartSlot >= 0 && replaceState.getBlock() instanceof RailBlock) {
			BlockRayTraceResult rail = level.clip(new RayTraceContext(eyePos, eyePos.subtract(0, reachDistance, 0), RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, player));
			if(runWithSlotSelected(minecartSlot, () -> controller.useItemOn(player, level, Hand.MAIN_HAND, rail)) == ActionResultType.SUCCESS);
				return;
		}

		// place boats
		if (boatSlot >= 0) {
			BlockRayTraceResult rail = level.clip(new RayTraceContext(eyePos, eyePos.subtract(0, reachDistance, 0), RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.ANY, player));
			if (runWithSlotSelected(boatSlot, () -> controller.useItemOn(player, level, Hand.MAIN_HAND, rail)) == ActionResultType.SUCCESS);
				return;
		}

	}

	@Nullable
	private static <T> T runWithSlotSelected(int toSelect, Supplier<T> r) {
		if (MC.player == null)
			return null;
		int oldSlot = MC.player.inventory.selected;
		MC.player.inventory.selected = toSelect;
		T result = r.get();
		MC.player.inventory.selected = oldSlot;
		return result;
	}

	@SubscribeEvent
	public static void onRenderGui(GuiOpenEvent event) {
		if (event.getGui() != null && searchingMountableEntity) {
			event.setCanceled(true);
		}
	}
}
