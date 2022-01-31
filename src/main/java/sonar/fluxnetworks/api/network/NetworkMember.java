package sonar.fluxnetworks.api.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.UUID;

public class NetworkMember {

    private UUID playerUUID;
    private String cachedName;


    private NetworkMember() {
    }

    public NetworkMember(CompoundNBT nbt) {
        readNBT(nbt);
    }

    public static NetworkMember create(PlayerEntity player) {
        NetworkMember t = new NetworkMember();
        GameProfile profile = player.getGameProfile();

        t.playerUUID = PlayerEntity.getUUID(profile);
        t.cachedName = profile.getName();

        return t;
    }

    /*public static NetworkMember createMemberByUsername(String username) {
        NetworkMember t = new NetworkMember();
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        boolean isOffline = !server.isServerInOnlineMode();

        if (!isOffline) {
            PlayerProfileCache cache = server.getPlayerProfileCache();
            GameProfile profile = cache.getGameProfileForUsername(username);
            if (profile != null) {
                t.playerUUID = profile.getId();
            } else {
                isOffline = true;
            }
        }
        if (isOffline) {
            t.playerUUID = PlayerEntity.getOfflineUUID(username);
        }
        t.cachedName = username;
        t.accessLevel = AccessLevel.USER;
        return t;
    }*/

    public String getCachedName() {
        return cachedName;
    }


    public UUID getPlayerUUID() {
        return playerUUID;
    }



    public void readNBT(@Nonnull CompoundNBT nbt) {
        playerUUID = nbt.getUniqueId("playerUUID");
        cachedName = nbt.getString("cachedName");

    }

    public void writeNBT(@Nonnull CompoundNBT nbt) {
        nbt.putUniqueId("playerUUID", playerUUID);
        nbt.putString("cachedName", cachedName);
    }
}
