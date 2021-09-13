package mod.grimmauld.overlayhack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static mod.grimmauld.overlayhack.BuildConfig.MODID;

@Mod(MODID)
public class OverlayHacks {
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public OverlayHacks() {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(OverlayHackClient::setupOverlay));
	}
}
