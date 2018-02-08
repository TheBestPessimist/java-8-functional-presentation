package tbp.land.java8.tutorial;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import tbp.land.java8.tutorial.auxiliar.entities.RandomImplementation;
import tbp.land.java8.tutorial.auxiliar.entities.someRandomInterface;
import tbp.land.random.DURR;

//@SuppressWarnings("ALL")
public class J1Lambda {

  private List<String> names = Arrays.asList("peter", "anna", "mike", "xenia");

  public static void main(String[] args) {
    new J1Lambda().run();
  }

  private void run() {
//    comparatorDecreasingly();
//    runnable();
//    lambdaScoping();
//    functionalInterface1TheBeginnings();
//    functionalInterface2WhatAmIDoingHere();
//    predicate();
//    functions();
//    suppliers();
//    consumers();
//    optionals();
  }

  /**
   * Consumers accept a single argument and do some operation with it but don't return anything.
   * They operate via side-effects.
   *
   * Please notice how functional composition works!
   */
  private void consumers() {
    //
    // expanded version
    Consumer<Integer> dividerBy2 = new Consumer<Integer>() {
      @Override
      public void accept(Integer i) {
        System.out.printf("divide by 2: original=%d, divided=%d%n", i, i / 2);
      }
    };

    //
    // functional version
    Consumer<Integer> dividerBy3Functional = (it) -> System.out.printf("divide by 3: original=%d, divided=%d%n", it, it / 3);

    dividerBy2.accept(666);
    dividerBy3Functional.andThen(dividerBy2).accept(666);
  }

