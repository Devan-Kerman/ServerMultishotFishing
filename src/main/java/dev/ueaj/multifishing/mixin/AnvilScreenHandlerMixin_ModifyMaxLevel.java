package dev.ueaj.multifishing.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.MultishotEnchantment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin_ModifyMaxLevel extends ForgingScreenHandler {
	public AnvilScreenHandlerMixin_ModifyMaxLevel(
		@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context
	) {
		super(type, syncId, playerInventory, context);
	}

	@Redirect (method = "updateResult", at = @At (value = "INVOKE",
	                                              target = "Lnet/minecraft/enchantment/Enchantment;getMaxLevel()I"))
	private int redirectMaxLevel(Enchantment instance) {
		if(instance instanceof MultishotEnchantment) {
			ItemStack input = this.input.getStack(0);
			if(input.getItem() == Items.ENCHANTED_BOOK || input.getItem() == Items.FISHING_ROD) {
				return 10;
			}
		}
		return instance.getMaxLevel();
	}
}
