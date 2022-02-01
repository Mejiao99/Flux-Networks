package sonar.fluxnetworks.common.connection.transfer;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import sonar.fluxnetworks.FluxNetworks;
import sonar.fluxnetworks.api.energy.IItemEnergyHandler;
import sonar.fluxnetworks.api.network.NetworkMember;

import sonar.fluxnetworks.common.misc.EnergyUtils;
import sonar.fluxnetworks.common.tileentity.TileFluxController;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FluxControllerHandler extends BasicPointHandler<TileFluxController> {

    // a set of players that have at least one network for wireless charging
    private static final Set<ServerPlayerEntity> CHARGING_PLAYERS = new ObjectOpenHashSet<>();

    private static final Predicate<ItemStack> NOT_EMPTY = s -> !s.isEmpty();

    private final Map<ServerPlayerEntity, Iterable<WirelessHandler>> players = new HashMap<>();
    private int timer;

    public FluxControllerHandler(TileFluxController fluxController) {
        super(fluxController);
    }

    @Override
    public void onCycleStart() {
        if (!device.isActive()) {
            demand = 0;
            clearPlayers();
            return;
        }
        if (timer == 0) updatePlayers();
        if ((timer & 0x3) == 2) {
            // keep demand
            demand = chargeAllItems(device.getLogicLimit(), true);
        }
    }

    @Override
    public void onCycleEnd() {
        super.onCycleEnd();
        timer = ++timer & 0x1f;
    }

    @Override
    public void updateTransfers(@Nonnull Direction... faces) {

    }

    @Override
    public void reset() {
        super.reset();
        clearPlayers();
    }

    @Override
    public long sendToConsumers(long energy, boolean simulate) {
        if (!device.isActive()) return 0;
        if ((timer & 0x3) > 0) return 0;
        return chargeAllItems(energy, simulate);
    }

    private long chargeAllItems(long energy, boolean simulate) {
        long leftover = energy;
        for (Map.Entry<ServerPlayerEntity, Iterable<WirelessHandler>> player : players.entrySet()) {
            // dead, or quit game
            if (!player.getKey().isAlive()) {
                continue;
            }
            for (WirelessHandler handler : player.getValue()) {
                leftover = handler.chargeItems(leftover, simulate);
                if (leftover <= 0) {
                    return energy;
                }
            }
        }
        return energy - leftover;
    }

    private void clearPlayers() {
        if (!players.isEmpty()) {
            for (ServerPlayerEntity toRemove : players.keySet()) {
                CHARGING_PLAYERS.remove(toRemove);
            }
            players.clear();
        }
    }

    private void updatePlayers() {
        clearPlayers();
        PlayerList playerList = ServerLifecycleHooks.getCurrentServer().getPlayerList();

        for (NetworkMember p : device.getNetwork().getAllMembers()) {
            ServerPlayerEntity player = playerList.getPlayerByUUID(p.getPlayerUUID());
            if (player == null || CHARGING_PLAYERS.contains(player)) {
                continue;
            }
            final PlayerInventory inv = player.inventory;
            List<WirelessHandler> handlers = new ArrayList<>();

            players.put(player, handlers);
            CHARGING_PLAYERS.add(player);
        }
    }

    private static class WirelessHandler {

        private final Supplier<Iterator<ItemStack>> supplier;
        private final Predicate<ItemStack> validator;

        WirelessHandler(Supplier<Iterator<ItemStack>> supplier, Predicate<ItemStack> validator) {
            this.supplier = supplier;
            this.validator = validator;
        }

        private long chargeItems(long leftover, boolean simulate) {
            for (Iterator<ItemStack> it = supplier.get(); it != null && it.hasNext(); ) {
                ItemStack stack = it.next();
                IItemEnergyHandler handler;
                if (!validator.test(stack) || (handler = EnergyUtils.getEnergyHandler(stack)) == null) {
                    continue;
                }
                if (handler.canAddEnergy(stack)) {
                    leftover -= handler.addEnergy(leftover, stack, simulate);
                    if (leftover <= 0) {
                        return 0;
                    }
                }
            }
            return leftover;
        }
    }
}