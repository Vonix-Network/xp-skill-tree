package com.vonix.xpskilltree;

import net.minecraft.nbt.CompoundTag;

public final class ClientState {
    private static final SkillTreeData DATA = new SkillTreeData();
    private ClientState() {}
    public static SkillTreeData data() { return DATA; }
    public static void load(CompoundTag tag) { DATA.deserializeNBT(tag); }
}
