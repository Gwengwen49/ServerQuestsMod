package fr.gwengwen49.serverquests.questsystem;

import fr.gwengwen49.serverquests.QuestUser;
import fr.gwengwen49.serverquests.ServerQuestsMod;
import fr.gwengwen49.serverquests.questsystem.serializers.DataHolder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Marker;

import java.util.List;

public class QuestHandler {

    private final ServerPlayerEntity player;
    private Quest quest;
    private int questProgression;
    private int playerLevel;
    private boolean rewardOnRespawn;

    public QuestHandler(ServerPlayerEntity player) {
        this.player = player;
        this.quest = null;
        this.questProgression = 0;
        this.playerLevel = 0;
        this.rewardOnRespawn = false;

        
    }

    public void completeQuest() {
        ServerQuestsMod.getLogger().info("{} has completed the quest \"{}\" !", this.player.getName(), this.quest.displayName());
        this.quest.rewards().forEach(this::applyReward);
        ++playerLevel;
        this.resetQuest();
    }

    public void update() {
        if(this.quest == null) return;
        if(this.questProgression >= this.quest.action().getCount()) {
            this.completeQuest();
        }
    }
    public void applyReward(Reward reward) {
        reward.apply(this.player.getServerWorld(), this.player);
    }

    public boolean tryProgress(ActionType type, DataHolder context) {
        if(this.quest == null) return false;
        if(this.quest.action().getType() == type) {
            if(this.quest.action().getResult(player.getServerWorld(), player, context)) {
                ++this.questProgression;
                ServerQuestsMod.logQuestProgress(this.player);
                if (this.questProgression >= this.quest.action().getCount()) {
                    if(type == ActionType.DIE) {

                    }
                    else this.completeQuest();
                }
                return true;
            }
        }
        return false;
    }

    public List<Quest> getAvailableQuests() {
        return QuestLoader.getLoadedQuests();
    }

    public boolean acceptQuest(Quest quest) {
        if (this.quest == null) {
            if (this.playerLevel >= quest.accessLevel()) {
                this.player.sendMessage(Text.literal("Vous avez selectionné la quete [" + quest.displayName() + "]"));
                this.quest = quest;
                this.questProgression = 0;
                return true;
            }
            else {
                this.player.sendMessage(Text.literal("Votre niveau ne permet pas d'effectuer cette quete [accessible au niveau "+quest.accessLevel()+"]"));
                return false;
            }
        }
        else {
            this.player.sendMessage(Text.literal("Vous avez déja une quete en cours [" + this.quest.displayName() + "]"));
            return false;
        }
    }

    public int getLevel() {
        return this.playerLevel;
    }

    public void restartQuest() {
        this.questProgression = 0;
    }

    public Quest getCurrentQuest() {
        return this.quest;
    }

    public int getQuestProgression() {
        return this.questProgression;
    }

    public void resetQuest() {
        this.quest = null;
        this.questProgression = 0;
    }

    public void reset() {
        this.resetQuest();
        this.playerLevel = 0;
    }

    public void resetLevel() {
        this.playerLevel = 0;
    }

    public void setLevel(int level) {
        this.playerLevel = level;
    }

    public void addLevel(int count) {
        this.playerLevel += count;
    }

    public void setProgression(int progression) {
        this.questProgression = progression;
        this.update();
    }

    public void increaseProgression(int progression) {
        this.questProgression += progression;
        this.update();
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putString("quest_id", this.quest.id());
        nbt.putInt("quest_progression", this.questProgression);
    }

    public void readNbt(NbtCompound nbt) {
        this.quest = QuestLoader.getQuestFromName(nbt.getString("quest_id"));
        this.questProgression = nbt.getInt("quest_progression");
        this.playerLevel = nbt.getInt("player_level");
    }

    public void copyFrom(QuestHandler oldHandler) {
        this.quest = oldHandler.quest;
        this.questProgression = oldHandler.questProgression;
        this.playerLevel = oldHandler.playerLevel;
    }


}
