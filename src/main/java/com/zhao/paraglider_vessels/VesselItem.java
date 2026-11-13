package com.zhao.paraglider_vessels;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 行为对齐原模组 VesselItem：右键消耗物品并永久增加一个容器，已达上限则失败且不消耗。
 * 新容器计数不做客户端同步，所以客户端乐观放行，实际判定在服务端完成。
 */
public class VesselItem extends Item {
	private final VesselType type;

	public VesselItem(VesselType type) {
		super(new Item.Properties().rarity(Rarity.RARE));
		this.type = type;
	}

	public VesselType type() {
		return type;
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return true;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (level.isClientSide) return InteractionResultHolder.sidedSuccess(stack, true);

		if (VesselData.give(player, type, 1, true) == 1) {
			VesselData.give(player, type, 1, false);
			VesselEffects.容器使用(player);
			stack.shrink(1);
			return InteractionResultHolder.sidedSuccess(stack, false);
		}
		return InteractionResultHolder.fail(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
		VesselType.Bonus[] bonuses = type.加成();
		Object[] 参数 = new Object[bonuses.length + 1];
		// 加成数值在前、使用次数在后，两者都用黄色，整行绿色——与原模组容器 tooltip 完全一致
		for (int i = 0; i < bonuses.length; i++) {
			double 值 = VesselConfig.bonus(type, i);
			if (type == VesselType.STABILITY) 值 = Math.abs(值); // 文案写「降低」，显示绝对值
			参数[i] = Component.literal(数值文本(值)).withStyle(ChatFormatting.YELLOW);
		}
		参数[bonuses.length] = Component.literal(Integer.toString(VesselConfig.max(type)))
				.withStyle(ChatFormatting.YELLOW);
		tooltip.add(Component.translatable("tooltip." + ParagliderVessels.MODID + "." + type.key, 参数)
				.withStyle(ChatFormatting.GREEN));
	}

	/** 整数值不显示多余的 .0。 */
	private static String 数值文本(double 值) {
		return 值 == Math.rint(值) ? Long.toString((long) 值) : Double.toString(值);
	}
}
