package fr.gwengwen49.serverquests.questsystem.serializers;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class JsonPlayer implements JsonElement<ServerPlayerEntity> {


    @Override
    public ServerPlayerEntity convert(ServerWorld world) {
        return null;
    }

    @Override
    public boolean match(ServerPlayerEntity other) {
        return false;
    }
}
