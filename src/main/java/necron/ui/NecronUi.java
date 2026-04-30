package necron.ui;

import com.google.gson.Gson;
import lombok.Cleanup;
import lombok.val;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class NecronUi {
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
}
