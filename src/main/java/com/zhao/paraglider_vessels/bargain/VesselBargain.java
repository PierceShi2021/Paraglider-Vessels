package com.zhao.paraglider_vessels.bargain;

import com.google.common.collect.ImmutableList;
import com.zhao.paraglider_vessels.ModSerializers;
import com.zhao.paraglider_vessels.ParagliderVessels;
import com.zhao.paraglider_vessels.VesselData;
import com.zhao.paraglider_vessels.VesselType;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;
import tictim.paraglider.ParagliderUtils;
import tictim.paraglider.api.bargain.Bargain;
import tictim.paraglider.api.bargain.BargainResult;
import tictim.paraglider.api.bargain.DemandPreview;
import tictim.paraglider.api.bargain.OfferPreview;
import tictim.paraglider.api.bargain.ParagliderBargainTags;
import tictim.paraglider.api.bargain.ParagliderFailReasons;
import tictim.paraglider.api.vessel.VesselContainer;
import tictim.paraglider.bargain.preview.EssenceDemandPreview;
import tictim.paraglider.bargain.preview.EssenceOfferPreview;
import tictim.paraglider.bargain.preview.QuantifiedIngredient;
import tictim.paraglider.bargain.preview.QuantifiedItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 自定义交易配方：getType() 返回原模组的 paraglider:bargain，
 * 因此会被 BargainHandler 收集并显示在女神/恶魔雕像界面里，原模组无需改动。
 */
public final class VesselBargain implements Bargain {
	final ResourceLocation id;
	final ResourceLocation bargainType;
	final List<QuantifiedIngredient> itemDemands;
	final int essenceDemands;
	@Nullable final VesselType vesselDemandType;
	final int vesselDemandCount;
	final List<QuantifiedItem> itemOffers;
	final int essenceOffers;
	@Nullable final VesselType vesselOfferType;
	final int vesselOfferCount;
	final Set<String> tags;
	private final List<DemandPreview> demandPreviews;
	private final List<OfferPreview> offerPreviews;

	public VesselBargain(ResourceLocation id,
	                     ResourceLocation bargainType,
	                     List<QuantifiedIngredient> itemDemands,
	                     int essenceDemands,
	                     @Nullable VesselType vesselDemandType,
	                     int vesselDemandCount,
	                     List<QuantifiedItem> itemOffers,
	                     int essenceOffers,
	                     @Nullable VesselType vesselOfferType,
	                     int vesselOfferCount,
	                     Set<String> userTags) {
		this.id = Objects.requireNonNull(id);
		this.bargainType = Objects.requireNonNull(bargainType);
		this.itemDemands = ImmutableList.copyOf(itemDemands);
		this.essenceDemands = essenceDemands;
		this.vesselDemandType = vesselDemandType;
		this.vesselDemandCount = vesselDemandType == null ? 0 : vesselDemandCount;
		this.itemOffers = ImmutableList.copyOf(itemOffers);
		this.essenceOffers = essenceOffers;
		this.vesselOfferType = vesselOfferType;
		this.vesselOfferCount = vesselOfferType == null ? 0 : vesselOfferCount;
		this.userTags = Set.copyOf(userTags);

		// 双端预览数量与顺序必须完全一致，所以在构造时一次生成
		List<DemandPreview> demands = new ArrayList<>(this.itemDemands);
		if (this.essenceDemands > 0) demands.add(new EssenceDemandPreview(this.essenceDemands));
		if (this.vesselDemandType != null && this.vesselDemandCount > 0)
			demands.add(new VesselDemandPreview(this.vesselDemandType, this.vesselDemandCount));
		this.demandPreviews = Collections.unmodifiableList(demands);

		List<OfferPreview> offers = new ArrayList<>(this.itemOffers);
		if (this.essenceOffers > 0) offers.add(new EssenceOfferPreview(this.essenceOffers));
		if (this.vesselOfferType != null && this.vesselOfferCount > 0)
			offers.add(new VesselOfferPreview(this.vesselOfferType, this.vesselOfferCount));
		this.offerPreviews = Collections.unmodifiableList(offers);

		Set<String> 标签 = new HashSet<>(this.userTags);
		if (!this.itemDemands.isEmpty()) 标签.add(ParagliderBargainTags.CONSUMES_ITEM);
		if (this.essenceDemands > 0) 标签.add(ParagliderBargainTags.CONSUMES_ESSENCE);
		if (this.vesselDemandType != null) 标签.add(ParagliderVessels.MODID + ":consumes_vessel");
		if (!this.itemOffers.isEmpty()) 标签.add(ParagliderBargainTags.GIVES_ITEM);
		if (this.essenceOffers > 0) 标签.add(ParagliderBargainTags.GIVES_ESSENCE);
		if (this.vesselOfferType != null) 标签.add(ParagliderVessels.MODID + ":gives_vessel");
		this.tags = Set.copyOf(标签);
	}

