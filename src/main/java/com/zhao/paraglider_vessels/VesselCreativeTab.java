package com.zhao.paraglider_vessels;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

/** 把已启用的新容器追加到原模组自己的创造标签页。 */
public final class VesselCreativeTab {
	private VesselCreativeTab() {
	}

	private static final ResourceLocation PARAGLIDER_TAB = new ResourceLocation("paraglider", "paraglider");

	public static void 填充创造标签(BuildCreativeModeTabContentsEvent e) {
		if (!PARAGLIDER_TAB.equals(e.getTabKey().location())) return;
		for (VesselType type : VesselType.values()) {
			if (!type.isEnabled() || !ModItems.已注册(type)) continue;
			e.accept(new ItemStack(ModItems.item(type)));
		}
	}
}
