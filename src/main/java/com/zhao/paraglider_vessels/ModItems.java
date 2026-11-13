package com.zhao.paraglider_vessels;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.EnumMap;
import java.util.Map;

public final class ModItems {
	private ModItems() {
	}

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ParagliderVessels.MODID);
	private static final Map<VesselType, RegistryObject<Item>> 物品 = new EnumMap<>(VesselType.class);

	static {
		// 联动容器缺模组时连物品都不注册
		for (VesselType type : VesselType.values()) {
			if (!type.isEnabled()) continue;
			VesselType t = type;
			物品.put(t, ITEMS.register(t.物品id, () -> new VesselItem(t)));
		}
	}

	/** 未启用的容器返回空气，避免预览构造时对空 RegistryObject 取值而崩溃。 */
	public static Item item(VesselType type) {
		RegistryObject<Item> r = 物品.get(type);
		return r == null || !r.isPresent() ? Items.AIR : r.get();
	}

	public static boolean 已注册(VesselType type) {
		RegistryObject<Item> r = 物品.get(type);
		return r != null && r.isPresent();
	}
}
