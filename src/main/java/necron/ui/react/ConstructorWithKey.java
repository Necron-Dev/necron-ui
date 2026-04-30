package necron.ui.react;

public interface ConstructorWithKey<T> extends WithKey {
  T construct();
}
