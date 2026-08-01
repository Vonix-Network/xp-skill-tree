package com.vonix.xpskilltree;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import java.util.HashSet;
import java.util.Set;

public final class SkillTreeData implements INBTSerializable<CompoundTag> {
    private final Set<String> unlocked = new HashSet<>();
    public boolean unlocked(String id) { return unlocked.contains(id); }
    public boolean canUnlock(String id) {
        SkillNode n = SkillNode.all().get(id);
        return n != null && !unlocked(id) && (n.prerequisite() == null || unlocked(n.prerequisite()));
    }
    public void unlock(String id) { unlocked.add(id); }
    public Set<String> unlockedIds() { return new HashSet<>(unlocked); }
    @Override public CompoundTag serializeNBT() {
        CompoundTag t = new CompoundTag();
        for (String id : unlocked) t.putBoolean(id, true);
        return t;
    }
    @Override public void deserializeNBT(CompoundTag t) {
        unlocked.clear();
        for (String id : SkillNode.all().keySet()) if (t.getBoolean(id)) unlocked.add(id);
    }
    public void copyFrom(SkillTreeData other) { unlocked.clear(); unlocked.addAll(other.unlocked); }
}
