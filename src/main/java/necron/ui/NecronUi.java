package necron.ui;

import com.google.gson.Gson;
import lombok.Cleanup;
import lombok.val;
import necron.ui.callback.FrameCallback;
import necron.ui.demo.DemoScreen;
import necron.ui.style.Palette;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class NecronUi implements ClientModInitializer {
  public static final String ID, VERSION;

  public static Identifier identifier(String path) {
    return Identifier.fromNamespaceAndPath(ID, path);
  }

  static {
    try {
      @Cleanup val resource = NecronUi.class.getResourceAsStream("/fabric.mod.json");
      Objects.requireNonNull(resource);
      val content = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
      val json = new Gson().fromJson(content, Map.class);
      ID = (String) json.get("id");
      VERSION = (String) json.get("version");
    } catch (Exception exception) {
      throw new RuntimeException(exception);
    }
  }

  public static boolean isDebugMode() {
    return GLFW.glfwGetKey(Lazy.MC.getWindow().handle(), GLFW.GLFW_KEY_BACKSPACE) == GLFW.GLFW_PRESS;
  }

  @Override
  public void onInitializeClient() {
    ClientCommandRegistrationCallback.EVENT.register((dispatcher, _) -> {
      val command = dispatcher.register(
        ClientCommandManager
          .literal("necron-ui")
          .then(
            ClientCommandManager
              .literal("demo")
              .then(
                ClientCommandManager
                  .literal("screen")
                  .executes(_ -> DemoScreen.display())
              )
          )
      );
      dispatcher.register(ClientCommandManager.literal("nui").redirect(command));
    });

    FrameCallback.EVENT.register(() -> {
      Timestamp.update();
      Surface.update();
      Input.update();
      Palette.GLOBAL.update();
    });
  }
}
