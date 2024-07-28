package fr.gwengwen49.serverquests.gui;

import fr.gwengwen49.serverquests.QuestUser;
import fr.gwengwen49.serverquests.questsystem.Quest;
import fr.gwengwen49.serverquests.questsystem.QuestLoader;
import net.minecraft.block.MushroomPlantBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.spawner.PatrolSpawner;

public class QuestGui extends ScreenHandler {

    private final PlayerInventory playerInventory;
    public QuestGui(int syncId, PlayerInventory playerInventory) {
        super(ScreenHandlerType.GENERIC_9X6, syncId);
        this.playerInventory = playerInventory;
        Inventory inventory = new SimpleInventory(9*6);
        int i = (6 - 4) * 18;
        for(int j = 0; j < 6; ++j) {
            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(inventory, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }
        for(int j = 0; j < 3; ++j) {
            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }
        for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(playerInventory, j, 8 + j * 18, 161 + i));
        }
        if(playerInventory.player.getWorld() instanceof ServerWorld serverWorld)
            this.initialize(serverWorld);
    }

    public QuestGui initialize(ServerWorld world) {
        if(this.playerInventory.player instanceof QuestUser questUser) {
            for (int i = 0; i < questUser.getQuestHandler().getAvailableQuests().size(); i++) {
                Quest quest = questUser.getQuestHandler().getAvailableQuests().get(i);
                NbtCompound id = new NbtCompound();
                id.putString("quest_id", quest.id());
                ItemStack questIcon = quest.menuItem().convert(world);
                if(questIcon == ItemStack.EMPTY) questIcon = Items.STONE.getDefaultStack();
                questIcon.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(id));
                if(i >= 9*6) {
                    break;
                }
                Slot slot = this.getSlot(i);
                slot.setStack(questIcon);
                this.sendContentUpdates();
            }
        }
        return this;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if(player instanceof QuestUser user) {
            ItemStack itemStack = this.getSlot(slotIndex).getStack();
            NbtComponent nbtComp = itemStack.get(DataComponentTypes.CUSTOM_DATA);
            if(nbtComp == null) return;
            if(!nbtComp.copyNbt().contains("quest_id")) return;
            Quest quest = QuestLoader.getQuestFromName(nbtComp.copyNbt().getString("quest_id"));
            if(quest == null) return;
            if(!user.getQuestHandler().acceptQuest(quest)) {
                this.getSlot(slotIndex).setStack(Items.RED_CONCRETE.getDefaultStack());
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
