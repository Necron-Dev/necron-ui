package necron.ui.widget.container;

import lombok.Builder;
import lombok.val;
import necron.ui.animation.Ease;
import necron.ui.context.Context;
import necron.ui.event.Event;
import necron.ui.event.KeyEvent;
import necron.ui.event.MousePosEvent;
import necron.ui.event.UpdateEvent;
import necron.ui.layout.Axis;
import necron.ui.layout.Box;
import necron.ui.layout.Pos;
import necron.ui.react.BoxReact;
import necron.ui.react.CalcReact;
import necron.ui.react.React;
import necron.ui.style.Palette;
import necron.ui.util.ColorUtil;
import necron.ui.widget.ChildrenConfiguration;
import necron.ui.widget.Container;
import necron.ui.widget.element.RoundedRect;

import static necron.ui.layout.Axis.Y;
import static necron.ui.layout.Box.box;
import static necron.ui.layout.Dim.fp;
import static necron.ui.layout.Dim.min;
import static necron.ui.layout.Pos.auto;
import static necron.ui.react.React.*;
import static necron.ui.util.fn.Fn2.fn;
import static necron.ui.util.fn.Fn3.fn;
import static necron.ui.util.fn.Fn4.fn;
import static yqloss.E.$;

public class SimpleButton extends Div {
  private final React<Boolean> disabled;
  private final BoxReact<Boolean> isHoveredBox, isPressedBox;
  private final CalcReact<Boolean> isHovered, isPressed;
  private final React<Runnable> callback;

  @Builder(builderMethodName = "simpleButton")
  public SimpleButton(
    Container parent,
    Object key,
    Box.SizePadding sizePadding,
    Pos positioning,
    React<Float> elevation,
    Axis axis,
    React<Float> alignment,
    React<Boolean> disabled,
    React<Float> deltaElevation,
    React<Float> cornerRadius,
    React<Integer> color,
    React<Runnable> callback,
    ChildrenConfiguration children
  ) {
    val thisIsHoveredBox = isHoveredBox = useBox(false);
    val thisIsPressedBox = isPressedBox = useBox(false);
    val thisIsHovered = isHovered = useCalc(
      fn((Boolean d, Boolean i) -> !d && i),
      disabled, thisIsHoveredBox
    );
    val thisIsPressed = isPressed = useCalc(
      fn((Boolean d, Boolean i) -> !d && i),
      disabled, thisIsPressedBox
    );
    super(
      parent, key, sizePadding, positioning,
      useAnimated(
        useCalc(
          fn((Float e, Boolean h, Boolean p, Float d) -> {
            return e + (p ? -d : h ? d : 0);
          }),
          elevation, thisIsHovered, thisIsPressed, deltaElevation
        ),
        (a, x) -> a.interrupt().next(Ease.LINEAR, x, 0, 200)
      ),
      axis, alignment,
      ctx -> {
        val builder = children.getChildrenBuilder(ctx);
        return dsl -> {
          RoundedRect.background(
            dsl, cornerRadius, useGradient(
              useCalc(
                fn((Integer c, Boolean h, Boolean p) -> {
                  return ColorUtil.scaleRGB(
                    c, p ? 0.9F : h ? 1.1F : 1F
                  );
                }),
                color, thisIsHovered, thisIsPressed
              ),
              (a, x) -> a.interrupt().next(Ease.LINEAR, x, 0, 200)
            )
          );
          builder.buildChildren(dsl);
        };
      }
    );
    this.disabled = disabled;
    this.callback = callback;
  }

  @Override
  public boolean dispatch(Context context, Event event, boolean handled) {
    switch (event) {
      case UpdateEvent _ -> {
        disabled.get();
        isHovered.get();
        isPressed.get();
      }

      case MousePosEvent mousePosEvent -> {
        isHoveredBox.set(isInside(mousePosEvent.getPosition()));
      }

      case KeyEvent keyEvent when !disabled.peek() && keyEvent.getType() == KeyEvent.Type.MOUSE -> {
        if (keyEvent.isPress()) {
          if (isHovered.peek()) {
            isPressedBox.set(true);
          }
        } else {
          if (isHovered.peek()) {
            callback.get().run();
          }
          isPressedBox.set(false);
        }
      }

      default -> {}
    }

    return super.dispatch(context, event, handled);
  }

  public static SimpleButtonBuilder simpleButton(Container parent, Object key) {
    return new SimpleButtonBuilder()
             .parent(parent)
             .key(key)
             .sizePadding(box(min(), min(), 8))
             .positioning(auto())
             .elevation($($(parent.up(1)), fp(0)))
             .axis(Y)
             .alignment(fp(0.5f))
             .disabled(useConst(false))
             .deltaElevation(fp(0.1f))
             .cornerRadius(fp(8))
             .color(Palette.GLOBAL.getPrimary())
             .callback(useConst(() -> {}))
             .children(_ -> _ -> {});
  }

  public static SimpleButtonBuilder simpleButton(Container parent, Object key, ChildrenConfiguration children) {
    return simpleButton(parent, key).children(children);
  }
}
