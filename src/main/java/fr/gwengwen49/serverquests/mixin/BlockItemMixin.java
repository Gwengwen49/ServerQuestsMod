package fr.gwengwen49.serverquests.mixin;

import fr.gwengwen49.serverquests.QuestUser;
import fr.gwengwen49.serverquests.questsystem.ActionType;
import fr.gwengwen49.serverquests.questsystem.serializers.DataHolder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.xml.crypto.Data;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {


    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/criterion/ItemCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V", shift = At.Shift.AFTER), method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", locals = LocalCapture.CAPTURE_FAILSOFT)
    public void onPlace(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir, ItemPlacementContext itemPlacementContext, BlockState blockState, BlockPos blockPos, World world, PlayerEntity playerEntity, ItemStack itemStack, BlockState blockState2) {
        if(playerEntity instanceof QuestUser questUser) {
            questUser.getQuestHandler().tryProgress(ActionType.PLACE_BLOCK,
                    new DataHolder()
                    .add(DataHolder.ITEM, itemStack)
                            .add(DataHolder.BLOCKPOS, blockPos));
        }
    }
}
