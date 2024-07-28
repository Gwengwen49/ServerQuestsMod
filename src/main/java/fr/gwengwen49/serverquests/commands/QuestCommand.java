package fr.gwengwen49.serverquests.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import fr.gwengwen49.serverquests.QuestUser;
import fr.gwengwen49.serverquests.gui.QuestGui;
import fr.gwengwen49.serverquests.questsystem.QuestLoader;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.KillCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class QuestCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("quests")
                .executes(context -> executeOpenQuestGui(context.getSource()))
                .then(CommandManager.literal("reload")
                        .requires(source -> source.hasPermissionLevel(4))
                        .executes(context -> {
                           QuestLoader.reload(context.getSource());
                           return 0;
                        })));
        dispatcher.register(CommandManager.literal("quest")
                .executes(context -> executeGetActualQuest(context.getSource()))
                .then(CommandManager.argument("targets", GameProfileArgumentType.gameProfile())
                        .then(CommandManager.literal("level")
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                                .executes(context -> executeSetLevel(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), IntegerArgumentType.getInteger(context, "level")))))
                                .then(CommandManager.literal("add")
                                        .then(CommandManager.argument("level", IntegerArgumentType.integer(0))
                                                .executes(context -> executeAddLevel(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), IntegerArgumentType.getInteger(context, "level"))))))
                        .then(CommandManager.literal("quest_progression")
                                .then(CommandManager.literal("set")
                                        .then(CommandManager.argument("progression", IntegerArgumentType.integer(0))
                                                .executes(context -> executeSetProgression(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), IntegerArgumentType.getInteger(context, "progression")))))
                                .then(CommandManager.literal("add")
                                        .then(CommandManager.argument("progression", IntegerArgumentType.integer(0))
                                                .executes(context -> executeAddProgression(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"), IntegerArgumentType.getInteger(context, "progression"))))))
                        .then(CommandManager.literal("reset")
                                .executes(context -> executeResetQuest(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"))))
                        .then(CommandManager.literal("restart")
                                .executes(context -> executeRestartQuest(context.getSource(), GameProfileArgumentType.getProfileArgument(context, "targets"))))

                )

        );

    }

    private static int executeOpenQuestGui(ServerCommandSource source) {
        if (source.getPlayer() == null) return -1;
        source.getPlayer().openHandledScreen(new NamedScreenHandlerFactory() {
            @Override
            public Text getDisplayName() {
                return Text.of("QuestMenu");
            }

            @Nullable
            @Override
            public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
                return new QuestGui(syncId, playerInventory);
            }
        });
        return 0;
    }

    private static int executeGetActualQuest(ServerCommandSource source) {
        if (source.getPlayer() == null) return -1;
        if (((QuestUser) source.getPlayer()).getQuestHandler().getCurrentQuest() == null) {
            source.getPlayer().sendMessage(Text.literal("Vous n'avez pas de quete [sélectionnez en une en tapant la commande /quests"));
            return -1;
        }
        source.getPlayer().sendMessage(Text.literal("Votre quete actuelle est [" + ((QuestUser) source.getPlayer()).getQuestHandler().getCurrentQuest().displayName() + "]"));
        return 0;
    }

    private static int executeResetQuest(ServerCommandSource source, Collection<GameProfile> gameProfiles) {
        for (GameProfile profile : gameProfiles) {
            ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(profile.getId());
            if (player == null) continue;
            ((QuestUser) player).getQuestHandler().resetQuest();
        }
        return 0;
    }

    private static int executeRestartQuest(ServerCommandSource source, Collection<GameProfile> gameProfiles) {
        for (GameProfile profile : gameProfiles) {
            ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(profile.getId());
            if (player == null) continue;
            ((QuestUser) player).getQuestHandler().restartQuest();
        }
        return 0;
    }

    private static int executeSetLevel(ServerCommandSource source, Collection<GameProfile> gameProfiles, int level) {
        for (GameProfile profile : gameProfiles) {
            ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(profile.getId());
            if (player == null) continue;
            ((QuestUser) player).getQuestHandler().setLevel(level);
        }
        return 0;
    }

    private static int executeAddLevel(ServerCommandSource source, Collection<GameProfile> gameProfiles, int level) {
        for (GameProfile profile : gameProfiles) {
            ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(profile.getId());
            if (player == null) continue;
            ((QuestUser) player).getQuestHandler().addLevel(level);
        }
        return 0;
    }

    private static int executeSetProgression(ServerCommandSource source, Collection<GameProfile> gameProfiles, int progression) {
        for (GameProfile profile : gameProfiles) {
            ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(profile.getId());
            if (player == null) continue;
            ((QuestUser) player).getQuestHandler().setProgression(progression);
        }
        return 0;
    }

    private static int executeAddProgression(ServerCommandSource source, Collection<GameProfile> gameProfiles, int progression) {
        for (GameProfile profile : gameProfiles) {
            ServerPlayerEntity player = source.getServer().getPlayerManager().getPlayer(profile.getId());
            if (player == null) continue;
            ((QuestUser) player).getQuestHandler().increaseProgression(progression);
        }
        return 0;
    }
}
