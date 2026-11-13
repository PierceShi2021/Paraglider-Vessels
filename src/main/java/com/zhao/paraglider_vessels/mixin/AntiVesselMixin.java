package com.zhao.paraglider_vessels.mixin;

import com.zhao.paraglider_vessels.ModItems;
import com.zhao.paraglider_vessels.VesselData;
import com.zhao.paraglider_vessels.VesselEffects;
import com.zhao.paraglider_vessels.VesselType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tictim.paraglider.ParagliderUtils;
import tictim.paraglider.api.vessel.VesselContainer;

/**
 * 原 AntiVesselItem.use 写死只卸载心之容器与耐力容器，且两容器都为 0 时直接返回成功、不消耗物品。
 * 这里在服务端完整接管：额外卸载三种新容器并返还对应物品，精华按原设定不返还。
 * 目标类用字符串指定（第三方模组在开发环境是反混淆的），运行时按真实 SRG 名 m_7203_ 注入。
 */
@Mixin(targets = "tictim.paraglider.contents.item.AntiVesselItem", remap = false)
public class AntiVesselMixin {
	private static final String USE_DESC =
			"(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;" +
					"Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;";

	@Inject(method = "m_7203_" + USE_DESC, at = @At("HEAD"), cancellable = true, remap = false)
	private void 接管排斥容器(Level level, Player player, InteractionHand hand,
	                      CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		if (level.isClientSide) return; // 客户端让原方法照常返回，避免双端行为不一致

		ItemStack stack = player.getItemInHand(hand);
		VesselContainer 原容器 = VesselContainer.get(player);

		int 心 = 原容器.takeHeartContainers(Integer.MAX_VALUE, false, true);
		int 耐力 = 原容器.takeStaminaVessels(Integer.MAX_VALUE, false, true);
		int 卸载总数 = 心 + 耐力;

		for (VesselType type : VesselType.values()) {
			// 未启用的联动容器保留历史计数，不卸载也不返还
			if (!type.isEnabled() || !ModItems.已注册(type)) continue;
			int 数量 = VesselData.take(player, type, Integer.MAX_VALUE, false);
			if (数量 > 0) {
				ParagliderUtils.giveItem(player, new ItemStack(ModItems.item(type), 数量));
				卸载总数 += 数量;
			}
		}

		if (卸载总数 > 0) {
			VesselEffects.排斥容器(player);
			if (心 > 0) ParagliderUtils.giveItem(player,
					new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("paraglider", "heart_container")), 心));
			if (耐力 > 0) ParagliderUtils.giveItem(player,
					new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("paraglider", "stamina_vessel")), 耐力));
			stack.shrink(1);
			cir.setReturnValue(InteractionResultHolder.consume(stack));
		} else {
			cir.setReturnValue(InteractionResultHolder.success(stack));
		}
	}
}
