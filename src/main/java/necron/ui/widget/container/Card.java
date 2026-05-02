package necron.ui.widget.container;

import lombok.Builder;
import lombok.val;
import necron.ui.layout.Axis;
import necron.ui.layout.Box;
import necron.ui.layout.Pos;
import necron.ui.react.React;
import necron.ui.style.Palette;
import necron.ui.widget.ChildrenConfiguration;
import necron.ui.widget.Container;
import necron.ui.widget.element.RoundedRect;

import static necron.ui.layout.Box.box;
import static necron.ui.layout.Dim.fp;
import static necron.ui.layout.Dim.min;
import static necron.ui.layout.Pos.auto;
import static yqloss.E.$;

public class Card extends Div {
  @Builder(builderMethodName = "card")
  public Card(
    Container parent,
    Object key,
    Box.SizePadding sizePadding,
    Pos positioning,
    React<Float> elevation,
    Axis axis,
    React<Float> alignment,
    React<Float> cornerRadius,
    React<Integer> background,
    ChildrenConfiguration children
  ) {
    super(
      parent,
      key,
      sizePadding,
      positioning,
      elevation,
      axis,
      alignment,
      ctx -> {
        val builder = children.getChildrenBuilder(ctx);
        return dsl -> {
          RoundedRect.background(dsl, cornerRadius, background);
          builder.buildChildren(dsl);
        };
      }
    );
  }

  public static CardBuilder card(Container parent, Object key) {
    return new CardBuilder()
             .parent(parent)
             .key(key)
             .sizePadding(box(min(), min()))
             .positioning(auto())
             .elevation($($(parent.up(1)), fp(0)))
             .axis(Axis.Y)
             .alignment(fp(0))
             .cornerRadius(fp(12))
             .background(Palette.GLOBAL.getBackground())
             .children(_ -> _ -> {});
  }

  public static CardBuilder card(Container parent, Object key, ChildrenConfiguration children) {
    return card(parent, key).children(children);
  }
}
