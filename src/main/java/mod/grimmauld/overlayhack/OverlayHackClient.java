package mod.grimmauld.overlayhack;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.sidebaroverlay.Manager;
import mod.grimmauld.sidebaroverlay.SidebarOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.SelectOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.SelectOpenOverlay;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.BooleanSelectConfig;
import mod.grimmauld.sidebaroverlay.api.overlay.selection.config.IntSelectConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.LazyValue;
import net.minecraftforge.api.distmarker.Dist;
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
	public static final LazyValue<BooleanSelectConfig> autoMlg = new LazyValue<>(() -> new BooleanSelectConfig(translationComponent("automlg"), false));
	public static final LazyValue<BooleanSelectConfig> contrapionFly = new LazyValue<>(() -> new BooleanSelectConfig(translationComponent("contraptionfly"), false));
	public static final LazyValue<BooleanSelectConfig> preventFallDmg = new LazyValue<>(() -> new BooleanSelectConfig(translationComponent("preventfalldmg"), true));
	public static final LazyValue<IntSelectConfig> flySpeed = new LazyValue<>(() -> new IntSelectConfig(translationComponent("flyspeed"), 1, 10, 500));
	private static final Minecraft MC = Minecraft.getInstance();

	public static void setupOverlay(InterModEnqueueEvent event) {
		SelectOverlay clickerOverlay = new SelectOverlay(translationComponent("hacks"))
			// .addOption(autoMlg.get())
			.addOption(contrapionFly.get())
			.addOption(preventFallDmg.get())
			.addOption(flySpeed.get())
			.register();

		InterModComms.sendTo(SidebarOverlay.MODID, Manager.IMC_ADD_OVERLAY_ENTRY, () -> new SelectOpenOverlay(translationComponent("hacks"), clickerOverlay));
	}
}
