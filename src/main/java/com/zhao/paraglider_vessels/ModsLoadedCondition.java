package com.zhao.paraglider_vessels;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

/**
 * 配方条件：JSON 里列出依赖模组 ID，全部安装才加载该配方。
 * 照抄原模组 ConfigConditionSerializer 的注册方式，用于让联动容器配方在缺模组时完全不出现。
 */
public final class ModsLoadedCondition implements ICondition {
	public static final ResourceLocation ID = new ResourceLocation(ParagliderVessels.MODID, "mods_loaded");
	private final List<String> mods;

	public ModsLoadedCondition(List<String> mods) {
		this.mods = List.copyOf(mods);
	}

	@Override
	public boolean test(IContext context) {
		for (String mod : mods) if (!ModList.get().isLoaded(mod)) return false;
		return true;
	}

	@Override
	public ResourceLocation getID() {
		return ID;
	}

	public static final class Serializer implements IConditionSerializer<ModsLoadedCondition> {
		public static final Serializer INSTANCE = new Serializer();

		private Serializer() {
		}

		@Override
		public void write(JsonObject json, ModsLoadedCondition value) {
			JsonArray arr = new JsonArray();
			for (String mod : value.mods) arr.add(mod);
			json.add("mods", arr);
		}

		@Override
		public ModsLoadedCondition read(JsonObject json) {
			List<String> mods = new ArrayList<>();
			JsonArray arr = GsonHelper.getAsJsonArray(json, "mods");
			for (JsonElement el : arr) mods.add(GsonHelper.convertToString(el, "mod"));
			return new ModsLoadedCondition(mods);
		}

		@Override
		public ResourceLocation getID() {
			return ID;
		}
	}
}
