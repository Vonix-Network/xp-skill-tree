package com.vonix.xpskilltree;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

/** Radial, pannable talent tree inspired by the supplied reference UI. */
public final class SkillTreeScreen extends Screen {
    private static final int PANEL_W = 190;
    private double zoom = .9;
    private int panX, panY;
    private boolean dragging;
    private double dragX, dragY;
    private SkillNode hovered;

    public SkillTreeScreen() { super(new TranslatableComponent("screen.xpskilltree.title")); }
    @Override protected void init() {
        addRenderableWidget(new Button(8, 8, 62, 20, new TextComponent("Close"), b -> onClose()));
    }
    @Override public void render(PoseStack pose, int mx, int my, float partial) {
        renderBackground(pose);
        GuiComponent.fill(pose, 0, 0, width, height, 0xD9080B12);
        GuiComponent.fill(pose, 0, 0, width, 34, 0xFF171A27);
        drawCenteredString(pose, font, title, width / 2, 12, 0xFFF4F0D0);
        int levels = Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.experienceLevel;
        drawString(pose, font, new TranslatableComponent("screen.xpskilltree.points", levels), 78, 14, 0xFFE5C95B);
        drawString(pose, font, new TextComponent(SkillNode.all().size() + " skills"), width - PANEL_W - 68, 14, 0xFF9AA6C2);

        int treeRight = width - PANEL_W;
        pose.pushPose(); pose.translate(treeRight / 2f + panX, (height + 30) / 2f + panY, 0); pose.scale((float)zoom, (float)zoom, 1);
        for (SkillNode n : SkillNode.all().values()) if (n.prerequisite() != null) {
            SkillNode parent = SkillNode.all().get(n.prerequisite());
            drawConnector(pose, parent.x(), parent.y(), n.x(), n.y(), branchColor(n.branch()), ClientState.data().unlocked(n.id()));
        }
        hovered = null;
        for (SkillNode n : SkillNode.all().values()) {
            drawNode(pose, n);
            if (over(n, mx, my, treeRight)) hovered = n;
        }
        pose.popPose();
        drawDetails(pose, treeRight, hovered);
        super.render(pose, mx, my, partial);
    }
    private void drawDetails(PoseStack p, int x, SkillNode n) {
        GuiComponent.fill(p, x, 34, width, height, 0xEE111522);
        GuiComponent.fill(p, x, 34, x + 2, height, 0xFF343A51);
        drawString(p, font, new TextComponent("SKILL DETAILS"), x + 12, 48, 0xFFE5C95B);
        if (n == null) {
            drawString(p, font, new TextComponent("Hover over a node"), x + 12, 78, 0xFFB0B7C8);
            drawString(p, font, new TextComponent("to inspect its upgrade."), x + 12, 92, 0xFFB0B7C8);
            return;
        }
        boolean unlocked = ClientState.data().unlocked(n.id());
        drawString(p, font, new TextComponent(n.name()), x + 12, 76, branchColor(n.branch()) | 0xFF000000);
        drawString(p, font, new TextComponent(n.description()), x + 12, 96, 0xFFD6D9E2);
        drawString(p, font, new TextComponent("Branch: " + n.branch().name()), x + 12, 122, 0xFF929BB2);
        drawString(p, font, new TextComponent("Effect: " + (n.effect() == SkillNode.Effect.SPELL_DAMAGE ? "Spell power" : "Mana recovery")), x + 12, 138, 0xFF929BB2);
        drawString(p, font, new TextComponent(unlocked ? "UNLOCKED" : "Cost: " + n.cost() + " levels"), x + 12, 164, unlocked ? 0xFF69D18A : 0xFFE5C95B);
        if (n.prerequisite() != null) drawString(p, font, new TextComponent("Requires: " + SkillNode.all().get(n.prerequisite()).name()), x + 12, 180, 0xFF929BB2);
    }
    private void drawNode(PoseStack p, SkillNode n) {
        boolean unlocked = ClientState.data().unlocked(n.id());
        boolean available = ClientState.data().canUnlock(n.id());
        int r = n == hovered ? 15 : n == SkillNode.all().get("root") ? 17 : 12;
        int color = unlocked ? 0xFF69D18A : available ? branchColor(n.branch()) : 0xFF596074;
        GuiComponent.fill(p, n.x() - r - 2, n.y() - r - 2, n.x() + r + 2, n.y() + r + 2, 0xFF080A10);
        GuiComponent.fill(p, n.x() - r, n.y() - r, n.x() + r, n.y() + r, color);
        GuiComponent.fill(p, n.x() - r + 3, n.y() - r + 3, n.x() + r - 3, n.y() + r - 3, unlocked ? 0xFF193A2A : 0xFF171A25);
        if (n == hovered || n == SkillNode.all().get("root")) drawCenteredString(p, font, n.name(), n.x(), n.y() + r + 5, 0xFFE4E7EF);
    }
    private void drawConnector(PoseStack p, int x1, int y1, int x2, int y2, int color, boolean active) {
        int steps = Math.max(Math.abs(x2-x1), Math.abs(y2-y1));
        for (int i=0; i<=steps; i++) {
            int x = x1 + (x2-x1)*i/Math.max(1,steps), y = y1 + (y2-y1)*i/Math.max(1,steps);
            GuiComponent.fill(p, x-1, y-1, x+2, y+2, active ? color : (color & 0x55FFFFFF) | 0x44000000);
        }
    }
    private int branchColor(SkillNode.Branch b) {
        switch (b) {
            case FLAME: return 0xFFE58B4A; case FROST: return 0xFF75CBE8; case STORM: return 0xFFB49AF2;
            case GUARDIAN: return 0xFFE0C46A; case RANGER: return 0xFF79C58B; case VOID: return 0xFFB778D1;
            case VITALITY: return 0xFF83C889; case ARCANE: return 0xFF6E9FEA; default: return 0xFFE5C95B;
        }
    }
    private boolean over(SkillNode n, double mx, double my, int treeRight) {
        double sx = treeRight / 2d + panX + n.x()*zoom, sy = (height+30)/2d + panY + n.y()*zoom;
        double r = 18*zoom; return (mx-sx)*(mx-sx)+(my-sy)*(my-sy) <= r*r;
    }
    @Override public boolean mouseClicked(double x, double y, int button) {
        if (button == 0) {
            if (hovered != null && ClientState.data().canUnlock(hovered.id())) {
                Network.CHANNEL.sendToServer(new Network.UnlockPacket(hovered.id()));
                return true;
            }
            dragging = true; dragX=x; dragY=y; return true;
        }
        return super.mouseClicked(x,y,button);
    }
    @Override public boolean mouseReleased(double x, double y, int button) { dragging = false; return super.mouseReleased(x,y,button); }
    @Override public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
        if (dragging) { panX += (int)(x-dragX); panY += (int)(y-dragY); dragX=x; dragY=y; return true; }
        return super.mouseDragged(x,y,button,dx,dy);
    }
    @Override public boolean mouseScrolled(double x, double y, double delta) { zoom = Math.max(.55, Math.min(1.45, zoom + delta*.08)); return true; }
    @Override public boolean isPauseScreen() { return false; }
}
