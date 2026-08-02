package com.vonix.xpskilltree;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.IdentityHashMap;
import java.util.Map;

/** Client-only visual adapter: temporarily feeds cosmetic armor to vanilla player armor layers. */
@Mod.EventBusSubscriber(modid = XPSkillTreeMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CosmeticArmorRenderer {
    private static final Map<Player, ItemStack[]> RESTORE = new IdentityHashMap<>();
    private CosmeticArmorRenderer() {}

    @SubscribeEvent
    public static void before(RenderPlayerEvent.Pre event) {
        Player player = event.getPlayer();
        if (!ClientState.cosmetics().enabled() || RESTORE.containsKey(player)) return;
        ItemStack[] old = new ItemStack[4];
        for (int slot = 0; slot < 4; slot++) {
            old[slot] = player.getInventory().getArmor(slot).copy();
            player.getInventory().armor.set(slot, ClientState.cosmetics().get(slot).copy());
        }
        RESTORE.put(player, old);
    }

    @SubscribeEvent
    public static void after(RenderPlayerEvent.Post event) {
        ItemStack[] old = RESTORE.remove(event.getPlayer());
        if (old == null) return;
        for (int slot = 0; slot < 4; slot++) playerArmor(event.getPlayer(), slot, old[slot]);
    }

    private static void playerArmor(Player player, int slot, ItemStack stack) {
        player.getInventory().armor.set(slot, stack);
    }
}
