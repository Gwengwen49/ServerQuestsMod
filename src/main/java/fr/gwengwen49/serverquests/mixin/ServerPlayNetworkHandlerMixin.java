package fr.gwengwen49.serverquests.mixin;

import fr.gwengwen49.serverquests.QuestUser;
import fr.gwengwen49.serverquests.questsystem.ActionType;
import fr.gwengwen49.serverquests.questsystem.serializers.DataHolder;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {


    @Shadow public ServerPlayerEntity player;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;jump()V", shift = At.Shift.AFTER), method = "onPlayerMove")
    public void onJump(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if(this.player instanceof QuestUser user) {
            user.getQuestHandler().tryProgress(ActionType.JUMP, new DataHolder());
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V", shift = At.Shift.AFTER), method = "onPlayerMove")
    public void onMove(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if(this.player instanceof QuestUser user) {
            user.getQuestHandler().tryProgress(ActionType.MOVE, new DataHolder());
        }
    }


}
