package fr.gwengwen49.serverquests.mixin;

import fr.gwengwen49.serverquests.QuestUser;
import fr.gwengwen49.serverquests.questsystem.ActionType;
import fr.gwengwen49.serverquests.questsystem.serializers.DataHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.xml.crypto.Data;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {


    @Shadow @Final protected ServerPlayerEntity player;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onBroken(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V", shift = At.Shift.AFTER), method = "tryBreakBlock", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir, BlockEntity blockEntity, Block block, BlockState blockState2, boolean bl) {
        if(this.player instanceof QuestUser user) {
            user.getQuestHandler().tryProgress(
                    ActionType.BREAK_BLOCK,
                    new DataHolder()
                            .add(DataHolder.BLOCKPOS, pos)
                            .add(DataHolder.BLOCK, block)
            );
        }
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;use(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/TypedActionResult;"), method = "interactItem")
    public void onUseItem(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if(this.player instanceof QuestUser user) {
            DataHolder holder = new DataHolder();
            holder.add(DataHolder.ITEM, stack);
            HitResult hitResult = player.raycast(player.getBlockInteractionRange(), 0.0F, true);
            if(hitResult instanceof BlockHitResult blockHitResult) {
                holder.add(DataHolder.BLOCKPOS, blockHitResult.getBlockPos());
                holder.add(DataHolder.BLOCK, world.getBlockState(blockHitResult.getBlockPos()).getBlock());
            }
            user.getQuestHandler().tryProgress(ActionType.USE_ITEM, holder);
        }
    }
}
