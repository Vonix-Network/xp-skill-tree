package com.vonix.xpskilltree;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.resources.ResourceLocation;
import java.util.function.Supplier;

public final class Network {
    private static final String PROTOCOL = "1";
    private static int id;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(XPSkillTreeMod.MODID, "main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
    private Network() {}
    public static void register() {
        CHANNEL.registerMessage(id++, SyncPacket.class, SyncPacket::encode, SyncPacket::decode, SyncPacket::handle);
        CHANNEL.registerMessage(id++, UnlockPacket.class, UnlockPacket::encode, UnlockPacket::decode, UnlockPacket::handle);
    }
    public static void sync(ServerPlayer p) { p.getCapability(ModCapabilities.SKILLS).ifPresent(d -> CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), new SyncPacket(d))); }
    public static final class SyncPacket {
        private final net.minecraft.nbt.CompoundTag tag;
        public SyncPacket(SkillTreeData d) { tag = d.serializeNBT(); }
        private SyncPacket(net.minecraft.nbt.CompoundTag t) { tag = t; }
        static void encode(SyncPacket p, FriendlyByteBuf b) { b.writeNbt(p.tag); }
        static SyncPacket decode(FriendlyByteBuf b) { return new SyncPacket(b.readNbt()); }
        static void handle(SyncPacket p, Supplier<NetworkEvent.Context> c) { c.get().enqueueWork(() -> ClientState.load(p.tag)); c.get().setPacketHandled(true); }
    }
    public static final class UnlockPacket {
        private final String id;
        public UnlockPacket(String id) { this.id = id; }
        static void encode(UnlockPacket p, FriendlyByteBuf b) { b.writeUtf(p.id, 64); }
        static UnlockPacket decode(FriendlyByteBuf b) { return new UnlockPacket(b.readUtf(64)); }
        static void handle(UnlockPacket p, Supplier<NetworkEvent.Context> c) {
            NetworkEvent.Context ctx = c.get();
            ctx.enqueueWork(() -> {
                ServerPlayer player = ctx.getSender();
                if (player == null) return;
                SkillNode node = SkillNode.all().get(p.id);
                if (node == null) { player.displayClientMessage(new net.minecraft.network.chat.TextComponent("Unknown talent."), true); return; }
                if (!player.getCapability(ModCapabilities.SKILLS).map(d -> d.canUnlock(p.id)).orElse(false)) {
                    player.displayClientMessage(new net.minecraft.network.chat.TextComponent("Unlock the prerequisite talent first."), true);
                    return;
                }
                if (player.experienceLevel < node.cost()) {
                    player.displayClientMessage(new net.minecraft.network.chat.TextComponent("You need " + node.cost() + " XP levels to unlock this talent."), true);
                    return;
                }
                player.giveExperienceLevels(-node.cost());
                player.getCapability(ModCapabilities.SKILLS).ifPresent(d -> { d.unlock(p.id); SkillEffects.apply(player, d); Network.sync(player); player.displayClientMessage(new net.minecraft.network.chat.TextComponent("Unlocked: " + node.name()), true); });
            });
            ctx.setPacketHandled(true);
        }
    }
}
