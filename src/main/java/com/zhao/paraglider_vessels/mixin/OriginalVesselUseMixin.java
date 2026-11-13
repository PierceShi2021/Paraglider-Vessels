package com.zhao.paraglider_vessels.mixin;

import com.zhao.paraglider_vessels.VesselEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 给原模组三个容器（心之容器 / 耐力容器 / 精华）的使用补上「叮」音效。
 * 注入到抽象父类 VesselItem 的 use 返回处，只在服务端且使用成功时播放；
 * 心之容器与耐力容器原本已有自己的粒子，精华原本没有粒子，所以只给精华补耐力容器同款粒子。
 */
@Mixin(targets = "tictim.paraglider.contents.item.VesselItem", remap = false)
public class OriginalVesselUseMixin {
	private static final String USE_DESC =
			"(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;" +
					"Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResultHolder;";

	@Inject(method = "m_7203_" + USE_DESC, at = @At("RETURN"), remap = false)
	private void 补容器使用音效(Level level, Player player, InteractionHand hand,
	                        CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
		if (level.isClientSide) return;
		InteractionResultHolder<ItemStack> 结果 = cir.getReturnValue();
		if (结果 == null || 结果.getResult() == InteractionResult.FAIL) return;

		Item 精华 = ForgeRegistries.ITEMS.getValue(new ResourceLocation("paraglider", "essence"));
		if (精华 != null && player.getItemInHand(hand).is(精华)) VesselEffects.粒子(player);
		VesselEffects.音效(player, SoundEvents.EXPERIENCE_ORB_PICKUP);
	}
}
