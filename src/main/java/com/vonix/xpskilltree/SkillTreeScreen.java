package com.vonix.xpskilltree;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.network.PacketDistributor;

public final class SkillTreeScreen extends Screen {
    private static final int NODE_W = 118, NODE_H = 42;
    private double zoom = 1.0;
    private int panX, panY;
    public SkillTreeScreen() { super(new TranslatableComponent("screen.xpskilltree.title")); }
    @Override protected void init() { addRenderableWidget(new Button(10, 10, 80, 20, new TextComponent("Close"), b -> onClose())); }
    @Override public void render(PoseStack pose, int mx, int my, float partial) {
        renderBackground(pose);
        drawCenteredString(pose, font, title, width / 2, 14, 0xFFFFFF);
        drawString(pose, font, new TranslatableComponent("screen.xpskilltree.points", Minecraft.getInstance().player == null ? 0 : Minecraft.getInstance().player.experienceLevel), 10, 38, 0x80FF80);
        pose.pushPose(); pose.translate(width / 2f + panX, height / 2f + panY, 0); pose.scale((float) zoom, (float) zoom, 1);
        for (SkillNode n : SkillNode.all().values()) {
            if (n.prerequisite() != null) { SkillNode p = SkillNode.all().get(n.prerequisite()); GuiComponent.fill(pose, p.x() - 2, p.y() - 2, n.x() + 2, n.y() + 2, 0xFF777777); }
        }
        for (SkillNode n : SkillNode.all().values()) drawNode(pose, n, mx, my);
        pose.popPose();
        super.render(pose, mx, my, partial);
    }
    private void drawNode(PoseStack pose, SkillNode n, int mx, int my) {
        boolean unlocked = ClientState.data().unlocked(n.id());
        int color = unlocked ? 0xFF2E9E52 : 0xFF393A4A;
        GuiComponent.fill(pose, n.x() - NODE_W / 2, n.y() - NODE_H / 2, n.x() + NODE_W / 2, n.y() + NODE_H / 2, color);
        GuiComponent.fill(pose, n.x() - NODE_W / 2 + 1, n.y() - NODE_H / 2 + 1, n.x() + NODE_W / 2 - 1, n.y() + NODE_H / 2 - 1, 0xFF11131D);
        drawCenteredString(pose, font, n.name(), n.x(), n.y() - 8, unlocked ? 0x80FF80 : 0xFFFFFF);
        drawCenteredString(pose, font, unlocked ? new TranslatableComponent("screen.xpskilltree.unlocked") : new TranslatableComponent("screen.xpskilltree.cost", n.cost()), n.x(), n.y() + 7, 0xFFB0B0B0);
    }
    private boolean over(SkillNode n, double x, double y) { double sx = width / 2d + panX + n.x() * zoom, sy = height / 2d + panY + n.y() * zoom; return x >= sx - NODE_W * zoom / 2 && x <= sx + NODE_W * zoom / 2 && y >= sy - NODE_H * zoom / 2 && y <= sy + NODE_H * zoom / 2; }
    @Override public boolean mouseClicked(double x, double y, int button) { if (button == 0) for (SkillNode n : SkillNode.all().values()) if (over(n, x, y) && !ClientState.data().unlocked(n.id())) { Network.CHANNEL.sendToServer(new Network.UnlockPacket(n.id())); return true; } return super.mouseClicked(x, y, button); }
    @Override public boolean mouseScrolled(double x, double y, double delta) { zoom = Math.max(.65, Math.min(1.5, zoom + delta * .1)); return true; }
    @Override public boolean isPauseScreen() { return false; }
}
