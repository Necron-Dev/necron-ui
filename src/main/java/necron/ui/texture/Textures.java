package necron.ui.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import lombok.SneakyThrows;
import lombok.val;
import necron.ui.Lazy;
import necron.ui.NecronUi;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import net.minecraft.client.renderer.texture.MipmapStrategy;
import net.minecraft.resources.Identifier;

public class Textures {
  public static final Identifier ROUNDED_RECT = NecronUi.identifier("generated/rounded_rect");

  static {
    loadInto(ROUNDED_RECT, RoundedRectGenerator.IMAGE);
  }

  @SneakyThrows
  private static void loadInto(Identifier id, byte[] png) {
    val image = NativeImage.read(png);
    val texture = new Texture(id, image);
    Lazy.MC.execute(() -> Lazy.MC.getTextureManager().register(id, texture));
  }

  private static class Texture extends AbstractTexture {
    private final NativeImage image;

    public Texture(Identifier identifier, NativeImage image) {
      this.image = image;
      val gpu = RenderSystem.getDevice();
      texture = gpu.createTexture(
        identifier.toString(),
        GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_TEXTURE_BINDING,
        TextureFormat.RGBA8,
        image.getWidth(),
        image.getHeight(),
        1,
        Integer.SIZE - Integer.numberOfLeadingZeros(Math.max(image.getWidth(), image.getHeight()))
      );
      sampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR, true);
      textureView = gpu.createTextureView(texture);
      val mipmaps = MipmapGenerator.generateMipLevels(
        identifier,
        new NativeImage[]{image},
        texture.getMipLevels() - 1,
        MipmapStrategy.MEAN,
        0
      );
      val encoder = RenderSystem.getDevice().createCommandEncoder();
      for (var l = 0; l < texture.getMipLevels(); l++) {
        encoder.writeToTexture(
          texture,
          mipmaps[l],
          l,
          0,
          0,
          0,
          mipmaps[l].getWidth(),
          mipmaps[l].getHeight(),
          0,
          0
        );
      }
    }

    @Override
    public void close() {
      image.close();
      super.close();
    }
  }
}
