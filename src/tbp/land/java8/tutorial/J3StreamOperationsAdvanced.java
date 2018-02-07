package tbp.land.java8.tutorial;

import java.util.Arrays;
import java.util.Collections;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import tbp.land.java8.tutorial.auxiliar.entities.Person;

public class J3StreamOperationsAdvanced {

  Collector<Object, StringJoiner, String> printListCollector = //
    Collector.of(() -> new StringJoiner(", ", "[", "]") //
      , (it1, it2) -> it1.add(it2.toString()) //
      , (it1, it2) -> it1.merge(it2) //
      , it -> {
        System.out.println(it.toString());
        return it.toString();
      }
    );

  List<Person> persons = Arrays.asList(   //
    new Person("Alin", 18), //
    new Person("Dan", 23), //
    new Person("Irina", 23), //
    new Person("Oana", 88), //
    new Person("Ofelia", 12));

  public static void main(String[] args) {
    new J3StreamOperationsAdvanced().run();
  }

  /**
   * The stream operations must be both non-interfering and stateless.
   *
   * A function is non-interfering when it does not modify the underlying data source of the stream,
   * e.g. no lambda expression should modify the streaming list by adding or removing elements from it.
   *
   * A function is stateless when the execution of the operation is deterministic,
   * e.g. no lambda expression depends on any mutable variables or states
   * from the outer scope which might change during execution.
   *
   */
  private void run() {
//    howToCreateStreams();
//    streamsAreLazySimpleFilter();
//    streamsAreLazyOrderingCounts();
//    CANNOTReuseStreams();
    collectors();
//    flatMap();
//    reduce();
  }

  private void reduce() {
    // sum of ages
    int theSum = 0;
    for (Person p : persons) {
      theSum += p.getAge();
    }
    System.out.println(theSum);

    //
    //
    Integer sumOfAges = persons.stream() //
      .reduce(0, (accumulator, it) -> accumulator += it.getAge(), (it1, it2) -> it1 + it2);
    System.out.println(sumOfAges == theSum);

    //
    //
    Integer sumOfAgesDebug = persons.stream() //
      .reduce(
        0,
        (accumulator, it) -> {
          System.out.printf("in accumulator: acc=%d, person=%s%n", accumulator, it);
          return accumulator += it.getAge();
        },
        (it1, it2) -> {
          System.out.printf("in combiner: it1=%d, it2=%d%n", it1, it2);
          return it1 + it2;
        }
      );

    System.out.println(sumOfAgesDebug);

  }

  /**
   * unlike map, which can only return a single object back,
   * flatMap returns a stream of objects.
   */
  private void flatMap() {
    persons.stream() //
      .flatMap(it -> Stream.of(it.getName(), it.getAge())) //
      .collect(printListCollector);
    // any other example?
  }

  /**
   * Terminal operation.
   *
   * Takes all the elements of the stream (sequence) and packs them
   *    nicely for further use after the stream is closed.
   */
  private void collectors() {
    // simple example
    Set<Person> oPersons = persons.stream() //
      .filter(it -> it.getName().startsWith("O")) //
      .collect(Collectors.toSet());
    System.out.println(oPersons);

    //
    // grouping by age
    Map<Integer, List<Person>> personsByAge = persons.stream() //
      .collect(Collectors.groupingBy(it -> it.getAge()));

    personsByAge.forEach((key, val) -> System.out.printf("key(age): %d, val(list of persons): %s%n", key, val));

    //
    // summary of age(s)
    IntSummaryStatistics statistics = persons.stream() //
      .collect(Collectors.summarizingInt(Person::getAge));

    System.out.println(statistics);

    //
    // join everything in a string
    String personsJoined = persons.stream() //
      .flatMap(it -> Stream.of(it.getName(), Integer.toString(it.getAge()))) //
      .sorted() //
      .collect(Collectors.joining(", ", "This is a list of everything: ", ". Which is now over."));
    System.out.println(personsJoined);

    //
    // return unmodifiable list
    List<Person> unmodifiablePersons = persons.stream() //
      //      .sorted() // this throws error
      .collect(Collectors.collectingAndThen( //
        Collectors.toList(), //
        Collections::unmodifiableList) //
      );
    System.out.println(unmodifiablePersons);

    unmodifiablePersons.add(new Person("Gigel", 2));

    //
    // constructing our own collector
    // exercise: use this collector to print some Integers!
//    Collector<String, StringJoiner, String> listOfStringsToConsole = //
//      Collector.of(() -> new StringJoiner(", ", "[", "]") //
//        , (it1, it2) -> it1.add(it2) //
//        , (it1, it2) -> it1.merge(it2) //
//        , it -> {
//          System.out.println(it.toString());
//          return null;
//        }
//      );
  }

