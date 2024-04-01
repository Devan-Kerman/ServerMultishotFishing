package dev.ueaj.multifishing.access;

import net.minecraft.entity.projectile.FishingBobberEntity;
import java.util.List;

public interface FishingBobberEntityAccess {
	void multifishing_setFriends(List<FishingBobberEntity> entities);

	List<FishingBobberEntity> multifishing_getFriends();

	int multifishing_getHookCooldown();

	void multifishing_resetCooldowns();
}
