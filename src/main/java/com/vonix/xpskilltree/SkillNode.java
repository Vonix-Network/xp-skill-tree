package com.vonix.xpskilltree;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SkillNode {
    public enum Effect { SPELL_DAMAGE, MANA_REGEN }
    private final String id;
    private final String name;
    private final int x;
    private final int y;
    private final int cost;
    private final String prerequisite;
    private final Effect effect;
    private final double amount;

    public SkillNode(String id, String name, int x, int y, int cost, String prerequisite, Effect effect, double amount) {
        this.id = id; this.name = name; this.x = x; this.y = y; this.cost = cost;
        this.prerequisite = prerequisite; this.effect = effect; this.amount = amount;
    }
    public String id() { return id; }
    public String name() { return name; }
    public int x() { return x; }
    public int y() { return y; }
    public int cost() { return cost; }
    public String prerequisite() { return prerequisite; }
    public Effect effect() { return effect; }
    public double amount() { return amount; }

    private static final Map<String, SkillNode> NODES;
    static {
        Map<String, SkillNode> m = new LinkedHashMap<>();
        m.put("root", new SkillNode("root", "Arcane Insight", 0, -100, 1, null, Effect.SPELL_DAMAGE, 0.03));
        m.put("fire_adept", new SkillNode("fire_adept", "Fire Adept", -150, 0, 2, "root", Effect.SPELL_DAMAGE, 0.05));
        m.put("fire_mastery", new SkillNode("fire_mastery", "Fire Mastery", -150, 110, 3, "fire_adept", Effect.SPELL_DAMAGE, 0.08));
        m.put("mana_focus", new SkillNode("mana_focus", "Mana Focus", 150, 0, 2, "root", Effect.MANA_REGEN, 0.10));
        m.put("mana_surge", new SkillNode("mana_surge", "Mana Surge", 150, 110, 3, "mana_focus", Effect.MANA_REGEN, 0.15));
        NODES = Collections.unmodifiableMap(m);
    }
    public static Map<String, SkillNode> all() { return NODES; }
}
