package necron.ui.mixin;

import lombok.val;
import necron.wcnmwynn.Encryption;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ManageServerScreen;
import net.minecraft.client.multiplayer.ServerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ManageServerScreen.class)
public abstract class MixinManageServerScreen {
  @Shadow
  @Final
  private ServerData serverData;

  @Shadow
  private EditBox nameEdit;

  @Shadow
  private EditBox ipEdit;

  @Inject(
    method = "init",
    at = @At("RETURN")
  )
  private void initReturn(CallbackInfo ci) {
    nameEdit.setMaxLength(Integer.MAX_VALUE);
    ipEdit.setMaxLength(Integer.MAX_VALUE);
  }

  @Inject(
    method = "onAdd",
    at = @At(
      value = "INVOKE",
      target = "Lit/unimi/dsi/fastutil/booleans/BooleanConsumer;accept(Z)V"
    )
  )
  private void onAdd_acceptBefore(CallbackInfo ci) {
    if (serverData.name.startsWith("#")) {
      val result = Encryption.encrypt(
        serverData.name.substring(1).toLowerCase(),
        serverData.ip.toLowerCase()
      );
      if (result != null) serverData.name = "@" + result;
    }
  }
}
