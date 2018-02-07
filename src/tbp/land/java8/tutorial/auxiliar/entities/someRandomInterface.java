package tbp.land.java8.tutorial.auxiliar.entities;

/**
 * A random functional interface which
 */
@FunctionalInterface
public interface someRandomInterface {
  void someRandomMethod();

// this will show an error, as @FunctionalInterface must only have a single method
//  void anotherRandomMethod();
}
