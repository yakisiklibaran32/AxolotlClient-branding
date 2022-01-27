package io.github.moehreag.axolotlclient.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
	/**
	 * @author LoganDark
	 * @reason Change integrated server brand
	 */
	@Overwrite(remap = false)
	public String getServerModName() {
		return "vanilla";
	}
}