  /**
   * An alternative to returning nulls.
   */
  private void optionals() {
    try {
      //
      //
      Optional<RandomImplementation> randomImpl =Optional.empty();

      System.out.println(randomImpl.isPresent());
      System.out.println(randomImpl.get());
      System.out.println(randomImpl.orElseThrow(() -> new Exception("there is NO class here")));


      //
      //
      Optional<RandomImplementation> randomOptional = Optional.ofNullable(null);

      System.out.println(randomOptional.isPresent());
      System.out.println(randomOptional.get());
      System.out.println(randomOptional.orElseThrow(() -> new Exception("there is NO class here")));

      //
      //
      Optional<RandomImplementation> optionalExists = Optional.of(new RandomImplementation());
      System.out.println(optionalExists.isPresent());
      System.out.println(optionalExists.get());
      System.out.println(optionalExists.orElseThrow(() -> new Exception("there is A class here")));

    } catch (Exception e) {
      System.out.println("in the exception: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Produce a new object of a specified type
   */
  private void suppliers() {
    // simple constructor

    //
    // expanded version
    Supplier<DURR> detatietor = new Supplier<DURR>() {
      @Override
      public DURR get() {
        return new DURR();
      }
    };

    //
    // functional version
    Supplier<DURR> detatietorFunctional = DURR::new;

    System.out.println(detatietor.get());
    System.out.println(detatietorFunctional.get());

    //
    // constructor with parameters
    //
    // expanded version
    Supplier<StringJoiner> joinerSupplier = new Supplier<StringJoiner>() {
      @Override
      public StringJoiner get() {
        return new StringJoiner(",", "[", "]");
      }
    };

    //
    // functional version
    Supplier<StringJoiner> joinerSupplierFunctional = () -> {
      return new StringJoiner(",", "[", "]");
    };

    System.out.println(joinerSupplier.get());
    StringJoiner sjf = joinerSupplierFunctional.get();    // the supplier returns a StringJoiner
    System.out.println(sjf);
  }

  /**
   * accept a single argument and produce a result
   */
  private void functions() {
    Map<String, Integer> nameAndAge = new HashMap<>();
    nameAndAge.put("cristi", 18);
    nameAndAge.put("gigi", 29);

    //
    // expanded version
    Function<Map, String> mapToString = new Function<Map, String>() {
      @Override
      public String apply(Map it) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, Integer> aa : ((Map<String, Integer>) it).entrySet()) {
          sb.append(aa.getKey() + " " + aa.getValue()).append(",");
        }

        return sb.toString();
      }
    };

    //
    // functional version
    Function<Map, String> mapToStringFunctional = (it) -> {
      StringBuilder sb = new StringBuilder();

      for (Map.Entry<String, Integer> aa : ((Map<String, Integer>) it).entrySet()) {
        sb.append(aa.getKey() + " " + aa.getValue()).append(",");
      }

      return sb.toString();
    };

    System.out.println(mapToString.apply(nameAndAge));
    System.out.println(mapToStringFunctional.apply(nameAndAge));
  }

  /**
   * Predicates: return a boolean and get one argument:
   */
  private void predicate() {
    Predicate<String> predicate = new Predicate<String>() {
      @Override
      public boolean test(String s) {
        return s.length() > 0;
      }
    };

    Predicate<String> predicateFunctional = (s) -> s.length() > 0;

    predicateFunctional.test("foo");              // true
    predicateFunctional.negate().test("foo");     // false

    Predicate<Boolean> nonNull = Objects::nonNull;
    Predicate<Boolean> isNull = Objects::isNull;

    Predicate<String> isEmpty = String::isEmpty;
    Predicate<String> isNotEmpty = isEmpty.negate();
  }

  /**
   * The method someRandomMethod from someRandomInterface is not explicitely overriden.
   * Since the interface is "functional" java knows that this is the only method i can override
   * with my lambda!
   */
  private void functionalInterface2WhatAmIDoingHere() {
    RandomImplementation ri = new RandomImplementation();
    ri.callTheInterface(() -> {
      int a = 66;
      int b = 88;
      System.out.printf("The sum of %d and %d is %s", a, b, a + b);
    });
  }

  /**
   * A lambda is an INTERFACE which has A SINGLE abstract method.
   *
   * In order to make functional interfaces easier to be distinguished, they are annotated with @FunctionalInterface
   *
   */
  private void functionalInterface1TheBeginnings() {
    // this is the normal way to instantiate something which belongs to an interface
    someRandomInterface i = new someRandomInterface() {
      @Override
      public void someRandomMethod() {
        System.out.println("some random method of some random interface normal implementation");
      }
    };
    i.someRandomMethod();

    // but since the interface is functional, we can also use it in the functional way
    someRandomInterface i2 = () -> System.out.println("this is the functional call of that random interface");
    i2.someRandomMethod();
  }

  /**
   * Lambdas can acces variables in the outer scope as long as
   * - they are final or practically final (this means that variables shouldn't be modified inside or after a lambda)
   * - they are (instance) fields or (static) fields of a class
   */
  private void lambdaScoping() {
    // final
    final int finalInt = 1;
    names.forEach(it -> System.out.println(String.valueOf(it + finalInt)));

    //
    // practically final
    int practicallyFinalInt = 1;
    names.forEach(it -> System.out.println(String.valueOf(it + practicallyFinalInt)));

    //
    // not final throws compiler error
    int notFinalInt = 1;
    names.forEach(it -> System.out.println(String.valueOf(it + notFinalInt)));
//    notFinalInt++;

    names.forEach(it -> System.out.println(it.toUpperCase()));
  }

  /**
   * How a runnable was created pre-java8, and how it can be done by using functional style
   */
  private void runnable() {
    // old method (pre-java8)
    Runnable someRunnable = new Runnable() {
      @Override
      public void run() {
        System.out.println("java 7 runnable");
      }
    };
    new Thread(someRunnable).start();

    //
    // java8 but still long
    Runnable someRunnableFunctional = () -> {
      System.out.printf("java 8 runnable%n");
    };
    new Thread(someRunnableFunctional).start();

    //
    // java8 shorter
    new Thread(() -> {
      System.out.printf("java 8 runnable no runnable object%n");
    }).start();

    //
    // java8 shortest
    new Thread(() -> System.out.println("java 8 runnable without braces")).start();

  }

  /**
   * How a comparator was used/created before java 8
   * Also: how lambdas should be "interpreted"/understood
   */
  private void comparatorDecreasingly() {
    // old method (pre-java8)
    Collections.sort(names, new Comparator<String>() {
      @Override
      public int compare(String a, String b) {
        return b.compareTo(a);
      }
    });

    //
    // java8 but still long
    Collections.sort(names, (String a, String b) -> {
      return b.compareTo(a);
    });

    //
    // java8 shorter
    Collections.sort(names, (String a, String b) -> b.compareTo(a));

    //
    // java8 shortest
    Collections.sort(names, (a, b) -> b.compareTo(a));

    //
    // java 8 already implemented functionality
    Collections.sort(names, Comparator.reverseOrder());
  }
}
