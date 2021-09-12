package mod.grimmauld.overlayhack.util;

import mcp.MethodsReturnNonnullByDefault;
import mod.grimmauld.overlayhack.BuildConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TextHelper {
	public static void sendStatus(@Nullable PlayerEntity player, String key, Object... args) {
		if (player != null)
			player.displayClientMessage(new TranslationTextComponent(translationKey(key), args), true);
	}

	public static String translationKey(String key) {
		return BuildConfig.MODID + "." + key;
	}

	public static ITextComponent translationComponent(String key) {
		return new TranslationTextComponent(translationKey(key));
	}
}
