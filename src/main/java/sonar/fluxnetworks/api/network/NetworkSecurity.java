package sonar.fluxnetworks.api.network;

import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class NetworkSecurity {


    @Nonnull
    private String password = "";

    public NetworkSecurity() {

    }

    public void set(@Nonnull String password) {
        this.password = password;
    }



    @Nonnull
    public String getPassword() {
        return password;
    }

    public void setPassword(@Nonnull String password) {
        this.password = password;
    }



    public void writeNBT(@Nonnull CompoundNBT nbt, boolean writePassword) {
        CompoundNBT tag = new CompoundNBT();
        if (writePassword)
            tag.putString("password", password);
        nbt.put("security", tag);
    }

    public void readNBT(@Nonnull CompoundNBT nbt) {
        CompoundNBT tag = nbt.getCompound("security");
        password = tag.getString("password");
    }
}
