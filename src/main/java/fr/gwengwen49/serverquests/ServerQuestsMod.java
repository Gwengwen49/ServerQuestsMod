package fr.gwengwen49.serverquests;

import fr.gwengwen49.serverquests.commands.QuestCommand;
import fr.gwengwen49.serverquests.questsystem.ActionType;
import fr.gwengwen49.serverquests.questsystem.Quest;
import fr.gwengwen49.serverquests.questsystem.QuestHandler;
import fr.gwengwen49.serverquests.questsystem.QuestLoader;
import fr.gwengwen49.serverquests.questsystem.serializers.DataHolder;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.BlockPickInteractionAware;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerBlockEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.Entity;
import net.minecraft.item.BookItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiling.jfr.event.PacketReceivedEvent;
import org.fusesource.jansi.Ansi;
import org.lwjgl.glfw.Callbacks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.callback.Callback;
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
                ServerQuestsMod.getLogger().info(Ansi.ansi().fgRgb(255, 0, 0).a("Error while loading quests : {}").reset().toString(), e.getMessage());
            }
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ((QuestUser)handler.player).getQuestHandler().tryProgress(ActionType.CONNECT_SERVER, new DataHolder());
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> ((QuestUser)handler.player).getQuestHandler().tryProgress(ActionType.DISCONNECT_SERVER, new DataHolder()));
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (newPlayer instanceof QuestUser user) {
                user.getQuestHandler().rewardOnRespawn();
            }
        });
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> QuestCommand.register(dispatcher));
    }

    public static void logQuestProgress(QuestUser player) {
        QuestHandler handler = player.getQuestHandler();
        Quest quest = handler.getCurrentQuest();
        LOGGER.info("{} has progressed the quest [{}] to {}/{} ({}%)", ((ServerPlayerEntity) player).getDisplayName().getString(), quest.displayName(), handler.getQuestProgression(), quest.action().getCount(), ((float) handler.getQuestProgression() / ((float) quest.action().getCount())) * 100.0F);
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}
