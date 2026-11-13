package com.zhao.paraglider_vessels.bargain;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.zhao.paraglider_vessels.VesselType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
import tictim.paraglider.bargain.preview.QuantifiedIngredient;
import tictim.paraglider.bargain.preview.QuantifiedItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 数据包配方 paraglider_vessels:vessel_bargain 的读写，双端字段顺序必须严格一致。 */
public final class VesselBargainSerializer implements RecipeSerializer<VesselBargain> {
	@Override
	public VesselBargain fromJson(ResourceLocation id, JsonObject json) {
		ResourceLocation bargainType = new ResourceLocation(GsonHelper.getAsString(json, "bargainType"));

		List<QuantifiedIngredient> itemDemands = new ArrayList<>();
		int essenceDemands = 0;
		VesselType vesselDemandType = null;
		int vesselDemandCount = 0;
		JsonObject demands = GsonHelper.getAsJsonObject(json, "demands", null);
		if (demands != null) {
			JsonArray items = GsonHelper.getAsJsonArray(demands, "items", null);
			if (items != null)
				for (JsonElement el : items)
					itemDemands.add(new QuantifiedIngredient(GsonHelper.convertToJsonObject(el, "item")));
			essenceDemands = Math.max(0, GsonHelper.getAsInt(demands, "essences", 0));
			if (demands.has("vessel")) {
				vesselDemandType = VesselType.byKey(GsonHelper.getAsString(demands, "vessel"));
				vesselDemandCount = Math.max(0, GsonHelper.getAsInt(demands, "count", 1));
			}
		}

		List<QuantifiedItem> itemOffers = new ArrayList<>();
		int essenceOffers = 0;
		VesselType vesselOfferType = null;
		int vesselOfferCount = 0;
		JsonObject offers = GsonHelper.getAsJsonObject(json, "offers", null);
		if (offers != null) {
			JsonArray items = GsonHelper.getAsJsonArray(offers, "items", null);
			if (items != null)
				for (JsonElement el : items)
					itemOffers.add(new QuantifiedItem(GsonHelper.convertToJsonObject(el, "item")));
			essenceOffers = Math.max(0, GsonHelper.getAsInt(offers, "essences", 0));
			if (offers.has("vessel")) {
				vesselOfferType = VesselType.byKey(GsonHelper.getAsString(offers, "vessel"));
				vesselOfferCount = Math.max(0, GsonHelper.getAsInt(offers, "count", 1));
			}
		}

		Set<String> tags = new HashSet<>();
		JsonArray tagArray = GsonHelper.getAsJsonArray(json, "tags", null);
		if (tagArray != null)
			for (JsonElement el : tagArray) tags.add(GsonHelper.convertToString(el, "tag"));

		return new VesselBargain(id, bargainType, itemDemands, essenceDemands, vesselDemandType, vesselDemandCount,
				itemOffers, essenceOffers, vesselOfferType, vesselOfferCount, tags);
	}

	@Override
	public VesselBargain fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
		ResourceLocation bargainType = buffer.readResourceLocation();

		List<QuantifiedIngredient> itemDemands = new ArrayList<>();
		for (int i = 0, size = buffer.readVarInt(); i < size; i++)
			itemDemands.add(QuantifiedIngredient.read(buffer));
		int essenceDemands = buffer.readVarInt();
		String demandKey = buffer.readUtf();
		VesselType vesselDemandType = demandKey.isEmpty() ? null : VesselType.byKey(demandKey);
		int vesselDemandCount = buffer.readVarInt();

		List<QuantifiedItem> itemOffers = new ArrayList<>();
		for (int i = 0, size = buffer.readVarInt(); i < size; i++)
			itemOffers.add(QuantifiedItem.read(buffer));
		int essenceOffers = buffer.readVarInt();
		String offerKey = buffer.readUtf();
		VesselType vesselOfferType = offerKey.isEmpty() ? null : VesselType.byKey(offerKey);
		int vesselOfferCount = buffer.readVarInt();

		Set<String> tags = new HashSet<>();
		for (int i = 0, size = buffer.readVarInt(); i < size; i++) tags.add(buffer.readUtf());

		return new VesselBargain(id, bargainType, itemDemands, essenceDemands, vesselDemandType, vesselDemandCount,
				itemOffers, essenceOffers, vesselOfferType, vesselOfferCount, tags);
	}

	@Override
	public void toNetwork(FriendlyByteBuf buffer, VesselBargain recipe) {
		buffer.writeResourceLocation(recipe.bargainType);

		buffer.writeVarInt(recipe.itemDemands.size());
		for (QuantifiedIngredient 需求 : recipe.itemDemands) 需求.write(buffer);
		buffer.writeVarInt(recipe.essenceDemands);
		buffer.writeUtf(recipe.vesselDemandType == null ? "" : recipe.vesselDemandType.key);
		buffer.writeVarInt(recipe.vesselDemandCount);

		buffer.writeVarInt(recipe.itemOffers.size());
		for (QuantifiedItem 产出 : recipe.itemOffers) 产出.write(buffer);
		buffer.writeVarInt(recipe.essenceOffers);
		buffer.writeUtf(recipe.vesselOfferType == null ? "" : recipe.vesselOfferType.key);
		buffer.writeVarInt(recipe.vesselOfferCount);

		buffer.writeVarInt(recipe.userTags.size());
		for (String tag : recipe.userTags) buffer.writeUtf(tag);
	}
}
