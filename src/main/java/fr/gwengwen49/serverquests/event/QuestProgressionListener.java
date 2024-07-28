package fr.gwengwen49.serverquests.event;

import fr.gwengwen49.serverquests.questsystem.Quest;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public interface QuestProgressionListener {

    default void onQuestProgression(ServerWorld world, ServerPlayerEntity player, Quest quest, int progession) {

    }

    default void onQuestCompletion(ServerWorld world, ServerPlayerEntity player, Quest quest) {

    }
}
