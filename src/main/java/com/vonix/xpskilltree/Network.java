package com.vonix.xpskilltree;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.TextComponent;
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
        CHANNEL.registerMessage(id++, CosmeticSyncPacket.class, CosmeticSyncPacket::encode, CosmeticSyncPacket::decode, CosmeticSyncPacket::handle);
        CHANNEL.registerMessage(id++, CosmeticTogglePacket.class, CosmeticTogglePacket::encode, CosmeticTogglePacket::decode, CosmeticTogglePacket::handle);
        CHANNEL.registerMessage(id++, CosmeticHeldPacket.class, CosmeticHeldPacket::encode, CosmeticHeldPacket::decode, CosmeticHeldPacket::handle);
        CHANNEL.registerMessage(id++, CosmeticClearPacket.class, CosmeticClearPacket::encode, CosmeticClearPacket::decode, CosmeticClearPacket::handle);
    }
    public static void sync(ServerPlayer p) { p.getCapability(ModCapabilities.SKILLS).ifPresent(d -> CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), new SyncPacket(d))); syncCosmetics(p); }
    public static void syncCosmetics(ServerPlayer p) { p.getCapability(CosmeticCapability.COSMETICS).ifPresent(d -> CHANNEL.send(PacketDistributor.PLAYER.with(() -> p), new CosmeticSyncPacket(d))); }
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
                if (node == null) { player.displayClientMessage(new TextComponent("Unknown talent."), true); return; }
                if (!player.getCapability(ModCapabilities.SKILLS).map(d -> d.canUnlock(p.id)).orElse(false)) { player.displayClientMessage(new TextComponent("Unlock the prerequisite talent first."), true); return; }
                if (player.experienceLevel < node.cost()) { player.displayClientMessage(new TextComponent("You need " + node.cost() + " XP levels to unlock this talent."), true); return; }
                player.giveExperienceLevels(-node.cost());
                player.getCapability(ModCapabilities.SKILLS).ifPresent(d -> { d.unlock(p.id); SkillEffects.apply(player, d); Network.sync(player); player.displayClientMessage(new TextComponent("Unlocked: " + node.name()), true); });
            });
            ctx.setPacketHandled(true);
        }
    }
    public static final class CosmeticSyncPacket { private final net.minecraft.nbt.CompoundTag tag; public CosmeticSyncPacket(CosmeticData d){tag=d.serializeNBT();} private CosmeticSyncPacket(net.minecraft.nbt.CompoundTag t){tag=t;} static void encode(CosmeticSyncPacket p,FriendlyByteBuf b){b.writeNbt(p.tag);} static CosmeticSyncPacket decode(FriendlyByteBuf b){return new CosmeticSyncPacket(b.readNbt());} static void handle(CosmeticSyncPacket p,Supplier<NetworkEvent.Context> c){c.get().enqueueWork(()->ClientState.loadCosmetics(p.tag));c.get().setPacketHandled(true);} }
    public static final class CosmeticTogglePacket { private final boolean enabled; public CosmeticTogglePacket(boolean e){enabled=e;} static void encode(CosmeticTogglePacket p,FriendlyByteBuf b){b.writeBoolean(p.enabled);} static CosmeticTogglePacket decode(FriendlyByteBuf b){return new CosmeticTogglePacket(b.readBoolean());} static void handle(CosmeticTogglePacket p,Supplier<NetworkEvent.Context> c){NetworkEvent.Context x=c.get();x.enqueueWork(()->{ServerPlayer q=x.getSender();if(q!=null)q.getCapability(CosmeticCapability.COSMETICS).ifPresent(d->{d.setEnabled(p.enabled);syncCosmetics(q);});});x.setPacketHandled(true);} }
    public static final class CosmeticHeldPacket { private final int slot; public CosmeticHeldPacket(int s){slot=s;} static void encode(CosmeticHeldPacket p,FriendlyByteBuf b){b.writeVarInt(p.slot);} static CosmeticHeldPacket decode(FriendlyByteBuf b){return new CosmeticHeldPacket(b.readVarInt());} static void handle(CosmeticHeldPacket p,Supplier<NetworkEvent.Context> c){NetworkEvent.Context x=c.get();x.enqueueWork(()->{ServerPlayer q=x.getSender();if(q!=null&&p.slot>=0&&p.slot<4&&q.getCapability(CosmeticCapability.COSMETICS).map(d->d.setFromHeld(q,p.slot)).orElse(false))syncCosmetics(q);});x.setPacketHandled(true);} }
    public static final class CosmeticClearPacket { private final int slot; public CosmeticClearPacket(int s){slot=s;} static void encode(CosmeticClearPacket p,FriendlyByteBuf b){b.writeVarInt(p.slot);} static CosmeticClearPacket decode(FriendlyByteBuf b){return new CosmeticClearPacket(b.readVarInt());} static void handle(CosmeticClearPacket p,Supplier<NetworkEvent.Context> c){NetworkEvent.Context x=c.get();x.enqueueWork(()->{ServerPlayer q=x.getSender();if(q!=null&&p.slot>=0&&p.slot<4)q.getCapability(CosmeticCapability.COSMETICS).ifPresent(d->{d.clear(p.slot);syncCosmetics(q);});});x.setPacketHandled(true);} }
}
