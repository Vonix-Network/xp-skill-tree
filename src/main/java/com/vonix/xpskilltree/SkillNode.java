package com.vonix.xpskilltree;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class SkillNode {
    public enum Effect { SPELL_DAMAGE, MANA_REGEN, MAX_MANA, COOLDOWN, HEALTH, ARMOR, MOVE_SPEED }
    public enum Branch { CORE, FIRE, FROST, LIGHTNING, ARCANE, HOLY, VOID }

    private final String id;
    private final String name;
    private final String description;
    private final int x, y, cost;
    private final String prerequisite;
    private final Effect effect;
    private final Branch branch;
    private final double amount;

    public SkillNode(String id, String name, String description, int x, int y, int cost, String prerequisite,
                     Effect effect, Branch branch, double amount) {
        this.id = id; this.name = name; this.description = description; this.x = x; this.y = y;
        this.cost = cost; this.prerequisite = prerequisite; this.effect = effect; this.branch = branch; this.amount = amount;
    }
    public String id() { return id; }
    public String name() { return name; }
    public String description() { return description; }
    public int x() { return x; }
    public int y() { return y; }
    public int cost() { return cost; }
    public String prerequisite() { return prerequisite; }
    public Effect effect() { return effect; }
    public Branch branch() { return branch; }
    public double amount() { return amount; }

    private static final Map<String, SkillNode> NODES;
    static {
        Map<String, SkillNode> m = new LinkedHashMap<>();
        add(m, "root", "Arcane Nexus", "The source of your magical potential.", 0, 0, 1, null, Effect.SPELL_DAMAGE, Branch.CORE, .03);
        add(m, "ember_heart", "Ember Heart", "Increase spell power by 5%.", -100, -35, 2, "root", Effect.SPELL_DAMAGE, Branch.FIRE, .05);
        add(m, "kindled_veins", "Kindled Veins", "Increase spell power by 6%.", -180, -100, 2, "ember_heart", Effect.SPELL_DAMAGE, Branch.FIRE, .06);
        add(m, "flame_mastery", "Flame Mastery", "Increase spell power by 9%.", -260, -155, 3, "kindled_veins", Effect.SPELL_DAMAGE, Branch.FIRE, .09);
        add(m, "scorching_will", "Scorching Will", "Increase spell power by 12%.", -325, -65, 4, "kindled_veins", Effect.SPELL_DAMAGE, Branch.FIRE, .12);
        add(m, "inferno_core", "Inferno Core", "Increase spell power by 18%.", -390, -185, 5, "flame_mastery", Effect.SPELL_DAMAGE, Branch.FIRE, .18);

        add(m, "frost_ward", "Frost Ward", "Gain 2 armor while casting.", -95, 35, 2, "root", Effect.ARMOR, Branch.FROST, 2);
        add(m, "rime_skin", "Rime Skin", "Gain 4 armor.", -170, 105, 2, "frost_ward", Effect.ARMOR, Branch.FROST, 4);
        add(m, "glacial_focus", "Glacial Focus", "Increase maximum mana by 15.", -255, 165, 3, "rime_skin", Effect.MAX_MANA, Branch.FROST, 15);
        add(m, "absolute_zero", "Absolute Zero", "Gain 8 armor.", -335, 115, 4, "glacial_focus", Effect.ARMOR, Branch.FROST, 8);

        add(m, "storm_spark", "Storm Spark", "Reduce spell cooldowns by 3%.", 100, -35, 2, "root", Effect.COOLDOWN, Branch.LIGHTNING, .03);
        add(m, "charged_mind", "Charged Mind", "Reduce spell cooldowns by 4%.", 180, -100, 2, "storm_spark", Effect.COOLDOWN, Branch.LIGHTNING, .04);
        add(m, "chain_reaction", "Chain Reaction", "Increase spell power by 7%.", 260, -155, 3, "charged_mind", Effect.SPELL_DAMAGE, Branch.LIGHTNING, .07);
        add(m, "thunder_lord", "Thunder Lord", "Reduce spell cooldowns by 8%.", 330, -80, 4, "chain_reaction", Effect.COOLDOWN, Branch.LIGHTNING, .08);

        add(m, "mana_well", "Mana Well", "Increase maximum mana by 20.", 95, 35, 2, "root", Effect.MAX_MANA, Branch.ARCANE, 20);
        add(m, "deep_reserves", "Deep Reserves", "Increase maximum mana by 30.", 170, 105, 2, "mana_well", Effect.MAX_MANA, Branch.ARCANE, 30);
        add(m, "mana_surge", "Mana Surge", "Regenerate additional mana over time.", 250, 165, 3, "deep_reserves", Effect.MANA_REGEN, Branch.ARCANE, .12);
        add(m, "arcane_efficiency", "Arcane Efficiency", "Reduce spell cooldowns by 7%.", 335, 115, 4, "mana_surge", Effect.COOLDOWN, Branch.ARCANE, .07);
        add(m, "infinite_well", "Infinite Well", "Increase maximum mana by 60.", 395, 190, 5, "arcane_efficiency", Effect.MAX_MANA, Branch.ARCANE, 60);

        add(m, "vital_flame", "Vital Flame", "Increase maximum health by 2 hearts.", -55, 115, 2, "root", Effect.HEALTH, Branch.HOLY, 4);
        add(m, "radiant_body", "Radiant Body", "Increase maximum health by 3 hearts.", -110, 210, 3, "vital_flame", Effect.HEALTH, Branch.HOLY, 6);
        add(m, "guardian_light", "Guardian Light", "Increase armor by 6.", -180, 275, 4, "radiant_body", Effect.ARMOR, Branch.HOLY, 6);

        add(m, "void_step", "Void Step", "Increase movement speed by 3%.", 55, 115, 2, "root", Effect.MOVE_SPEED, Branch.VOID, .03);
        add(m, "shadow_stride", "Shadow Stride", "Increase movement speed by 5%.", 110, 210, 3, "void_step", Effect.MOVE_SPEED, Branch.VOID, .05);
        add(m, "void_mastery", "Void Mastery", "Increase spell power by 10%.", 180, 275, 4, "shadow_stride", Effect.SPELL_DAMAGE, Branch.VOID, .10);

        // Expanded spell-specialization layer: deeper caster choices for each school.
        add(m, "ember_burst", "Ember Burst", "Fire spells deal 8% more spell power.", -455, -250, 5, "inferno_core", Effect.SPELL_DAMAGE, Branch.FIRE, .08);
        add(m, "cinder_recall", "Cinder Recall", "Fire casting reduces cooldown pressure by 5%.", -520, -145, 6, "ember_burst", Effect.COOLDOWN, Branch.FIRE, .05);
        add(m, "pyroclastic_surge", "Pyroclastic Surge", "Heavy fire magic gains 14% spell power.", -585, -285, 7, "cinder_recall", Effect.SPELL_DAMAGE, Branch.FIRE, .14);

        add(m, "permafrost_aegis", "Permafrost Aegis", "Frost magic grants 5 additional armor.", -415, 205, 5, "absolute_zero", Effect.ARMOR, Branch.FROST, 5);
        add(m, "winter_reservoir", "Winter Reservoir", "Frost spells increase maximum mana by 35.", -490, 300, 6, "permafrost_aegis", Effect.MAX_MANA, Branch.FROST, 35);
        add(m, "everfrost", "Everfrost", "Frost casting improves spell power by 11%.", -580, 235, 7, "winter_reservoir", Effect.SPELL_DAMAGE, Branch.FROST, .11);

        add(m, "static_attunement", "Static Attunement", "Lightning spells gain 9% spell power.", 405, -185, 5, "thunder_lord", Effect.SPELL_DAMAGE, Branch.LIGHTNING, .09);
        add(m, "voltaic_memory", "Voltaic Memory", "Lightning casting reduces cooldowns by 10%.", 480, -285, 6, "static_attunement", Effect.COOLDOWN, Branch.LIGHTNING, .10);
        add(m, "tempest_crown", "Tempest Crown", "Mastered lightning magic gains 16% spell power.", 575, -205, 7, "voltaic_memory", Effect.SPELL_DAMAGE, Branch.LIGHTNING, .16);

        add(m, "quickened_channel", "Quickened Channel", "Arcane spells reduce cooldowns by 9%.", 420, 235, 5, "infinite_well", Effect.COOLDOWN, Branch.ARCANE, .09);
        add(m, "mana_conduit", "Mana Conduit", "Gain 45 maximum mana for sustained casting.", 500, 320, 6, "quickened_channel", Effect.MAX_MANA, Branch.ARCANE, 45);
        add(m, "archmage_focus", "Archmage Focus", "Arcane spells gain 15% spell power.", 590, 250, 7, "mana_conduit", Effect.SPELL_DAMAGE, Branch.ARCANE, .15);

        add(m, "radiant_lexicon", "Radiant Lexicon", "Holy spells gain 8% spell power.", -270, 365, 5, "guardian_light", Effect.SPELL_DAMAGE, Branch.HOLY, .08);
        add(m, "blessed_reserves", "Blessed Reserves", "Increase maximum mana by 40.", -360, 440, 6, "radiant_lexicon", Effect.MAX_MANA, Branch.HOLY, 40);
        add(m, "sanctified_casting", "Sanctified Casting", "Holy casting improves spell power by 13%.", -465, 390, 7, "blessed_reserves", Effect.SPELL_DAMAGE, Branch.HOLY, .13);

        add(m, "null_sigil", "Null Sigil", "Void spells gain 12% spell power.", 285, 390, 5, "void_mastery", Effect.SPELL_DAMAGE, Branch.VOID, .12);
        add(m, "event_horizon", "Event Horizon", "Void casting reduces cooldowns by 11%.", 390, 470, 6, "null_sigil", Effect.COOLDOWN, Branch.VOID, .11);
        add(m, "abyssal_apotheosis", "Abyssal Apotheosis", "Abyssal magic gains 17% spell power.", 510, 420, 7, "event_horizon", Effect.SPELL_DAMAGE, Branch.VOID, .17);
        NODES = Collections.unmodifiableMap(m);
    }
    private static void add(Map<String, SkillNode> m, String id, String name, String desc, int x, int y, int cost,
                             String prereq, Effect effect, Branch branch, double amount) {
        m.put(id, new SkillNode(id, name, desc, x, y, cost, prereq, effect, branch, amount));
    }
    public static Map<String, SkillNode> all() { return NODES; }
}
