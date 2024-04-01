package dev.ueaj.multifishing.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.MultishotEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin_MultishotFishingRod {
	@Inject(method = "isAcceptableItem",
	        at = @At("HEAD"), cancellable = true)
	private void acceptFishingRod(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		if((Object)this instanceof MultishotEnchantment && stack.getItem() == Items.FISHING_ROD) {
			cir.setReturnValue(Boolean.TRUE);
		}
	}
}
