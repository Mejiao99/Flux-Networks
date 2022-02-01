package sonar.fluxnetworks.api.misc;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.energy.CapabilityEnergy;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

public class FluxCapabilities {

    /**
     * Only make use of this capability if your mod can send/receive energy at a rate greater than Integer.MAX_VALUE
     * Flux Networks will handle all Forge Energy implementations as normal.
     * <p>
     * Functions the same as {@link CapabilityEnergy} but allows Long.MAX_VALUE
     * you can add this cap to Items or Tile Entities, the Flux Plug & Point also use this capability
     */
    @CapabilityInject(IFNEnergyStorage.class)
    public static Capability<IFNEnergyStorage> FN_ENERGY_STORAGE = null;
}