  /**
   * A stream cannot simply be reused.
   *
   * However there are suppliers, as explained in {@link J1Lambda#suppliers()},
   * so a stream can be (re-)created as many times as it is needed (though this incurs the cost of
   * re-doing all the computations when the stream is used) by calling {@link Supplier<>.get}.
   */
  private void CANNOTReuseStreams() {
    Stream<String> stream =
      Stream.of("d2", "a2", "b1", "b3", "c")
        .filter(s -> s.startsWith("a"));

    stream.anyMatch(s -> true);
    stream.noneMatch(s -> true);

    Supplier<Stream<String>> streamSupplier =
      () -> Stream.of("d2", "a2", "b1", "b3", "c")
        .filter(s -> s.startsWith("a"));

    streamSupplier.get().anyMatch(s -> true);
    streamSupplier.get().noneMatch(s -> true);
  }

  private void streamsAreLazyOrderingCounts() {
    Stream.of("d2", "a2", "b1", "b3", "c")
      .map(s -> {
        System.out.println("map: " + s);
        return s.toUpperCase();
      })
      .filter(s -> {
        System.out.println("filter: " + s);
        return s.startsWith("A");
      })
      .forEach(s -> System.out.println("forEach: " + s));

    Stream.of("d2", "a2", "b1", "b3", "c")
      .filter(s -> {
        System.out.println("filter: " + s);
        return s.startsWith("a");
      })
      .map(s -> {
        System.out.println("map: " + s);
        return s.toUpperCase();
      })
      .forEach(s -> System.out.println("forEach: " + s));

    // sorting
    Stream.of("d2", "a2", "b1", "b3", "c")
      .sorted((s1, s2) -> {
        System.out.printf("sort: %s; %s\n", s1, s2);
        return s1.compareTo(s2);
      })
      .filter(s -> {
        System.out.println("filter: " + s);
        return s.startsWith("a");
      })
      .map(s -> {
        System.out.println("map: " + s);
        return s.toUpperCase();
      })
      .forEach(s -> System.out.println("forEach: " + s));

    Stream.of("d2", "a2", "b1", "b3", "c")
      .filter(s -> {
        System.out.println("filter: " + s);
        return s.startsWith("a");
      })
      .sorted((s1, s2) -> {
        System.out.printf("sort: %s; %s\n", s1, s2);
        return s1.compareTo(s2);
      })
      .map(s -> {
        System.out.println("map: " + s);
        return s.toUpperCase();
      })
      .forEach(s -> System.out.println("forEach: " + s));
  }

  private void streamsAreLazySimpleFilter() {
    Stream.of("d2", "a2", "b1", "b3", "c")
      .filter(s -> {
        System.out.println("filter: " + s);
        return true;
      });

    Stream.of("d2", "a2", "b1", "b3", "c")
      .filter(s -> {
        System.out.println("filter: " + s);
        return true;
      })
      .forEach(s -> System.out.println("forEach: " + s));

    Stream.of("d2", "a2", "b1", "b3", "c")
      .map(s -> {
        System.out.println("map: " + s);
        return s.toUpperCase();
      })
      .anyMatch(s -> {
        System.out.println("anyMatch: " + s);
        return s.startsWith("A");
      });
  }

  private void howToCreateStreams() {
    Arrays.asList("a1", "a2", "a3")
      .stream()
      .findFirst()
      .ifPresent(System.out::println);

    Stream.of("a1", "a2", "a3")
      .findFirst()
      .ifPresent(System.out::println);

    // there is a difference between streams of objects and streams of primitives
    IntStream.range(1, 4)       // stream of primitives!
      .forEach(System.out::println);

    Arrays.stream(new int[] { 1, 2, 3 })
      .map(n -> 2 * n + 1)
      .average()
      .ifPresent(System.out::println);

    // stream of double primitives -> stream of primitive int -> stream of string
    Stream.of(1.0, 2.0, 3.0)
      .mapToInt(Double::intValue)
      .mapToObj(i -> "a" + i)
      .forEach(System.out::println);
  }
}
