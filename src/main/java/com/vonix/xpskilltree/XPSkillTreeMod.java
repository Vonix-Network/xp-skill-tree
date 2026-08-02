package com.vonix.xpskilltree;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(XPSkillTreeMod.MODID)
public final class XPSkillTreeMod {
    public static final String MODID = "xpskilltree";
    public XPSkillTreeMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ModCapabilities::register);
        MinecraftForge.EVENT_BUS.register(this);
    }
    private void commonSetup(FMLCommonSetupEvent e) { Network.register(); }
    @SubscribeEvent public void login(PlayerEvent.PlayerLoggedInEvent e) { if (e.getPlayer() instanceof ServerPlayer p) Network.sync(p); }
    @SubscribeEvent public void respawn(PlayerEvent.PlayerRespawnEvent e) { if (e.getPlayer() instanceof ServerPlayer p) Network.sync(p); }
    @SubscribeEvent public void changeDim(PlayerEvent.PlayerChangedDimensionEvent e) { if (e.getPlayer() instanceof ServerPlayer p) Network.sync(p); }
}
