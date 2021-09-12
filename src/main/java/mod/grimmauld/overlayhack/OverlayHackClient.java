package mod.grimmauld.overlayhack;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.sidebaroverlay.Manager;
import mod.grimmauld.sidebaroverlay.SidebarOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.SelectOpenOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.BooleanSelectConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.util.LazyValue;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import javax.annotation.ParametersAreNonnullByDefault;

import static mod.grimmauld.overlayhack.util.TextHelper.translationComponent;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class OverlayHackClient {
	public static final LazyValue<BooleanSelectConfig> fastSignPlacing = new LazyValue<>(() -> new BooleanSelectConfig(translationComponent("fastsign"), false));
	private static final Minecraft MC = Minecraft.getInstance();

	public static void setupOverlay(InterModEnqueueEvent event) {
		SelectOverlay clickerOverlay = new SelectOverlay(translationComponent("stripper"))
			.addOption(fastSignPlacing.get())
			.register();

		InterModComms.sendTo(SidebarOverlay.MODID, Manager.IMC_ADD_OVERLAY_ENTRY, () -> new SelectOpenOverlay(translationComponent("stripper"), clickerOverlay));
	}

	@SubscribeEvent
	public static void onRenderGui(GuiOpenEvent event) {
		if (event.getGui() instanceof EditSignScreen && fastSignPlacing.get().getValue()) {
			event.setCanceled(true);
		}
	}
}