	final Set<String> userTags;

	@Override
	public ResourceLocation getBargainType() {
		return bargainType;
	}

	@Override
	public boolean isAvailableFor(Player player, net.minecraft.core.BlockPos pos) {
		// conditions 已在加载阶段过滤联动配方，这里再兜一层，防止数据包覆盖时露出未启用的容器
		if (vesselDemandType != null && !vesselDemandType.isEnabled()) return false;
		if (vesselOfferType != null && !vesselOfferType.isEnabled()) return false;
		return true;
	}

	@Override
	public BargainResult bargain(Player player, boolean simulate) {
		VesselContainer 原容器 = VesselContainer.get(player);
		Int2IntOpenHashMap 消耗表 = new Int2IntOpenHashMap();
		Set<String> 原因 = new HashSet<>();

		// 1. 需求：物品
		for (QuantifiedIngredient 需求 : itemDemands) {
			if (!ParagliderUtils.calculateConsumption(需求, player.getInventory(), 消耗表)) {
				原因.add(ParagliderFailReasons.NOT_ENOUGH_ITEMS);
				break;
			}
		}
		// 2. 需求：精华（复用原模组的全局货币）
		if (essenceDemands > 0 && 原容器.takeEssences(essenceDemands, true, false) < essenceDemands)
			原因.add(ParagliderFailReasons.NOT_ENOUGH_ESSENCES);
		// 3. 需求：新容器
		if (vesselDemandType != null && VesselData.get(player, vesselDemandType) < vesselDemandCount)
			原因.add(ParagliderVessels.MODID + ":not_enough_vessel");
		if (!原因.isEmpty()) return BargainResult.fail(原因);

		// 4. 产出侧上限检查
		if (essenceOffers > 0 && 原容器.giveEssences(essenceOffers, true, false) < essenceOffers)
			原因.add(ParagliderFailReasons.ESSENCE_FULL);
		if (vesselOfferType != null && VesselData.give(player, vesselOfferType, vesselOfferCount, true) < vesselOfferCount)
			原因.add(ParagliderVessels.MODID + ":vessel_full");
		if (!原因.isEmpty()) return BargainResult.fail(原因);

		if (!simulate) {
			// 扣物品
			for (var entry : 消耗表.int2IntEntrySet()) {
				int 槽位 = entry.getIntKey();
				int 数量 = entry.getIntValue();
				if (数量 <= 0) continue;
				ItemStack stack = player.getInventory().getItem(槽位);
				if (stack.getCount() > 数量) stack.shrink(数量);
				else player.getInventory().setItem(槽位, ItemStack.EMPTY);
			}
			// 发物品
			for (QuantifiedItem 产出 : itemOffers)
				ParagliderUtils.giveItem(player, 产出.getItemWithQuantity());

			// 精华与容器计数
			if (essenceDemands > 0) 原容器.takeEssences(essenceDemands, false, true);
			if (essenceOffers > 0) 原容器.giveEssences(essenceOffers, false, true);
			if (vesselDemandType != null) VesselData.take(player, vesselDemandType, vesselDemandCount, false);
			if (vesselOfferType != null) VesselData.give(player, vesselOfferType, vesselOfferCount, false);
		}
		return BargainResult.success();
	}

	@Override
	public List<DemandPreview> previewDemands() {
		return demandPreviews;
	}

	@Override
	public List<OfferPreview> previewOffers() {
		return offerPreviews;
	}

	@Override
	public Set<String> getBargainTags() {
		return tags;
	}

	@Override
	public ResourceLocation getId() {
		return id;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return ModSerializers.VESSEL_BARGAIN.get();
	}

	@Override
	public RecipeType<?> getType() {
		return ModSerializers.bargainType();
	}
}
