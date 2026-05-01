package necron.ui.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import lombok.SneakyThrows;
import lombok.val;
import necron.ui.Lazy;
import necron.ui.NecronUi;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;

import java.util.function.Supplier;

public class Textures {
  public static final Identifier ROUNDED_RECT = NecronUi.identifier("generated/rounded_rect");

  static {
    loadInto(ROUNDED_RECT, RoundedRectGenerator.IMAGE);
  }

  @SneakyThrows
  private static void loadInto(Identifier id, byte[] png) {
    val image = NativeImage.read(png);
    val texture = new Texture(id::toString, image);
    Lazy.MC.execute(() -> Lazy.MC.getTextureManager().register(id, texture));
  }

  private static class Texture extends DynamicTexture {
    public Texture(Supplier<String> label, NativeImage pixels) {
      super(label, pixels);
    }

    @Override
    public void upload() {
      sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR, true);
      super.upload();
    }
  }
}
