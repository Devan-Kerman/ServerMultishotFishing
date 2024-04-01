package dev.ueaj.multifishing.mixin;

import dev.ueaj.multifishing.Multifishing;
import dev.ueaj.multifishing.access.FishingBobberEntityAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.ArrayList;
import java.util.List;

@Mixin (FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin_MultiBobber extends ProjectileEntity
	implements FishingBobberEntityAccess {
	@Shadow
	private int hookCountdown;

	@Shadow
	public abstract @Nullable PlayerEntity getPlayerOwner();

	@Override
	@Shadow public abstract void setOwner(@Nullable Entity entity);

	@Shadow protected abstract void setPlayerFishHook(@Nullable FishingBobberEntity fishingBobber);

	@Shadow private int waitCountdown;
	@Shadow private int fishTravelCountdown;
	@Shadow @Final private static TrackedData<Boolean> CAUGHT_FISH;
	@Unique
	private List<FishingBobberEntity> friends;

	public FishingBobberEntityMixin_MultiBobber(
		EntityType<? extends ProjectileEntity> entityType, World world
	) {
		super(entityType, world);
	}

	@Inject (method = "use",
	         at = @At ("HEAD"),
	         cancellable = true)
	public void use(ItemStack usedItem, CallbackInfoReturnable<Integer> cir) {
		if (this.friends != null) {
			List<FishingBobberEntity> friends1 = this.friends;
			Multifishing.STOP_REMOVAL.set(Boolean.TRUE);
			try {
				this.friends = null;
				List<FishingBobberEntity> allFriends = new ArrayList<>(friends1);
				allFriends.add((FishingBobberEntity) (Object) this);

				int usage = 0;

				// first try only fishing
				for (FishingBobberEntity friend : allFriends) {
					if (((FishingBobberEntityAccess) friend).multifishing_getHookCooldown() > 0) {
						usage = Math.max(friend.use(usedItem), usage);
						((FishingBobberEntityAccess) friend).multifishing_resetCooldowns();
					}
				}

				// else pull in normally
				for (FishingBobberEntity friend : allFriends) {
					if (usage == 0) {
						usage = friend.use(usedItem);
						break;
					}
				}

				this.setPlayerFishHook((FishingBobberEntity) (Object) this);
				cir.setReturnValue(usage);
			} finally {
				this.friends = friends1;
				Multifishing.STOP_REMOVAL.remove();
			}
		}
	}

	@Inject(method = "remove", at = @At("HEAD"), cancellable = true)
	private void remove(RemovalReason reason, CallbackInfo ci) {
		if(Multifishing.STOP_REMOVAL.get()) {
			ci.cancel();
		}
	}

	@Override
	public void multifishing_setFriends(List<FishingBobberEntity> entities) {
		this.friends = entities;
	}

	@Override
	public List<FishingBobberEntity> multifishing_getFriends() {
		return this.friends;
	}

	@Override
	public int multifishing_getHookCooldown() {
		return this.hookCountdown;
	}

	@Override
	public void multifishing_resetCooldowns() {
		this.hookCountdown = 0;
		this.waitCountdown = 0;
		this.fishTravelCountdown = 0;
		this.getDataTracker().set(CAUGHT_FISH, false);
	}
}
