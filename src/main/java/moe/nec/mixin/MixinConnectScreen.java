package moe.nec.mixin;

import lombok.val;
import moe.nec.wcnmwynn.Encryption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public abstract class MixinConnectScreen {
  @Unique
  private static ServerData storedServerData;

  @Inject(
    method = "startConnecting",
    at = @At("HEAD")
  )
  private static void startConnectingHead(
    Screen parent,
    Minecraft minecraft,
    ServerAddress serverAddress,
    ServerData serverData,
    boolean isQuickPlay,
    TransferState transferState,
    CallbackInfo ci
  ) {
    storedServerData = serverData;
  }

  @ModifyVariable(
    method = "startConnecting",
    at = @At("HEAD"),
    argsOnly = true,
    ordinal = 0,
    order = 1001
  )
  private static ServerAddress startConnecting_modifyServerAddress(
    ServerAddress serverAddress
  ) {
    System.out.printf("Trying to decode connection to %s\n", serverAddress);
    if (storedServerData == null || !storedServerData.name.startsWith("@")) {
      return serverAddress;
    }
    val decrypted = Encryption.decrypt(
      storedServerData.name.substring(1),
      storedServerData.ip
    );
    return ServerAddress.parseString(decrypted);
  }
}
