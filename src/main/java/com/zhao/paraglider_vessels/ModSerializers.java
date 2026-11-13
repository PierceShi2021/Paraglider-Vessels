package com.zhao.paraglider_vessels;

import com.zhao.paraglider_vessels.bargain.VesselBargainSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import tictim.paraglider.api.bargain.Bargain;

public final class ModSerializers {
	private ModSerializers() {
	}

	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
			DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ParagliderVessels.MODID);

	public static final RegistryObject<RecipeSerializer<?>> VESSEL_BARGAIN =
			SERIALIZERS.register("vessel_bargain", VesselBargainSerializer::new);

	/**
	 * 返回原模组的交易配方类型。必须延迟查询：本类被加载时注册表可能还没填充。
	 * 返回同一个注册对象，BargainHandler 才能用 getAllRecipesFor 收集到我们的配方。
	 */
	@SuppressWarnings("unchecked")
	public static RecipeType<Bargain> bargainType() {
		return (RecipeType<Bargain>) ForgeRegistries.RECIPE_TYPES.getValue(new ResourceLocation("paraglider", "bargain"));
	}
}
