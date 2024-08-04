package fr.gwengwen49.serverquests.mixin;

import fr.gwengwen49.serverquests.QuestUser;
import fr.gwengwen49.serverquests.questsystem.ActionType;
import fr.gwengwen49.serverquests.questsystem.serializers.DataHolder;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {



}
