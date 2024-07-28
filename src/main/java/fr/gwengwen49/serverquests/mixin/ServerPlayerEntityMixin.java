package fr.gwengwen49.serverquests.mixin;

import com.mojang.authlib.GameProfile;
import fr.gwengwen49.serverquests.QuestUser;
import fr.gwengwen49.serverquests.ServerQuestsMod;
import fr.gwengwen49.serverquests.gui.QuestGui;
import fr.gwengwen49.serverquests.questsystem.ActionType;
import fr.gwengwen49.serverquests.questsystem.QuestHandler;
import fr.gwengwen49.serverquests.questsystem.serializers.DataHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements QuestUser {

    @Unique
    private final QuestHandler questHandler = new QuestHandler((ServerPlayerEntity)(Object)this);

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(at = @At(value = "TAIL"), method = "writeCustomDataToNbt")
    public void writeQuestData(NbtCompound nbt, CallbackInfo ci) {
        this.questHandler.writeNbt(nbt);
    }

    @Inject(at = @At(value = "TAIL"), method = "readCustomDataFromNbt")
    public void readQuestData(NbtCompound nbt, CallbackInfo ci) {
        this.questHandler.readNbt(nbt);
    }

    @Inject(at = @At(value = "TAIL"), method = "copyFrom")
    public void copyQuestData(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci){
        this.questHandler.copyFrom(((QuestUser)oldPlayer).getQuestHandler());
    }

    @Inject(at = @At(value = "TAIL"), method = "tick")
    public void hookQuestActions(CallbackInfo ci) {
        if(!this.isImmobile()) {
            this.questHandler.tryProgress(ActionType.MOVE, new DataHolder());
        }
    }

    @Override
    public QuestHandler getQuestHandler() {
        return this.questHandler;
    }



}
