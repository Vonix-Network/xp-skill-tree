package com.vonix.xpskilltree;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = XPSkillTreeMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientInit {
    public static final KeyMapping OPEN = new KeyMapping("key.xpskilltree.open", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, "key.categories.xpskilltree");
    private ClientInit() {}
    @SubscribeEvent public static void setup(FMLClientSetupEvent e) { e.enqueueWork(() -> net.minecraftforge.client.ClientRegistry.registerKeyBinding(OPEN)); }
    @Mod.EventBusSubscriber(modid = XPSkillTreeMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static final class Input {
        @SubscribeEvent public static void input(InputEvent.KeyInputEvent e) {
            if (OPEN.consumeClick() && Minecraft.getInstance().screen == null) Minecraft.getInstance().setScreen(new SkillTreeScreen());
        }
    }
}
