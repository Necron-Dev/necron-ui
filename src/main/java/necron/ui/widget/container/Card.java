package necron.ui.widget.container;

import lombok.Builder;
import lombok.val;
import necron.ui.layout.Axis;
import necron.ui.layout.Dim;
import necron.ui.layout.Padding;
import necron.ui.layout.Pos;
import necron.ui.react.React;
import necron.ui.style.Palette;
import necron.ui.widget.ChildrenConfiguration;
import necron.ui.widget.Container;
import necron.ui.widget.element.RoundedRect;

import static necron.ui.layout.Dim.fp;
import static necron.ui.layout.Dim.min;
import static necron.ui.layout.Padding.padding;
import static necron.ui.layout.Pos.auto;
import static yqloss.E.$;

public class Card extends Div {
  @Builder(builderMethodName = "card")
  public Card(
    Container parent,
    Object key,
    Dim width,
    Dim height,
    Padding padding,
    Pos xPos,
    Pos yPos,
    React<Float> elevation,
    Axis axis,
    React<Float> alignment,
    React<Float> cornerRadius,
    React<Integer> innerColor,
    React<Integer> outerColor,
    ChildrenConfiguration children
  ) {
    super(
      parent,
      key,
      width,
      height,
      padding,
      xPos,
      yPos,
      elevation,
      axis,
      alignment,
      ctx -> {
        val builder = children.getChildrenBuilder(ctx);
        return dsl -> {
          RoundedRect.background(dsl, cornerRadius, innerColor, outerColor);
          builder.buildChildren(dsl);
        };
      }
    );
  }

  public Card(CardBuilder builder) {
    this(
      builder.parent,
      builder.key,
      builder.width,
      builder.height,
      builder.padding,
      builder.xPos,
      builder.yPos,
      builder.elevation,
      builder.axis,
      builder.alignment,
      builder.cornerRadius,
      builder.innerColor,
      builder.outerColor,
      builder.children
    );
  }

  public static CardBuilder card(Container parent, Object key) {
    return new CardBuilder()
             .parent(parent)
             .key(key)
             .width(min())
             .height(min())
             .padding(padding())
             .xPos(auto())
             .yPos(auto())
             .elevation($($(parent.up(1)), fp(0)))
             .axis(Axis.Y)
             .alignment(fp(0))
             .cornerRadius(fp(12))
             .innerColor(Palette.GLOBAL.getBackground())
             .outerColor(Palette.GLOBAL.getBorder())
             .children(_ -> _ -> {});
  }

  public static CardBuilder card(Container parent, Object key, ChildrenConfiguration children) {
    return card(parent, key).children(children);
  }
}
