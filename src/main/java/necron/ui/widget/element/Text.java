package necron.ui.widget.element;

import lombok.Builder;
import lombok.val;
import necron.ui.Lazy;
import necron.ui.context.Context;
import necron.ui.event.*;
import necron.ui.layout.Dim;
import necron.ui.layout.Pos;
import necron.ui.react.React;
import necron.ui.react.SubReact;
import necron.ui.render.TextLineRenderable;
import necron.ui.style.Palette;
import necron.ui.widget.Container;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Vector2f;

import java.util.List;

import static necron.ui.layout.Dim.*;
import static necron.ui.layout.Pos.auto;
import static necron.ui.react.React.*;
import static necron.ui.util.fn.Fn3.fn;
import static necron.ui.util.fn.Fn5.fn;
import static yqloss.E.$;

public class Text extends Node {
  private final React<FormattedText> content;
  private final React<Font> font;
  private final React<Float> fontSize;
  private final React<Float> spacing;
  private final React<Float> align;
  private final React<Integer> color;
  private final React<Boolean> wrap;
  private final React<List<FormattedCharSequence>> lines;

  @Builder(builderMethodName = "text")
  public Text(
    Container parent,
    Object key,
    Dim width,
    Dim height,
    Pos xPos,
    Pos yPos,
    React<Float> elevation,
    React<FormattedText> content,
    React<Font> font,
    React<Float> fontSize,
    React<Float> spacing,
    React<Float> align,
    React<Integer> color,
    React<Boolean> wrap
  ) {
    SubReact<Float> heightReact = null;
    height = height instanceof Dim.Min
             ? px(heightReact = useSub(fp(0), x -> x))
             : height;
    super(parent, key, width, height, xPos, yPos, elevation);
    this.content = content;
    this.font = font;
    this.fontSize = fontSize;
    this.spacing = spacing;
    this.align = align;
    this.color = color;
    this.wrap = wrap;
    lines = useCalc(
      fn((FormattedText c, Font f, Float s, Float l, Boolean w) -> {
        return f.split(c, w ? (int) (l * f.lineHeight / s) : Integer.MAX_VALUE);
      }),
      content, font, fontSize, getWidth(), wrap
    );
    if (heightReact != null) {
      heightReact.setParent(
        useCalc(
          fn((List<FormattedCharSequence> l, Float s, Float f) -> {
            return Math.max(0F, l.size() * (s + f) - s);
          }),
          lines, spacing, fontSize
        ), x -> x
      );
    }
  }

  public Text(TextBuilder builder) {
    this(
      builder.parent,
      builder.key,
      builder.width,
      builder.height,
      builder.xPos,
      builder.yPos,
      builder.elevation,
      builder.content,
      builder.font,
      builder.fontSize,
      builder.spacing,
      builder.align,
      builder.color,
      builder.wrap
    );
  }

  @Override
  public boolean dispatch(Context context, Event event, boolean handled) {
    switch (event) {
      case ContentEvent _ -> {
        content.get();
        font.get();
        wrap.get();
      }

      case MetricsEvent _ -> {
        fontSize.get();
        spacing.get();
        lines.get();
      }

      case UpdateEvent _ -> {
        color.get();
        align.get();
      }

      case RenderEvent renderEvent -> {
        val widthValue = getWidth().peek();
        val alignValue = align.peek();
        val fontValue = font.peek();
        val fontSizeValue = fontSize.peek();
        val colorValue = color.peek();
        val elevationValue = getElevation().peek();
        val spacingValue = spacing.peek();
        var y = 0F;
        for (var line : lines.peek()) {
          renderEvent.getYieldRenderable().accept(new TextLineRenderable(
            new Vector2f((widthValue - fontValue.width(line) * fontSizeValue / fontValue.lineHeight) * alignValue, y),
            line,
            fontValue,
            fontSizeValue,
            colorValue,
            elevationValue
          ));
          y += fontSizeValue + spacingValue;
        }
      }

      default -> {}
    }

    return super.dispatch(context, event, handled);
  }

  public static TextBuilder text(Container parent, Object key) {
    return new TextBuilder()
             .parent(parent)
             .key(key)
             .width(flex())
             .height(min())
             .xPos(auto())
             .yPos(auto())
             .elevation($($(parent.up(1)), fp(0)))
             .content(useConst(Component.empty()))
             .font(useConst(Lazy.MC.font))
             .fontSize(fp(8))
             .spacing(fp(2))
             .align(fp(0))
             .color(Palette.GLOBAL.getForeground())
             .wrap(useConst(true));
  }
}
