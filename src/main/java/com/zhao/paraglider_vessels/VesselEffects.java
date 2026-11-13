package com.zhao.paraglider_vessels;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

/** 容器使用时的表现：粒子照抄原模组耐力容器，音效统一用原版短促的「叮」。 */
public final class VesselEffects {
	private VesselEffects() {
	}

	/** SimpleVesselContainer.spawnParticle 的同款参数：HAPPY_VILLAGER，7+7*1=14 个。 */
	public static void 粒子(Player player) {
		if (player == null || player.level() == null || !(player.level() instanceof ServerLevel sl)) return;
		sl.sendParticles(ParticleTypes.HAPPY_VILLAGER,
				player.getX(), player.getY(.5), player.getZ(), 14, 1, 2, 1, 0);
	}

	public static void 音效(Player player, SoundEvent sound) {
		if (player == null || player.level() == null || player.level().isClientSide) return;
		player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
				sound, SoundSource.PLAYERS, 1.0f, 1.0f);
	}

	/** 使用容器成功：耐力容器同款粒子 + 原版经验球拾取的「叮」。 */
	public static void 容器使用(Player player) {
		粒子(player);
		音效(player, SoundEvents.EXPERIENCE_ORB_PICKUP);
	}

	/** 排斥容器：原版信标失效音效。 */
	public static void 排斥容器(Player player) {
		音效(player, SoundEvents.BEACON_DEACTIVATE);
	}
}
