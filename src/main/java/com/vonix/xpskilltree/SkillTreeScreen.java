package com.vonix.xpskilltree;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.ArrayList;
import java.util.List;

public final class SkillTreeScreen extends Screen {
    private static final int PANEL_W = 228;
    private static final int NODE_R = 7;
    private double zoom = .72D;
    private float panX, panY;
    private boolean dragging;
    private double lastMouseX, lastMouseY;
    private String selectedId = "root";

    public SkillTreeScreen() { super(new TranslatableComponent("screen.xpskilltree.title")); }

    @Override protected void init() {
        addRenderableWidget(new Button(10, 10, 54, 20, new TextComponent("Close"), b -> onClose()));
    }

    @Override public void render(PoseStack pose, int mouseX, int mouseY, float partial) {
        renderBackground(pose);
        GuiComponent.fill(pose, 0, 0, width, height, 0x66000000);
        int left = 18, top = 24, right = width - 18, bottom = height - 18;
        GuiComponent.fill(pose, left, top, right, bottom, 0xE8121824);
        GuiComponent.fill(pose, left, top, right, top + 30, 0xF20D111B);
        GuiComponent.fill(pose, left, top + 30, right, top + 31, 0xFF3A4654);
        drawCenteredString(pose, font, new TextComponent("TALENT TREE"), width / 2, top + 10, 0xFFFFFFFF);
        drawString(pose, font, new TextComponent("✦ " + availableLevels() + " XP LEVELS AVAILABLE"), left + 12, top + 10, 0xFFFFD56A);
        drawString(pose, font, new TextComponent("XP " + experience()), right - 112, top + 10, 0xFFB9C7D6);
        drawString(pose, font, new TextComponent("✧ " + ClientState.data().unlockedIds().size() + "/" + SkillNode.all().size()), right - 48, top + 10, 0xFFFFD56A);

        int graphRight = right - PANEL_W - 8;
        drawInfo(pose, left + 12, top + 44);
        drawLegend(pose, left + 12, top + 98);
        GuiComponent.fill(pose, graphRight, top + 38, graphRight + 1, bottom - 8, 0xFF3A4654);
        drawGraph(pose, left + 12, top + 38, graphRight - 10, bottom - 8, mouseX, mouseY);
        drawStats(pose, graphRight + 10, top + 42, right - 10, bottom - 10);
        super.render(pose, mouseX, mouseY, partial);
        drawTooltipIfHovered(pose, mouseX, mouseY);
    }

    private void drawInfo(PoseStack p, int x, int y) {
        GuiComponent.fill(p, x, y, x + 128, y + 48, 0xB9141B27);
        drawString(p, font, new TextComponent("POINTS AVAILABLE"), x + 6, y + 6, 0xFFFFD56A);
        drawString(p, font, new TextComponent(String.valueOf(availableLevels())), x + 98, y + 6, 0xFFFFFFFF);
        drawString(p, font, new TextComponent("POINTS SPENT"), x + 6, y + 20, 0xFF9EABB9);
        drawString(p, font, new TextComponent(String.valueOf(spentLevels())), x + 98, y + 20, 0xFFFFFFFF);
        drawString(p, font, new TextComponent("NODES UNLOCKED"), x + 6, y + 34, 0xFF9EABB9);
        drawString(p, font, new TextComponent(ClientState.data().unlockedIds().size() + "/" + SkillNode.all().size()), x + 98, y + 34, 0xFFFFFFFF);
    }

    private void drawLegend(PoseStack p, int x, int y) {
        drawString(p, font, new TextComponent("HOW TO USE"), x, y, 0xFFFFD56A);
        drawString(p, font, new TextComponent("Click a bright node to unlock it"), x, y + 14, 0xFFB8C4D2);
        drawString(p, font, new TextComponent("Click + drag to move  |  Wheel to zoom"), x, y + 28, 0xFF8F9BAA);
        drawString(p, font, new TextComponent("Green unlocked  Gold available  Gray locked"), x, y + 42, 0xFF8F9BAA);
    }

    private void drawGraph(PoseStack p, int gx, int gy, int gw, int gh, int mouseX, int mouseY) {
        p.pushPose();
        p.translate(gx + gw / 2f + panX, gy + gh / 2f + panY, 0);
        p.scale((float) zoom, (float) zoom, 1);
        for (SkillNode n : SkillNode.all().values()) {
            if (n.prerequisite() == null) continue;
            SkillNode parent = SkillNode.all().get(n.prerequisite());
            boolean active = ClientState.data().unlocked(n.id()) && ClientState.data().unlocked(parent.id());
            line(p, parent.x(), parent.y(), n.x(), n.y(), active ? branchColor(n.branch()) : 0xFF4C5968);
        }
        for (SkillNode n : SkillNode.all().values()) drawNode(p, n);
        p.popPose();
    }

