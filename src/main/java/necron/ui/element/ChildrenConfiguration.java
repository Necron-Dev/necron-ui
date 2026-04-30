package necron.ui.element;

import lombok.val;
import necron.ui.react.CalcListReact;
import necron.ui.react.ConstructorWithKey;
import necron.ui.react.React;
import necron.ui.util.fn.Fn2;

import java.util.function.Consumer;

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

  @FunctionalInterface
  interface ChildrenBuilderDsl {
    void add(Object key, Fn2<? super Container, ? super Object, ? extends Element> factory);
  }

  static CalcListReact<Element> buildChildren(
    Container container,
    ChildrenConfiguration configuration
  ) {
    val context = new ChildrenContext();
    val builder = configuration.getChildrenBuilder(context);
    val react = React.<Element>calcList(emit -> {
      builder.buildChildren((key, factory) -> {
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
      });
    });
    context.configure.accept(react);
    return react;
  }
}
