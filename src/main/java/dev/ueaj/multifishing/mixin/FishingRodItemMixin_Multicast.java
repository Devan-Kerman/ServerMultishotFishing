package dev.ueaj.multifishing.mixin;

import dev.ueaj.multifishing.Multifishing;
import dev.ueaj.multifishing.access.FishingBobberEntityAccess;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.FishingRodItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import java.util.ArrayList;
import java.util.List;

@Mixin (FishingRodItem.class)
public class FishingRodItemMixin_Multicast {

	@Inject (at = @At (value = "INVOKE",
	                   target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"),
	         method = "use",
	         locals = LocalCapture.CAPTURE_FAILHARD)
	private void init(
		World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir, ItemStack itemStack, int lure, int luck
	) {
		int multi = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, itemStack);
		if(multi > 0) {
			List<FishingBobberEntity> friends = new ArrayList<>();
			for (int i = 0; i < multi*2; i++) {
				FishingBobberEntity entity = new FishingBobberEntity(user, world, lure, luck);
				entity.addVelocity(
					new Vec3d(world.getRandom().nextGaussian() * .1, world.getRandom().nextGaussian() * .1,
						world.getRandom().nextGaussian() * .1
					));
				world.spawnEntity(entity);
				friends.add(entity);
			}
			Multifishing.FRIENDS.set(friends);
		}
	}

	@ModifyArg(method = "use", at = @At (value = "INVOKE",
	                                     target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
	private Entity modify(Entity entity) {
		if(entity instanceof FishingBobberEntityAccess bobber) {
			bobber.multifishing_setFriends(Multifishing.FRIENDS.get());
			FishingBobberEntity bobber2 = (FishingBobberEntity) entity;
			bobber2.setOwner(bobber2.getOwner());
			Multifishing.FRIENDS.remove();
		}
		return entity;
	}
}