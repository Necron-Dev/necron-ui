package necron.ui.layout;

import lombok.Value;
import lombok.With;
import lombok.val;
import necron.ui.react.CalcReact;
import necron.ui.react.ConstReact;
import necron.ui.react.React;

import java.util.*;
import java.util.function.Consumer;

import static necron.ui.layout.Dim.flex;
import static necron.ui.layout.Dim.fp;
import static necron.ui.react.React.constant;
import static necron.ui.react.React.react;
import static necron.ui.util.fn.Fn2.fn;

public interface Dim {
  @With
  @Value
  class CreateResult {
    React<Float> react;
    Consumer<React<Float>[]> addReacts, removeReacts;
  }

  CreateResult create(React<Float> space, boolean isMajorAxis);

  boolean isIndependent();

  boolean isIndependentOnChildren();

  default Dim plus(Dim other) {
    return new Sum(this, other);
  }

  default Dim plus(React<Float> other) {
    return new HookSum(this, other);
  }

  @Value
  class Fixed implements Dim {
    float value;

    public React<Float> create() {
      return fp(value);
    }

    @Override
    public CreateResult create(React<Float> space, boolean isMajorAxis) {
      return new CreateResult(create(), null, null);
    }

    @Override
    public boolean isIndependent() {
      return true;
    }

    @Override
    public boolean isIndependentOnChildren() {
      return true;
    }
  }

  @Value
  class ReactFixed implements Dim {
    React<Float> value;

    public React<Float> create() {
      return value;
    }

    @Override
    public CreateResult create(React<Float> space, boolean isMajorAxis) {
      return new CreateResult(create(), null, null);
    }

    @Override
    public boolean isIndependent() {
      return true;
    }

    @Override
    public boolean isIndependentOnChildren() {
      return true;
    }
  }

  @Value
  class Flex implements Dim {
    float value;

    public React<Float> create(React<Float> space) {
      return react(space, v -> v * value);
    }

    @Override
    public CreateResult create(React<Float> space, boolean isMajorAxis) {
      return new CreateResult(create(space), null, null);
    }

    @Override
    public boolean isIndependent() {
      return false;
    }

    @Override
    public boolean isIndependentOnChildren() {
      return true;
    }
  }

  @Value
  class ReactFlex implements Dim {
    React<Float> react;

    public React<Float> create(React<Float> space) {
      return react(
        fn((Float a, Float b) -> a * b),
        space, react
      );
    }

    @Override
    public CreateResult create(React<Float> space, boolean isMajorAxis) {
      return new CreateResult(create(space), null, null);
    }

    @Override
    public boolean isIndependent() {
      return false;
    }

    @Override
    public boolean isIndependentOnChildren() {
      return true;
    }
  }

  enum Min implements Dim {
    INSTANCE;

    @Override
    public String toString() {
      return "Min";
    }

    public MinReact create(boolean isMajorAxis) {
      return new MinReact(isMajorAxis);
    }

    @Override
    public CreateResult create(React<Float> space, boolean isMajorAxis) {
      val minReact = create(isMajorAxis);
      return new CreateResult(minReact, minReact::add, minReact::remove);
    }

    @Override
    public boolean isIndependent() {
      return true;
    }

    @Override
    public boolean isIndependentOnChildren() {
      return false;
    }

    public static class MinReact extends CalcReact<Float> {
      private final boolean isMajorAxis;
      private final List<React<Float>> reacts;

      public MinReact(boolean isMajorAxis) {
        this.isMajorAxis = isMajorAxis;
        reacts = new ArrayList<>();
        super(Objects::equals);
      }

      @Override
      protected Float calculate() {
        var size = 0F;
        for (val react : reacts) {
          val reactSize = react.peek();
          if (isMajorAxis) {
            size += reactSize;
          } else if (reactSize > size) {
            size = reactSize;
          }
        }
        return size;
      }

      @SafeVarargs
      public final void add(React<Float>... reacts) {
        Collections.addAll(this.reacts, reacts);
        dependsOn(reacts);
        forceUpdate();
      }

      @SafeVarargs
      public final void remove(React<Float>... reacts) {
        this.reacts.removeAll(Arrays.asList(reacts));
        cancelDependencies(reacts);
        forceUpdate();
      }
    }
  }

  @Value
  class Sum implements Dim {
    Dim a, b;

    @Override
    public CreateResult create(React<Float> space, boolean isMajorAxis) {
      val resultA = a.create(space, isMajorAxis);
      val resultB = b.create(space, isMajorAxis);
      val ar = resultA.react;
      val br = resultA.react;
      return new CreateResult(
        react(fn(Float::sum), ar, br),
        x -> {
          resultA.addReacts.accept(x);
          resultB.addReacts.accept(x);
        },
        x -> {
          resultA.removeReacts.accept(x);
          resultB.removeReacts.accept(x);
        }
      );
    }

    @Override
    public boolean isIndependent() {
      return a.isIndependent() && b.isIndependent();
    }

    @Override
    public boolean isIndependentOnChildren() {
      return a.isIndependentOnChildren() && b.isIndependentOnChildren();
    }
  }

  @Value
  class HookSum implements Dim {
    Dim a;
    React<Float> b;

    @Override
    public CreateResult create(React<Float> space, boolean isMajorAxis) {
      val resultA = a.create(space, isMajorAxis);
      val ar = resultA.react;
      return resultA.withReact(react(fn(Float::sum), ar, b));
    }

    @Override
    public boolean isIndependent() {
      return a.isIndependent();
    }

    @Override
    public boolean isIndependentOnChildren() {
      return a.isIndependentOnChildren();
    }
  }

  Fixed ZERO = new Fixed(0F);

  React<Float> ZERO_REACT = ZERO.create();

  Flex UNIT = new Flex(1F);

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

  static React<Float> fixedHook(float value) {
    return value == 0F ? ZERO_REACT : fixed(value).create();
  }

  static React<Float> zeroHook() {
    return ZERO_REACT;
  }

  static React<Float> pxHook(float value) {
    return fixedHook(value);
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
    return constant(v);
  }
}
