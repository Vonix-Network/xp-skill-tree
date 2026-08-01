package com.vonix.xpskilltree;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/** Immutable definition of every node shown in the client tree. */
public final class SkillNode {
    public enum Branch { CORE, ARCANE, FLAME, FROST, STORM, GUARDIAN, RANGER, VOID, VITALITY }
    public enum Effect { SPELL_DAMAGE, MANA_REGEN }

    private final String id, name, description, prerequisite;
    private final int x, y, cost, maxRank;
    private final Branch branch;
    private final Effect effect;
    private final double amount;

    public SkillNode(String id, String name, String description, int x, int y, int cost,
                     int maxRank, String prerequisite, Branch branch, Effect effect, double amount) {
        this.id = id; this.name = name; this.description = description; this.x = x; this.y = y;
        this.cost = cost; this.maxRank = maxRank; this.prerequisite = prerequisite;
        this.branch = branch; this.effect = effect; this.amount = amount;
    }
    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public int x() { return x; }
    public int y() { return y; }
    public int cost() { return cost; }
    public int maxRank() { return maxRank; }
    public String prerequisite() { return prerequisite; }
    public Branch branch() { return branch; }
    public Effect effect() { return effect; }
    public double amount() { return amount; }

    private static final Map<String, SkillNode> NODES;
    static {
        Map<String, SkillNode> m = new LinkedHashMap<>();
        add(m, "root", "Awakening", "The source of your growing power.", 0, 0, 1, 1, null, Branch.CORE, Effect.SPELL_DAMAGE, .03);
        String[][] branches = {
            {"arcane", "Arcane Weave", "ARCANE", "SPELL_DAMAGE", "-135", "-70"},
            {"flame", "Emberheart", "FLAME", "SPELL_DAMAGE", "-135", "70"},
            {"frost", "Winter's Grasp", "FROST", "SPELL_DAMAGE", "-50", "145"},
            {"storm", "Stormcaller", "STORM", "SPELL_DAMAGE", "50", "145"},
            {"guardian", "Iron Resolve", "GUARDIAN", "MANA_REGEN", "135", "70"},
            {"ranger", "Wayfinder", "RANGER", "MANA_REGEN", "135", "-70"},
            {"void", "Void Walker", "VOID", "SPELL_DAMAGE", "50", "-145"},
            {"vitality", "Evergreen", "VITALITY", "MANA_REGEN", "-50", "-145"}
        };
        for (String[] b : branches) {
            Branch branch = Branch.valueOf(b[2]); Effect effect = Effect.valueOf(b[3]);
            int bx = Integer.parseInt(b[4]), by = Integer.parseInt(b[5]);
            String root = b[0] + "_1";
            add(m, root, b[1], "Begin the " + b[1] + " path.", bx, by, 2, 1, "root", branch, effect, effect == Effect.SPELL_DAMAGE ? .04 : .05);
            for (int i = 2; i <= 5; i++) {
                String id = b[0] + "_" + i;
                double t = (i - 1) / 4.0;
                int x = (int)Math.round(bx * (1.0 - t * .38));
                int y = (int)Math.round(by * (1.0 - t * .38));
                String name = b[1] + " " + (i == 5 ? "Mastery" : "I").replace("I", roman(i));
                if (i == 5) name = b[1] + " Mastery";
                add(m, id, name, "Increase your power along this branch.", x, y, i + 1, i == 5 ? 2 : 1, b[0] + "_" + (i - 1), branch, effect, effect == Effect.SPELL_DAMAGE ? .035 : .06);
            }
        }
        NODES = Collections.unmodifiableMap(m);
    }
    private static String roman(int n) { return n == 2 ? "II" : n == 3 ? "III" : "IV"; }
    private static void add(Map<String, SkillNode> m, String id, String name, String desc, int x, int y, int cost, int max,
                            String req, Branch branch, Effect effect, double amount) {
        m.put(id, new SkillNode(id, name, desc, x, y, cost, max, req, branch, effect, amount));
    }
    public static Map<String, SkillNode> all() { return NODES; }
}
