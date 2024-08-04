package fr.gwengwen49.serverquests.questsystem.serializers;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import fr.gwengwen49.serverquests.ServerQuestsMod;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public record JsonPlayer(String name, UUID uuid, String ipAddress) implements JsonElement<ServerPlayerEntity> {


    public static JsonPlayer deserialize(JsonReader reader) throws IOException {
        String name = null;
        UUID uuid = null;
        String ipAddress = null;
        reader.beginObject();
        while (reader.hasNext()){
            switch (reader.nextName()) {
                case "name" -> {
                    if (reader.peek() == JsonToken.STRING) name = reader.nextString();
                }
                case "uuid" -> {
                    if(reader.peek() == JsonToken.STRING) {
                        try {
                            uuid = UUID.fromString(reader.nextString());
                        } catch (IOException e) {
                            ServerQuestsMod.getLogger().error("Can't parse uuid");
                            uuid = null;
                        }
                    }
                }
                case "ipAddress" -> {
                    if(reader.peek() == JsonToken.STRING) ipAddress = reader.nextString();
                }
                default -> reader.skipValue();
            }
        }
        reader.endObject();
        return new JsonPlayer(name, uuid, ipAddress);
    }

    @Override
    public ServerPlayerEntity convert(ServerWorld world) {
        ServerPlayerEntity player = null;
        if(this.name != null) player = world.getServer().getPlayerManager().getPlayer(this.name);
        if(this.uuid != null) player = world.getServer().getPlayerManager().getPlayer(this.uuid);
        if(this.ipAddress != null) {
            Optional<ServerPlayerEntity> optional = world.getServer()
                    .getPlayerManager().getPlayerList().stream()
                    .filter(player1 -> player1.getIp()
                            .equals(this.ipAddress)).findFirst();
            if(optional.isPresent()) {
                player = optional.get();
            }
        }
        return player;
    }

    @Override
    public boolean match(ServerPlayerEntity other) {
        boolean nameMatch = ((this.name == null) || (this.name.equals(other.getName().getString())));
        boolean uuidMatch = ((this.uuid == null) || (this.uuid.equals(other.getUuid())));
        boolean ipMatch = ((this.ipAddress == null) || (this.ipAddress.equals(other.getIp())));
        return nameMatch && uuidMatch && ipMatch;
    }
}
