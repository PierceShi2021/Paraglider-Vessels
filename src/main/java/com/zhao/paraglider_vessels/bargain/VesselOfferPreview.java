package com.zhao.paraglider_vessels.bargain;

import com.zhao.paraglider_vessels.ModItems;
import com.zhao.paraglider_vessels.VesselType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import tictim.paraglider.api.bargain.OfferPreview;

import java.util.List;

/** 交易界面里「给予 N 个新容器」的预览。 */
public final class VesselOfferPreview implements OfferPreview {
	private final VesselType type;
	private final int quantity;
	private final ItemStack preview;

	public VesselOfferPreview(VesselType type, int quantity) {
		this.type = type;
		this.quantity = quantity;
		this.preview = new ItemStack(ModItems.item(type));
	}

	@Override
	public ItemStack preview() {
		return preview;
	}

	@Override
	public int quantity() {
		return quantity;
	}

	@Override
	public List<Component> getTooltip() {
		return List.of(Component.translatable("bargain.paraglider_vessels.vessel",
				Component.translatable(ModItems.item(type).getDescriptionId()), quantity));
	}
}
