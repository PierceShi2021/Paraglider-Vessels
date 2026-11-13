package com.zhao.paraglider_vessels;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ParagliderVessels.MODID)
public class ParagliderVessels {
	public static final String MODID = "paraglider_vessels";
	public static final Logger LOGGER = LogUtils.getLogger();

	public ParagliderVessels() {
		var modBus = FMLJavaModLoadingContext.get().getModEventBus();

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, VesselConfig.SPEC, MODID + "-server.toml");
		ModItems.ITEMS.register(modBus);
		ModSerializers.SERIALIZERS.register(modBus);

		// 联动配方的 conditions 需要在配方解析前注册
		CraftingHelper.register(ModsLoadedCondition.Serializer.INSTANCE);

		modBus.addListener(VesselEvents::配置重载);
		modBus.addListener(VesselCreativeTab::填充创造标签);
		modBus.addListener(this::初始化);
	}

	private void 初始化(FMLCommonSetupEvent e) {
		int 启用数 = 0;
		for (VesselType type : VesselType.values()) if (type.isEnabled()) 启用数++;
		LOGGER.info("容器拓展已加载，启用 {}/{} 种容器", 启用数, VesselType.values().length);
	}
}
