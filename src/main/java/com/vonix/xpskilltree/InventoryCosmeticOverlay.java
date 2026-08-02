package com.vonix.xpskilltree;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent.InitScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Adds the cosmetic wardrobe directly beside the vanilla player inventory. */
@Mod.EventBusSubscriber(modid = XPSkillTreeMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class InventoryCosmeticOverlay {
    private static final int PANEL_X = 180;
    private static final int PANEL_W = 86;
    private static final int ROW_H = 24;
    private static final String[] NAMES = {"Boots", "Legs", "Chest", "Helm"};

    private InventoryCosmeticOverlay() {}

    @SubscribeEvent
    public static void addToInventory(InitScreenEvent.Post event) {
        if (!(event.getScreen() instanceof InventoryScreen screen)) return;
        int x = screen.getGuiLeft() + PANEL_X;
        int y = screen.getGuiTop();
        event.addListener(new Button(x, y, PANEL_W, 20, new TextComponent("Cosmetics"), button ->
                Network.CHANNEL.sendToServer(new Network.CosmeticTogglePacket(!ClientState.cosmetics().enabled()))));
        for (int i = 0; i < 4; i++) {
            final int slot = i;
            int rowY = y + 24 + i * ROW_H;
            event.addListener(new Button(x, rowY, 42, 20, new TextComponent(NAMES[i]), button ->
                    Network.CHANNEL.sendToServer(new Network.CosmeticHeldPacket(slot))));
            event.addListener(new Button(x + 44, rowY, 42, 20, new TextComponent("Clear"), button ->
                    Network.CHANNEL.sendToServer(new Network.CosmeticClearPacket(slot))));
        }
    }
}
