package sonar.fluxnetworks.client.gui.tab;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import sonar.fluxnetworks.api.gui.EnumNavigationTab;
import sonar.fluxnetworks.api.misc.FeedbackInfo;

import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.basic.GuiButtonCore;
import sonar.fluxnetworks.client.gui.basic.GuiTabCore;

import sonar.fluxnetworks.client.gui.button.InvisibleButton;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.button.SlidedSwitchButton;
import sonar.fluxnetworks.common.misc.FluxMenu;
import sonar.fluxnetworks.register.NetworkHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class GuiTabWireless extends GuiTabCore {

    public InvisibleButton redirectButton;


    public NormalButton apply;

    // TODO: DELETE THIS
    public boolean wirelessMode;

    public GuiTabWireless(@Nonnull FluxMenu container, @Nonnull PlayerEntity player) {
        super(container, player);
    }

    public EnumNavigationTab getNavigationTab() {
        return EnumNavigationTab.TAB_WIRELESS;
    }

    @Override
    protected void drawForegroundLayer(MatrixStack matrixStack, int mouseX, int mouseY) {
        super.drawForegroundLayer(matrixStack, mouseX, mouseY);
        if (networkValid) {
            int color = network.getNetworkColor();
            drawCenterText(matrixStack, FluxTranslate.TAB_WIRELESS.t(), 88, 12, 0xb4b4b4);
            font.drawString(matrixStack, FluxTranslate.ENABLE_WIRELESS.t(), 20, 156, color);
            drawCenterText(matrixStack, FluxClientCache.getFeedbackText(), 88, 146, FluxClientCache.getFeedbackColor());
        } else {
            renderNavigationPrompt(matrixStack, FluxTranslate.ERROR_NO_SELECTED.t(), FluxTranslate.TAB_SELECTION.t());
        }
    }

    @Override
    public void init() {
        super.init();
        configureNavigationButtons(EnumNavigationTab.TAB_WIRELESS, navigationTabs);


        if (networkValid) {

            wirelessMode = network.getWirelessMode();

            switches.add(new SlidedSwitchButton(140, 156, 4, guiLeft, guiTop, wirelessMode));

            apply = new NormalButton(FluxTranslate.APPLY.t(), 73, 130, 32, 12, 0).setUnclickable();
            buttons.add(apply);
        } else {
            redirectButton = new InvisibleButton(guiLeft + 20, guiTop + 16, 135, 20,
                    EnumNavigationTab.TAB_SELECTION.getTranslatedName(), b -> switchTab(EnumNavigationTab.TAB_SELECTION));
            addButton(redirectButton);
        }
    }


    public void onButtonClicked(GuiButtonCore button, int mouseX, int mouseY, int mouseButton) {
        super.onButtonClicked(button, mouseX, mouseY, mouseButton);
        if (mouseButton != 0) {
            return;
        }
        if (button instanceof NormalButton && button.id == 0) {
            NetworkHandler.C2S_EditWireless(network.getNetworkID(), wirelessMode);
        }
        if (button instanceof SlidedSwitchButton) {
            ((SlidedSwitchButton) button).switchButton();
            if (button.id == 4) {
                switchSetting();
            }
        }
    }

    public void switchSetting() {
        wirelessMode = !wirelessMode;
        apply.clickable = true;
    }

    @Override
    public void onFeedbackAction(@Nonnull FeedbackInfo info) {
        super.onFeedbackAction(info);
        if (apply != null && info == FeedbackInfo.SUCCESS) {
            apply.clickable = false;
        }
    }
}
