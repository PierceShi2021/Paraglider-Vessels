package com.zhao.paraglider_vessels;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

/**
 * 新容器的玩家数据。只存服务端，客户端不需要（交易可用性由服务端算好后下发）。
 */
public final class VesselData {
	private VesselData() {
	}

	private static final String ROOT = "paraglider_vessels";

	private static CompoundTag 读取(Player player) {
		CompoundTag data = player.getPersistentData();
		return data.contains(ROOT, Tag.TAG_COMPOUND) ? data.getCompound(ROOT) : new CompoundTag();
	}

	private static void 写入(Player player, CompoundTag tag) {
		player.getPersistentData().put(ROOT, tag);
	}

	public static int get(Player player, VesselType type) {
		return Math.max(0, 读取(player).getInt(type.key));
	}

	public static void set(Player player, VesselType type, int 数量) {
		CompoundTag tag = 读取(player);
		tag.putInt(type.key, Math.max(0, 数量));
		写入(player, tag);
		VesselAttributes.refresh(player);
	}

	/** 尝试增加容器，返回实际增加数量；simulate=true 时只计算不写入。 */
	public static int give(Player player, VesselType type, int 数量, boolean simulate) {
		int 当前 = get(player, type);
		int 实际 = Math.min(Math.max(0, 数量), Math.max(0, VesselConfig.max(type) - 当前));
		if (实际 <= 0) return 0;
		if (!simulate) set(player, type, 当前 + 实际);
		return 实际;
	}

	/** 尝试卸载容器，返回实际卸载数量；simulate=true 时只计算不写入。 */
	public static int take(Player player, VesselType type, int 数量, boolean simulate) {
		int 当前 = get(player, type);
		int 实际 = Math.min(Math.max(0, 数量), 当前);
		if (实际 <= 0) return 0;
		if (!simulate) set(player, type, 当前 - 实际);
		return 实际;
	}

	/** 死亡复活、返回末地时复制数据到新玩家对象。 */
	public static void copy(Player 旧玩家, Player 新玩家) {
		写入(新玩家, 读取(旧玩家).copy());
	}
}
