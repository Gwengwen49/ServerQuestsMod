package fr.gwengwen49.serverquests;

import fr.gwengwen49.serverquests.commands.QuestCommand;
import fr.gwengwen49.serverquests.questsystem.Quest;
import fr.gwengwen49.serverquests.questsystem.QuestHandler;
import fr.gwengwen49.serverquests.questsystem.QuestLoader;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Environment(EnvType.SERVER)
public class ServerQuestsMod implements DedicatedServerModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("ServerQuestMod");
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
                    try {
                        QuestLoader.loadQuestsFromFiles();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> QuestCommand.register(dispatcher));
    }

    public static void logQuestProgress(QuestUser player) {
        QuestHandler handler = player.getQuestHandler();
        Quest quest = handler.getCurrentQuest();
        LOGGER.info("{} has progressed the quest [{}] to {}/{} ({}%)", ((ServerPlayerEntity)player).getDisplayName(), quest.displayName(), handler.getQuestProgression(), quest.action().getCount(), ((float)(handler.getQuestProgression()/quest.action().getCount()))*100.0F);
    }
    public static Logger getLogger() {
        return LOGGER;
    }
}
