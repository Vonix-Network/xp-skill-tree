package com.vonix.xpskilltree;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = XPSkillTreeMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ModCapabilities {
    public static final Capability<SkillTreeData> SKILLS = CapabilityManager.get(new CapabilityToken<SkillTreeData>() {});
    private static final ResourceLocation KEY = new ResourceLocation(XPSkillTreeMod.MODID, "skills");
    private ModCapabilities() {}
    public static void register(RegisterCapabilitiesEvent e) { e.register(SkillTreeData.class); }
    @SubscribeEvent public static void attach(AttachCapabilitiesEvent<net.minecraft.world.entity.Entity> e) {
        if (e.getObject() instanceof net.minecraft.world.entity.player.Player) e.addCapability(KEY, new SkillProvider());
    }
    @SubscribeEvent public static void clone(PlayerEvent.Clone e) {
        e.getOriginal().reviveCaps();
        e.getOriginal().getCapability(SKILLS).ifPresent(old -> e.getPlayer().getCapability(SKILLS).ifPresent(now -> now.copyFrom(old)));
        e.getOriginal().invalidateCaps();
    }
    public static final class SkillProvider implements net.minecraftforge.common.capabilities.ICapabilityProvider, net.minecraftforge.common.util.INBTSerializable<CompoundTag> {
        private final SkillTreeData data = new SkillTreeData();
        private final LazyOptional<SkillTreeData> optional = LazyOptional.of(() -> data);
        @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) { return cap == SKILLS ? optional.cast() : LazyOptional.empty(); }
        @Override public CompoundTag serializeNBT() { return data.serializeNBT(); }
        @Override public void deserializeNBT(CompoundTag nbt) { data.deserializeNBT(nbt); }
    }
}
