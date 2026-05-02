package necron.ui.widget;

import lombok.val;
import necron.ui.layout.Dim;
import necron.ui.react.CalcListReact;
import necron.ui.react.ConstructorWithKey;
import necron.ui.react.React;
import necron.ui.util.fn.Fn2;
import necron.ui.util.fn.Fn3;
import necron.ui.widget.container.Div;

import java.util.function.Consumer;
import java.util.function.Function;

@FunctionalInterface
public interface ChildrenConfiguration {
  ChildrenBuilder getChildrenBuilder(ChildrenContext context);

  class ChildrenContext {
    public Consumer<? super CalcListReact<? super Element>> configure = _ -> {};
  }

  @FunctionalInterface
  interface ChildrenBuilder {
    void buildChildren(ChildrenBuilderDsl dsl);
  }

  interface ChildrenBuilderDsl {
    Container parent();

    void add(Object key, Fn2<? super Container, ? super Object, ? extends Element> factory);

    default <T> void add(
      Object key,
      Fn2<? super Container, ? super Object, ? extends T> builderFactory,
      Function<? super T, ? extends Element> builder
    ) {
      add(key, (c, k) -> builder.apply(builderFactory.invoke(c, k)));
    }

    default <T> void add(
      Object key,
      Fn3<? super Container, ? super Object, ? super ChildrenConfiguration, ? extends T> builderFactory,
      Function<? super T, ? extends Element> builder,
      ChildrenConfiguration configuration
    ) {
      add(key, (c, k) -> builder.apply(builderFactory.invoke(c, k, configuration)));
    }

    default void spacer(Object key, Dim length) {
      if (!(parent() instanceof Div div)) {
        throw new IllegalArgumentException("Spacer can only be added to a Div");
      }
      add(key, (p, k) -> div.spacer(k, length));
    }

    default void divider(Object key) {
      if (!(parent() instanceof Div div)) {
        throw new IllegalArgumentException("Spacer can only be added to a Div");
      }
      add(key, (p, k) -> div.divider(k));
    }
  }

  static CalcListReact<Element> buildChildren(
    Container container,
    ChildrenConfiguration configuration
  ) {
    val context = new ChildrenContext();
    val builder = configuration.getChildrenBuilder(context);
    val react = React.<Element>useCalcList(emit -> {
      builder.buildChildren(new ChildrenBuilderDsl() {
        @Override
        public Container parent() {
          return container;
        }

        @Override
        public void add(Object key, Fn2<? super Container, ? super Object, ? extends Element> factory) {
          emit.accept(new ConstructorWithKey<>() {
            @Override
            public Element construct() {
              return factory.invoke(container, key);
            }

            @Override
            public Object getKey() {
              return key;
            }
          });
        }
      });
    });
    context.configure.accept(react);
    return react;
  }
}