    private void drawNode(PoseStack p, SkillNode n) {
        boolean unlocked = ClientState.data().unlocked(n.id());
        boolean available = ClientState.data().canUnlock(n.id());
        boolean selected = selectedId.equals(n.id());
        int color = selected ? 0xFFFFE28A : unlocked ? branchColor(n.branch()) : available ? 0xFFE5B84F : 0xFF8793A1;
        if (n.id().equals("root")) {
            circle(p, n.x(), n.y(), 22, 0xFF172230, color);
            circle(p, n.x(), n.y(), 15, 0xFF101722, selected ? 0xFFFFFFFF : color);
            circle(p, n.x(), n.y(), 5, unlocked ? color : 0xFF586575, unlocked ? color : 0xFF586575);
        } else {
            circle(p, n.x(), n.y(), NODE_R + (selected ? 3 : 0), 0xFF111722, color);
            if (unlocked) circle(p, n.x(), n.y(), 3, color, color);
        }
    }

    private void drawStats(PoseStack p, int x, int y, int right, int bottom) {
        GuiComponent.fill(p, x, y, right, bottom, 0xE8171E2A);
        section(p, "PLAYER ATTRIBUTES", x + 8, y + 8);
        row(p, "Strength", "10", x + 8, y + 24); row(p, "Dexterity", "10", x + 8, y + 37);
        row(p, "Vitality", value("health"), x + 8, y + 50); row(p, "Intelligence", value("spell"), x + 8, y + 63);
        row(p, "Perception", "10", x + 8, y + 76); row(p, "Luck", "10", x + 8, y + 89);
        section(p, "PLAYER STATISTICS", x + 8, y + 110);
        row(p, "Health", value("health"), x + 8, y + 126); row(p, "Mana", value("mana"), x + 8, y + 139);
        row(p, "Spell Power", value("spell"), x + 8, y + 152); row(p, "Cooldown", value("cooldown"), x + 8, y + 165);
        section(p, "COMBAT", x + 8, y + 186);
        row(p, "Damage", value("spell"), x + 8, y + 202); row(p, "Armor", value("armor"), x + 8, y + 215);
        section(p, "RESISTANCES", x + 8, y + 236);
        row(p, "Physical", "0%", x + 8, y + 252); row(p, "Fire", "0%", x + 8, y + 265);
        row(p, "Frost", "0%", x + 8, y + 278); row(p, "Magic", "0%", x + 8, y + 291);
        section(p, "ABILITIES & UTILITY", x + 8, y + 312);
        row(p, "Move Speed", value("move"), x + 8, y + 328); row(p, "Mana Regen", value("regen"), x + 8, y + 341);
    }

    private void section(PoseStack p, String text, int x, int y) { drawString(p, font, new TextComponent(text), x, y, 0xFFFFD56A); }
    private void row(PoseStack p, String label, String value, int x, int y) { drawString(p, font, new TextComponent(label), x, y, 0xFFB8C4D2); drawString(p, font, new TextComponent(value), x + 128, y, 0xFFFFFFFF); }
    private String value(String type) {
        double total = 0;
        for (String id : ClientState.data().unlockedIds()) { SkillNode n = SkillNode.all().get(id); if (n == null) continue;
            if (type.equals("spell") && n.effect() == SkillNode.Effect.SPELL_DAMAGE) total += n.amount() * 100;
            if (type.equals("mana") && n.effect() == SkillNode.Effect.MAX_MANA) total += n.amount();
            if (type.equals("cooldown") && n.effect() == SkillNode.Effect.COOLDOWN) total += n.amount() * 100;
            if (type.equals("health") && n.effect() == SkillNode.Effect.HEALTH) total += n.amount();
            if (type.equals("armor") && n.effect() == SkillNode.Effect.ARMOR) total += n.amount();
            if (type.equals("move") && n.effect() == SkillNode.Effect.MOVE_SPEED) total += n.amount() * 100;
            if (type.equals("regen") && n.effect() == SkillNode.Effect.MANA_REGEN) total += n.amount() * 100;
        }
        return type.equals("cooldown") || type.equals("move") || type.equals("regen") ? String.format("%.0f%%", total) : String.format("%.0f", total);
    }

