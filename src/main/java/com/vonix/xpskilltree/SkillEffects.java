package com.vonix.xpskilltree;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Method;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = XPSkillTreeMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SkillEffects {
    private static final UUID SPELL_DAMAGE_UUID = UUID.fromString("a6c6b163-72ae-4bf5-9b3f-e1c4e00b5001");
    private static final UUID MANA_UUID = UUID.fromString("a6c6b163-72ae-4bf5-9b3f-e1c4e00b5002");
    private SkillEffects() {}

    public static void apply(ServerPlayer player, SkillTreeData data) {
        double spell = 0.0D;
        double mana = 0.0D;
        for (String id : data.unlockedIds()) {
            SkillNode node = SkillNode.all().get(id);
            if (node == null) continue;
            if (node.effect() == SkillNode.Effect.SPELL_DAMAGE) spell += node.amount();
            if (node.effect() == SkillNode.Effect.MANA_REGEN) mana += node.amount() * 100.0D;
        }
        setModifier(player, "irons_spellbooks:spell_power", SPELL_DAMAGE_UUID, spell);
        setModifier(player, "irons_spellbooks:max_mana", MANA_UUID, mana);
    }

    private static void setModifier(ServerPlayer player, String id, UUID uuid, double amount) {
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(id));
        if (attribute == null) return;
        AttributeInstance instance = player.getAttribute(attribute);
        if (instance == null) return;
        instance.removeModifier(uuid);
        if (amount != 0.0D) instance.addTransientModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(uuid, XPSkillTreeMod.MODID + ".skill", amount, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADDITION));
    }

    @SubscribeEvent public static void login(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getPlayer() instanceof ServerPlayer p) p.getCapability(ModCapabilities.SKILLS).ifPresent(d -> apply(p, d));
    }
    @SubscribeEvent public static void respawn(net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent e) {
        if (e.getPlayer() instanceof ServerPlayer p) p.getCapability(ModCapabilities.SKILLS).ifPresent(d -> apply(p, d));
    }
    @SubscribeEvent public static void tick(TickEvent.PlayerTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !(e.player instanceof ServerPlayer p) || p.level.isClientSide || p.tickCount % 20 != 0) return;
        p.getCapability(ModCapabilities.SKILLS).ifPresent(d -> {
            double regen = d.unlockedIds().stream().map(SkillNode.all()::get).filter(n -> n != null && n.effect() == SkillNode.Effect.MANA_REGEN).mapToDouble(SkillNode::amount).sum();
            if (regen <= 0 || !net.minecraftforge.fml.ModList.get().isLoaded("irons_spellbooks")) return;
            try {
                Class<?> magic = Class.forName("io.redspace.ironsspellbooks.capabilities.magic.PlayerMagicData");
                Method getter = magic.getMethod("getPlayerMagicData", net.minecraft.world.entity.LivingEntity.class);
                Object data = getter.invoke(null, p);
                Method addMana = magic.getMethod("addMana", int.class);
                addMana.invoke(data, Math.max(1, (int) Math.round(regen * 2.0D)));
            } catch (ReflectiveOperationException ignored) {
                // Optional dependency not installed or API changed; attribute bonuses remain safe.
            }
        });
    }
}
