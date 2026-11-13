package com.zhao.paraglider_vessels;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.ModList;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

/**
 * 15 种容器的统一定义：配置段名、物品 ID、属性加成、联动依赖、修饰器 UUID 都从这里取。
 * 一个容器可以对应多个属性（坚毅容器 = 盔甲韧性 + 击退抗性）。
 */
public enum VesselType {
	// ===== 常规容器（无联动依赖） =====
	STRENGTH("strength", "strength_vessel", "力量容器", Group.REGULAR,
			Bonus.of("minecraft:generic.attack_damage", "bonusPerVessel", 1.0)),
	SWIFTNESS("swiftness", "swiftness_vessel", "迅捷容器", Group.REGULAR,
			Bonus.of("minecraft:generic.movement_speed", "bonusPerVessel", 0.0025)),
	GUARD("guard", "guard_vessel", "御之容器", Group.REGULAR,
			Bonus.of("minecraft:generic.armor", "bonusPerVessel", 1.0)),
	LUCK("luck", "luck_vessel", "幸运容器", Group.REGULAR,
			Bonus.of("minecraft:generic.luck", "bonusPerVessel", 0.5)),
	AGILITY("agility", "agility_vessel", "敏之容器", Group.REGULAR,
			Bonus.of("minecraft:generic.attack_speed", "bonusPerVessel", 0.16)),
	TENACITY("tenacity", "tenacity_vessel", "坚毅容器", Group.REGULAR,
			Bonus.of("minecraft:generic.armor_toughness", "armorToughnessPerVessel", 0.5),
			Bonus.of("minecraft:generic.knockback_resistance", "knockbackResistancePerVessel", 0.01)),

	// ===== 神化联动：Apotheosis + Apothic Attributes =====
	CRIT("apoth_crit", "crit_vessel", "暴击容器", Group.APOTH,
			Bonus.of("attributeslib:crit_chance", "bonusPerVessel", 0.015)),
	CRIT_DAMAGE("apoth_critdamage", "crit_damage_vessel", "暴伤容器", Group.APOTH,
			Bonus.of("attributeslib:crit_damage", "bonusPerVessel", 0.03)),
	ARMOR_PIERCE("apoth_armorpierce", "armor_pierce_vessel", "甲穿容器", Group.APOTH,
			Bonus.of("attributeslib:armor_pierce", "bonusPerVessel", 0.01)),
	EXPERIENCE("apoth_experience", "experience_vessel", "经验容器", Group.APOTH,
			Bonus.of("attributeslib:experience_gained", "bonusPerVessel", 0.02)),
	HEALING("apoth_healing", "healing_vessel", "治愈容器", Group.APOTH,
			Bonus.of("attributeslib:healing_received", "bonusPerVessel", 0.02)),

	// ===== TACZ 联动：TaCZ + TaczAttributeAdd =====
	GUNNER("tacz_gunner", "gunner_vessel", "枪手容器", Group.TACZ,
			Bonus.of("taa:bullet_gundamage", "bonusPerVessel", 0.02)),
	PRECISION("tacz_precision", "precision_vessel", "精准容器", Group.TACZ,
			Bonus.of("taa:headshot_multiplier", "bonusPerVessel", 0.03)),
	STABILITY("tacz_stability", "stability_vessel", "稳定容器", Group.TACZ,
			Bonus.of("taa:recoil", "bonusPerVessel", -0.03)),
	ASSAULT("tacz_assault", "assault_vessel", "强袭容器", Group.TACZ,
			Bonus.of("taa:melee_damage", "bonusPerVessel", 0.04));

	private enum Group {
		REGULAR(),
		APOTH("apotheosis", "attributeslib"),
		TACZ("tacz", "taa");

		final String[] 依赖;

		Group(String... 依赖) {
			this.依赖 = 依赖;
		}
	}

	/** 单条属性加成：属性 ID、配置键、默认值、固定修饰器 UUID。 */
	public static final class Bonus {
		public final ResourceLocation 属性id;
		public final String 配置键;
		public final double 默认加成;
		public final UUID uuid;

		private Bonus(ResourceLocation 属性id, String 配置键, double 默认加成, UUID uuid) {
			this.属性id = 属性id;
			this.配置键 = 配置键;
			this.默认加成 = 默认加成;
			this.uuid = uuid;
		}

		static Bonus of(String id, String 配置键, double 默认加成) {
			return new Bonus(Objects.requireNonNull(ResourceLocation.tryParse(id)), 配置键, 默认加成, null);
		}

		Bonus withUuid(UUID u) {
			return new Bonus(属性id, 配置键, 默认加成, u);
		}
	}

	public final String key;
	public final String 物品id;
	public final String 中文名;
	private final Group group;
	private final Bonus[] 加成;

	VesselType(String key, String 物品id, String 中文名, Group group, Bonus... 加成) {
		this.key = key;
		this.物品id = 物品id;
		this.中文名 = 中文名;
		this.group = group;
		this.加成 = new Bonus[加成.length];
		for (int i = 0; i < 加成.length; i++) this.加成[i] = 加成[i].withUuid(uuidFor(key, i));
	}

	/** 前三个容器沿用 v1 写进存档的 UUID，避免旧修饰器残留导致重复加成。 */
	private static UUID uuidFor(String key, int index) {
		if (index == 0) {
			String 旧 = switch (key) {
				case "strength" -> "6f1a0c2e-1d4b-4a3f-9c8e-000000000001";
				case "swiftness" -> "6f1a0c2e-1d4b-4a3f-9c8e-000000000002";
				case "guard" -> "6f1a0c2e-1d4b-4a3f-9c8e-000000000003";
				default -> null;
			};
			if (旧 != null) return UUID.fromString(旧);
		}
		return UUID.nameUUIDFromBytes((key + "#" + index).getBytes(StandardCharsets.UTF_8));
	}

	public Bonus[] 加成() {
		return 加成;
	}

	/** 联动容器仅在依赖模组全部安装时启用。 */
	public boolean isEnabled() {
		if (group.依赖.length == 0) return true;
		try {
			for (String mod : group.依赖) if (!ModList.get().isLoaded(mod)) return false;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static VesselType byKey(String key) {
		for (VesselType type : values()) if (type.key.equals(key)) return type;
		throw new IllegalArgumentException("未知容器类型: " + key);
	}
}
