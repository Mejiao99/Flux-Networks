package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.api.gui.EnumNavigationTab;
import sonar.fluxnetworks.api.gui.EnumNetworkColor;
import sonar.fluxnetworks.api.misc.FeedbackInfo;
import sonar.fluxnetworks.api.network.SecurityType;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.button.ColorButton;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.common.misc.FluxMenu;
import sonar.fluxnetworks.register.NetworkHandler;

import javax.annotation.Nonnull;

public class GuiTabCreate extends GuiTabEditAbstract {

    public NormalButton apply, create;

    public GuiTabCreate(@Nonnull FluxMenu container, @Nonnull PlayerEntity player) {
        super(container, player);
        securityType = SecurityType.PUBLIC;
    }

    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_CREATE;
    }

    @Override
    public void init() {
        super.init();
        nameField.setText(player.getDisplayName().getString() + "'s Network");
        int i = 0;
        for (EnumNetworkColor color : EnumNetworkColor.values()) {
            colorButtons.add(new ColorButton(48 + (i >= 7 ? i - 7 : i) * 16, 91 + (i >= 7 ? 1 : 0) * 16, color.getRGB()));
            i++;
        }
        colorBtn = colorButtons.get(0);
        colorBtn.selected = true;

        buttons.add(create = new NormalButton(FluxTranslate.CREATE.t(), 70, 150, 36, 12, 3).setUnclickable());
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);

        screenUtils.renderNetwork(matrixStack, nameField.getText(), colorBtn.color, 20, 129);
        drawCenterText(matrixStack, FluxClientCache.getFeedbackText(), 88, 150, FluxClientCache.getFeedbackColor());
    }

    @Override
    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (button instanceof NormalButton) {
            if (mouseButton == 0 && button.id == 3) {
                //PacketHandler.CHANNEL.sendToServer(new GeneralPacket(GeneralPacketEnum.CREATE_NETWORK, GeneralPacketHandler.getCreateNetworkPacket(name.getText(), color.color, securityType, energyType, password.getText())));
                NetworkHandler.C2S_CreateNetwork(nameField.getText(), colorBtn.color, securityType, passwordField.getText());
            }
        }
    }

    @Override
    public void onEditSettingsChanged() {
        if (create != null) {
            create.clickable = nameField.getText().length() != 0;
        }
    }

    @Override
    public void onFeedbackAction(@Nonnull FeedbackInfo info) {
        super.onFeedbackAction(info);
        if (info == FeedbackInfo.SUCCESS) {
            switchTab(EnumNavigationTab.TAB_SELECTION);
        }
    }
}
