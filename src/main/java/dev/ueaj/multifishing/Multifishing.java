package dev.ueaj.multifishing;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.tag.convention.v1.ConventionalEnchantmentTags;
import net.minecraft.entity.projectile.FishingBobberEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Unique;
import java.util.List;

public class Multifishing implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("multifishing");
	public static final ThreadLocal<List<FishingBobberEntity>> FRIENDS = new ThreadLocal<>();
	public static final ThreadLocal<Boolean> STOP_REMOVAL = ThreadLocal.withInitial(() -> Boolean.FALSE);

	@Override
	public void onInitialize() {
		LOGGER.info("Initialized Serverside Multishot Fishing!");
	}
}