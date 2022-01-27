package io.github.moehreag.axolotlclient.mixin;

import io.github.moehreag.axolotlclient.Axolotlclient;
import io.github.moehreag.axolotlclient.util.DiscordRPC;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/* Debugging...
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.github.moehreag.branding.Axolotlclient;
*/

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

	/**
	 * @author moehreag
	 * @reason Remove modded signs because they're ugly
	 */
	@Overwrite
	public boolean isModded() {
		return false;
	}

	/**
	 * @author meohreag
	 * @reason Customize Window title for use in AxolotlClient
	 */
	@Overwrite
	private String getWindowTitle() {

		return "AxolotlClient" + " " +SharedConstants.getGameVersion().getName();
	}

	@Redirect(
		method = "method_1509", // "Is Modded" lambda in addSystemDetailsToCrashReport
		at = @At(
			value = "INVOKE",
			target = "Ljava/lang/Class;getSigners()[Ljava/lang/Object;"
		),
		remap = false
	)
	private static Object[] onGetSigners(Class aClass) {
		return new Object[0]; // not null
	}

	@Redirect(
		method = "<init>",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/RunArgs$Game;version:Ljava/lang/String;"
		)
	)
	private String redirectVersion(RunArgs.Game game) {
		return SharedConstants.getGameVersion().getName();
	}

	@Redirect(
		method = "<init>",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/RunArgs$Game;versionType:Ljava/lang/String;"
		)
	)
	private String redirectVersionType(RunArgs.Game game) {
		String versionType = game.versionType;

		if (versionType.endsWith("Fabric")) {
			if (versionType.endsWith("/Fabric")) {
				return versionType.substring(0, versionType.length() - 7);
			}

			return "release";
		}

		return versionType;
	}


	@Inject(method = "tick", at = @At("HEAD"))
	public void TickClient(CallbackInfo ci){
		DiscordRPC.update();
		Axolotlclient.TickClient();
	}
}
