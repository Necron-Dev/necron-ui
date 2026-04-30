package necron.ui.layout;

import lombok.Value;
import lombok.val;
import necron.ui.react.ConstReact;
import necron.ui.react.ListReact;
import necron.ui.react.React;

import static necron.ui.layout.Dim.flex;
import static necron.ui.layout.Dim.fp;
import static necron.ui.react.React.*;
import static necron.ui.util.fn.Fn2.fn;

public interface Dim {
  React<Float> create(ListReact<React<Float>> children, React<Float> space, boolean isMajorAxis);

  boolean isIndependent();

  default Dim plus(Dim other) {
    return new Sum(this, other);
  }

  default Dim plus(React<Float> other) {
    return new HookSum(this, other);
  }

  @Value
  class Fixed implements Dim {
    float value;

    @Override
    public React<Float> create(ListReact<React<Float>> children, React<Float> space, boolean isMajorAxis) {
      return fp(value);
    }

    @Override
    public boolean isIndependent() {
      return true;
    }
  }

  @Value
  class ReactFixed implements Dim {
    React<Float> value;

    @Override
    public React<Float> create(ListReact<React<Float>> children, React<Float> space, boolean isMajorAxis) {
      return value;
    }

    @Override
    public boolean isIndependent() {
      return true;
    }
  }

  @Value
  class Flex implements Dim {
    float value;

    @Override
    public React<Float> create(ListReact<React<Float>> children, React<Float> space, boolean isMajorAxis) {
      return react(space, v -> v * value);
    }

    @Override
    public boolean isIndependent() {
      return false;
    }
  }

  @Value
  class ReactFlex implements Dim {
    React<Float> react;

    @Override
    public React<Float> create(ListReact<React<Float>> children, React<Float> space, boolean isMajorAxis) {
      return react(
        fn((Float a, Float b) -> a * b),
        space, react
      );
    }

    @Override
    public boolean isIndependent() {
      return false;
    }
  }

  enum Min implements Dim {
    INSTANCE;

    @Override
    public String toString() {
      return "Min";
    }

    @Override
    public React<Float> create(ListReact<React<Float>> children, React<Float> space, boolean isMajorAxis) {
      return listDepend(
        react(
          children, childLengths -> {
            var length = 0F;
            for (val childLength : childLengths) {
              val reactSize = childLength.peek();
              if (isMajorAxis) {
                length += reactSize;
              } else if (reactSize > length) {
                length = reactSize;
              }
            }
            return length;
          }
        ),
        children
      );
    }

    @Override
    public boolean isIndependent() {
      return true;
    }
  }

  @Value
  class Sum implements Dim {
    Dim a, b;

    @Override
    public React<Float> create(ListReact<React<Float>> children, React<Float> space, boolean isMajorAxis) {
      return react(
        fn(Float::sum),
        a.create(children, space, isMajorAxis),
        b.create(children, space, isMajorAxis)
      );
    }

    @Override
    public boolean isIndependent() {
      return a.isIndependent() && b.isIndependent();
    }
  }

  @Value
  class HookSum implements Dim {
    Dim a;
    React<Float> b;

    @Override
    public React<Float> create(ListReact<React<Float>> children, React<Float> space, boolean isMajorAxis) {
      return react(fn(Float::sum), a.create(children, space, isMajorAxis), b);
    }

    @Override
    public boolean isIndependent() {
      return a.isIndependent();
    }
  }

  Fixed ZERO = new Fixed(0F);
  Flex UNIT = new Flex(1F);
  ConstReact<Float> FP_0 = constant(0F);
  ConstReact<Float> FP_0_5 = constant(0.5F);
  ConstReact<Float> FP_1 = constant(1F);

  static Fixed fixed(float value) {
    return value == 0F ? ZERO : new Fixed(value);
  }

  static ReactFixed fixed(React<Float> value) {
    return new ReactFixed(value);
  }

  static Fixed zero() {
    return ZERO;
  }

  static Fixed px(float value) {
    return fixed(value);
  }

  static ReactFixed px(React<Float> value) {
    return fixed(value);
  }

  static Flex flex(float value) {
    return value == 1F ? UNIT : new Flex(value);
  }

  static Flex flex() {
    return UNIT;
  }

  static Flex flex(float num, float den) {
    return num == den ? UNIT : flex(num / den);
  }

  static Flex percent(float value) {
    return value == 100F ? UNIT : flex(value / 100F);
  }

  static ReactFlex flex(React<Float> react) {
    return new ReactFlex(react);
  }

  static Flex max() {
    return UNIT;
  }

  static Min min() {
    return Min.INSTANCE;
  }

  static ConstReact<Float> fp(float v) {
    return v == 0F ? FP_0 :
           v == 1F ? FP_1 :
           v == 0.5F ? FP_0_5 :
           constant(v);
  }
}
