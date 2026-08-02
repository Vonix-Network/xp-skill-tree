package com.vonix.xpskilltree;

import net.minecraft.nbt.CompoundTag;

public final class ClientState {
    private static final SkillTreeData DATA = new SkillTreeData();
    private static final CosmeticData COSMETICS = new CosmeticData();
    private ClientState() {}
    public static SkillTreeData data() { return DATA; }
    public static CosmeticData cosmetics() { return COSMETICS; }
    public static void load(CompoundTag tag) { DATA.deserializeNBT(tag); }
    public static void loadCosmetics(CompoundTag tag) { COSMETICS.deserializeNBT(tag); }
}
