package com.zhao.paraglider_vessels.bargain;

import com.zhao.paraglider_vessels.ModItems;
import com.zhao.paraglider_vessels.VesselData;
import com.zhao.paraglider_vessels.VesselType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import tictim.paraglider.api.bargain.DemandPreview;

import java.util.List;

/** 交易界面里「需要 N 个新容器」的预览，图标用对应容器物品。 */
public final class VesselDemandPreview implements DemandPreview {
	private final VesselType type;
	private final int quantity;
	private final List<ItemStack> preview;

	public VesselDemandPreview(VesselType type, int quantity) {
		this.type = type;
		this.quantity = quantity;
		this.preview = List.of(new ItemStack(ModItems.item(type)));
	}

	@Override
	public List<ItemStack> preview() {
		return preview;
	}

	@Override
	public int quantity() {
		return quantity;
	}

	@Override
	public int count(Player player) {
		return VesselData.get(player, type);
	}

	@Override
	public List<Component> getTooltip(int previewIndex) {
		return List.of(Component.translatable("bargain.paraglider_vessels.vessel",
				Component.translatable(ModItems.item(type).getDescriptionId()), quantity));
	}
}