    private void drawTooltipIfHovered(PoseStack p, int mx, int my) {
        SkillNode n = hovered(mx, my); if (n == null) return;
        List<Component> lines = new ArrayList<>(); lines.add(new TextComponent(n.name())); lines.add(new TextComponent(n.description()));
        lines.add(new TextComponent("Cost: " + n.cost() + " XP levels"));
        if (n.prerequisite() != null) lines.add(new TextComponent("Requires: " + SkillNode.all().get(n.prerequisite()).name()));
        if (ClientState.data().unlocked(n.id())) lines.add(new TextComponent("Unlocked"));
        else if (ClientState.data().canUnlock(n.id())) lines.add(new TextComponent("Available - click to unlock"));
        else lines.add(new TextComponent("Locked - unlock the prerequisite first"));
        List<net.minecraft.util.FormattedCharSequence> tooltip = new ArrayList<>(); for (Component line : lines) tooltip.add(line.getVisualOrderText()); renderTooltip(p, tooltip, mx, my);
    }

    private SkillNode hovered(double mx, double my) {
        int left = 18, top = 24;
        int right = width - 18, bottom = height - 18;
        int graphRight = right - PANEL_W - 8;
        int gx = left + 12, gy = top + 38;
        int gw = graphRight - 10, gh = bottom - 8;
        double cx = gx + gw / 2d + panX, cy = gy + gh / 2d + panY;
        for (SkillNode n : SkillNode.all().values()) {
            double x = cx + n.x() * zoom, y = cy + n.y() * zoom;
            double radius = n.id().equals("root") ? 28 : Math.max(12, 14 * zoom);
            if (Math.hypot(mx - x, my - y) <= radius) return n;
        }
        return null;
    }

    private void line(PoseStack p, int x1, int y1, int x2, int y2, int color) { int dx = x2 - x1, dy = y2 - y1, steps = Math.max(Math.abs(dx), Math.abs(dy)); for (int i = 0; i <= steps; i++) { int x = x1 + dx * i / Math.max(1, steps), y = y1 + dy * i / Math.max(1, steps); GuiComponent.fill(p, x - 1, y - 1, x + 2, y + 2, color); } }
    private void circle(PoseStack p, int cx, int cy, int r, int fill, int border) { for (int y = -r; y <= r; y++) for (int x = -r; x <= r; x++) if (x*x + y*y <= r*r) GuiComponent.fill(p, cx+x, cy+y, cx+x+1, cy+y+1, x*x+y*y >= (r-2)*(r-2) ? border : fill); }
    private int branchColor(SkillNode.Branch b) { switch (b) { case FIRE: return 0xFFC87854; case FROST: return 0xFF75B9D1; case LIGHTNING: return 0xFFE2D16B; case ARCANE: return 0xFFB28AD7; case HOLY: return 0xFFD7B768; case VOID: return 0xFF9A76C4; default: return 0xFFD7DCE4; } }
    private int availableLevels() { return Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.experienceLevel; }
    private int experience() { return Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.totalExperience; }
    private int spentLevels() { int total = 0; for (String id : ClientState.data().unlockedIds()) { SkillNode n = SkillNode.all().get(id); if (n != null) total += n.cost(); } return total; }
    @Override public boolean mouseClicked(double x, double y, int button) { if (button == 0) { SkillNode n = hovered(x, y); if (n != null) { selectedId = n.id(); if (!ClientState.data().unlocked(n.id())) Network.CHANNEL.sendToServer(new Network.UnlockPacket(n.id())); return true; } dragging = true; lastMouseX = x; lastMouseY = y; } return super.mouseClicked(x, y, button); }
    @Override public boolean mouseReleased(double x, double y, int button) { if (button == 0) dragging = false; return super.mouseReleased(x, y, button); }
    @Override public boolean mouseDragged(double x, double y, int button, double dx, double dy) { if (dragging && button == 0) { panX += x - lastMouseX; panY += y - lastMouseY; lastMouseX = x; lastMouseY = y; return true; } return super.mouseDragged(x, y, button, dx, dy); }
    @Override public boolean mouseScrolled(double x, double y, double delta) { zoom = Math.max(.45, Math.min(1.35, zoom + delta * .08)); return true; }
    @Override public boolean isPauseScreen() { return false; }
}
