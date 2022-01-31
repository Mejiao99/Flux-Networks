package sonar.fluxnetworks.client.gui.popup;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import sonar.fluxnetworks.api.misc.FluxConstants;
import sonar.fluxnetworks.api.text.FluxTranslate;
import sonar.fluxnetworks.client.FluxClientCache;
import sonar.fluxnetworks.client.gui.button.NormalButton;
import sonar.fluxnetworks.client.gui.tab.GuiTabMembers;
import sonar.fluxnetworks.register.NetworkHandler;

import javax.annotation.Nonnull;

public class PopupMemberEdit extends PopupCore<GuiTabMembers> {

    public NormalButton transferOwnership;
    public int transferOwnershipCount;

    public PopupMemberEdit(GuiTabMembers host, PlayerEntity player) {
        super(host, player);
    }

    @Override
    public void init() {
        super.init();
        popButtons.clear();
        String text;
        int length;
        int i = 0;
        text = FluxTranslate.SET_USER.t();
        length = Math.max(64, font.getStringWidth(text) + 4);
        popButtons.add(new NormalButton(text, 88 - length / 2, 76, length, 12, FluxConstants.TYPE_NEW_MEMBER));
        ++i;
        text = FluxTranslate.TRANSFER_OWNERSHIP.t();
        length = Math.max(64, font.getStringWidth(text) + 4);
        transferOwnership = new NormalButton(text, 88 - length / 2, 76 + 16 * i, length, 12, 4).setUnclickable().setTextColor(0xffaa00aa);
        popButtons.add(transferOwnership);

    }

    @Override
    public void drawGuiContainerForegroundLayer(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY) {
        //screenUtils.drawRectWithBackground(20, 34, 100, 138, 0xccffffff, 0x80000000);
        super.drawGuiContainerForegroundLayer(matrixStack, mouseX, mouseY);
        drawCenterText(matrixStack, FluxClientCache.getFeedbackText(), 88, 162, FluxClientCache.getFeedbackColor());
        drawCenterText(matrixStack, TextFormatting.AQUA + host.selectedPlayer.getCachedName(), 88, 38, 0xffffff);
        drawCenterText(matrixStack, "superadmindXL2", 88, 48, 0xffffff);
        String text = host.selectedPlayer.getPlayerUUID().toString();
        matrixStack.push();
        matrixStack.scale(0.625f, 0.625f, 1);
        drawCenterText(matrixStack, "UUID: " + text.substring(0, 16), 88 * 1.6f, 60 * 1.6f, 0xffffff);
        drawCenterText(matrixStack, text.substring(16), 88 * 1.6f, 66 * 1.6f, 0xffffff);
        matrixStack.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        for (NormalButton button : popButtons) {
            if (button.clickable && button.isMouseHovered(minecraft, (int) mouseX - guiLeft, (int) mouseY - guiTop)) {
                NetworkHandler.C2S_EditMember(host.network.getNetworkID(), host.selectedPlayer.getPlayerUUID(), button.id);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (transferOwnership != null) {
            if (scanCode == 42) {
                transferOwnershipCount++;
                if (transferOwnershipCount > 1) {
                    transferOwnership.clickable = true;
                }
            } else {
                transferOwnershipCount = 0;
                transferOwnership.clickable = false;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
