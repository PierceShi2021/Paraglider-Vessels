package com.zhao.paraglider_vessels;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 照抄原模组心之容器的做法：每条属性一个固定 UUID 的 permanent ADDITION 修饰器。
 * 每次重算都先移除旧修饰器，因此改配置、调上限、卸载返还都不会残留旧数值。
 */
public final class VesselAttributes {
	private VesselAttributes() {
	}

	public static void refresh(Player player) {
		if (player == null || player.level() == null || player.level().isClientSide) return;

		for (VesselType type : VesselType.values()) {
			if (!type.isEnabled()) continue;
			// 上限下调时保留计数，但加成按新上限截断
			int 生效数量 = Math.min(VesselData.get(player, type), VesselConfig.max(type));
			VesselType.Bonus[] bonuses = type.加成();

			for (int i = 0; i < bonuses.length; i++) {
				VesselType.Bonus bonus = bonuses[i];
				Attribute 属性 = ForgeRegistries.ATTRIBUTES.getValue(bonus.属性id);
				if (属性 == null) continue; // 联动模组缺失时属性未注册
				AttributeInstance 实例 = player.getAttribute(属性);
				if (实例 == null) continue;

				AttributeModifier 旧修饰器 = 实例.getModifier(bonus.uuid);
				if (旧修饰器 != null) 实例.removeModifier(旧修饰器);

				double 加成量 = 生效数量 * VesselConfig.bonus(type, i);
				if (加成量 != 0) { // 稳定容器是负加成，不能用 > 0 判断
					实例.addPermanentModifier(new AttributeModifier(
							bonus.uuid, "Vessel: " + type.key + "#" + i, 加成量,
							AttributeModifier.Operation.ADDITION));
				}
			}
		}
	}
}
