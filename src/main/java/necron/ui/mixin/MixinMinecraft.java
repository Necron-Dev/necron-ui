package necron.ui.mixin;

import necron.ui.callback.FrameCallback;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {
  @Inject(
    method = "runTick",
    at = @At("HEAD")
  )
  private void runTick_head(CallbackInfo ci) {
    FrameCallback.EVENT.invoker().onFrame();
  }
}
