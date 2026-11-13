package com.zhao.paraglider_vessels;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

/**
 * permanent 修饰器写进了玩家存档，所以登录、复活、换维都要重算一次，
 * 保证存档里的旧修饰器被清理并换成当前配置的数值。
 */
@Mod.EventBusSubscriber(modid = ParagliderVessels.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class VesselEvents {
	private VesselEvents() {
	}

	@SubscribeEvent
	public static void 登录(PlayerEvent.PlayerLoggedInEvent e) {
		VesselAttributes.refresh(e.getEntity());
	}

	@SubscribeEvent
	public static void 克隆(PlayerEvent.Clone e) {
		VesselData.copy(e.getOriginal(), e.getEntity());
		VesselAttributes.refresh(e.getEntity());
	}

	@SubscribeEvent
	public static void 换维(PlayerEvent.PlayerChangedDimensionEvent e) {
		VesselAttributes.refresh(e.getEntity());
	}

	@SubscribeEvent
	public static void 复活(PlayerEvent.PlayerRespawnEvent e) {
		VesselAttributes.refresh(e.getEntity());
	}

	/** 配置重载后对在线玩家全量重算，这个方法注册在 MOD 事件总线上。 */
	public static void 配置重载(ModConfigEvent.Reloading e) {
		if (!ParagliderVessels.MODID.equals(e.getConfig().getModId())) return;
		MinecraftServer 服务器 = ServerLifecycleHooks.getCurrentServer();
		if (服务器 == null) return;
		for (ServerPlayer 玩家 : 服务器.getPlayerList().getPlayers()) VesselAttributes.refresh(玩家);
	}
}
