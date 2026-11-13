package com.zhao.paraglider_vessels;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

/**
 * 单文件多段配置：每种容器一段 maxVessels（默认 20），以及每条属性加成各一个配置项。
 * 配置段始终为 15 种容器全部生成，避免类初始化时 ModList 尚未就绪导致缺段；
 * 是否生效由 VesselType#isEnabled() 另行决定。
 */
public final class VesselConfig {
	private VesselConfig() {
	}

	public static final int 默认上限 = 20;
	public static final ForgeConfigSpec SPEC;
	private static final Map<String, ForgeConfigSpec.IntValue> 上限 = new HashMap<>();
	private static final Map<String, ForgeConfigSpec.DoubleValue> 加成 = new HashMap<>();

	static {
		ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();
		b.comment("容器拓展：每种容器独立的使用上限与每条属性的每级加成。",
				"属性按 min(已使用数量, 上限) 计算；调低上限不会删除已有计数。",
				"加成可为负值（稳定容器降低后坐力）。");
		for (VesselType type : VesselType.values()) {
			b.push(type.key);
			上限.put(type.key, b.comment(type.中文名 + "：最多可使用的数量")
					.defineInRange("maxVessels", 默认上限, 0, 1024));
			for (VesselType.Bonus bonus : type.加成()) {
				加成.put(配置键(type, bonus), b.comment("每个容器提供的 " + bonus.属性id + " 加成（ADDITION）")
						.defineInRange(bonus.配置键, bonus.默认加成, -1024.0, 1024.0));
			}
			b.pop();
		}
		SPEC = b.build();
	}

	private static String 配置键(VesselType type, VesselType.Bonus bonus) {
		return type.key + "." + bonus.配置键;
	}

	public static int max(VesselType type) {
		try {
			return 上限.get(type.key).get();
		} catch (Exception e) { // 配置尚未加载时回退默认值
			return 默认上限;
		}
	}

	public static double bonus(VesselType type, int index) {
		VesselType.Bonus[] bonuses = type.加成();
		if (index < 0 || index >= bonuses.length) return 0;
		try {
			return 加成.get(配置键(type, bonuses[index])).get();
		} catch (Exception e) {
			return bonuses[index].默认加成;
		}
	}

	public static double bonus(VesselType type) {
		return bonus(type, 0);
	}
}
