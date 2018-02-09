package tbp.land.java8.tutorial;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class J2StreamOperations {

  List<String> strings = Arrays.asList("d2", "a2", "b1", "a1", "b3", "c6", "b2", "d1");

  public static void main(String[] args) {
    new J2StreamOperations().run();
  }

  /**
   * Streams represent sequences of elements.
   * Note that there is no link between the streams presented here and other File or Binary Streams.
   *
   * Operations can be done on streams: intermediate operations or terminal operations.
   *
   * Streams can be sequential or parallel.
   */
  private void run() {
//    streamConsumer();
//    filter();
//    sorted();
//    map();
//    match();
//    count();
//    parallelStreamSpeed();
//    streamsForMaps();
  }

  private void parallelStreamSpeed() {
    int elementNo = 10_000_000;
    List<String> values = new ArrayList<>(elementNo);

    long prep0 = System.nanoTime();
    for (int i = 0; i < elementNo; i++) {
      UUID uuid = UUID.randomUUID();
      values.add(uuid.toString());
    }
    long prep1 = System.nanoTime();
    long prep2 = TimeUnit.NANOSECONDS.toMillis(prep1 - prep0);
    System.out.println(String.format("preparation of data for %d elements took: %d ms (%s)", elementNo, prep2, Duration.of(prep2, ChronoUnit.MILLIS)));

    // parallel sort
    long ps0 = System.nanoTime();
    long countps = values.parallelStream().sorted().count();
    long ps1 = System.nanoTime();
    long ps2 = TimeUnit.NANOSECONDS.toMillis(ps1 - ps0);
    System.out.println(String.format("parallel sort took: %d ms (%s)", ps2, Duration.of(ps2, ChronoUnit.MILLIS)));

    // serial sort
    long ss0 = System.nanoTime();
    long conutss = values.stream().sorted().count();
    long ss1 = System.nanoTime();
    long ss2 = TimeUnit.NANOSECONDS.toMillis(ss1 - ss0);
    System.out.println(String.format("sequential sort took: %d ms (%s)", ss2, Duration.of(ss2, ChronoUnit.MILLIS)));

  }

  /**
   * Maps don't have streaming capabilities in java 8 though :(.
   *
   * Their Entries do however :^).
   *
   * Still, cannot have the key and value "destructured" (unpacked), so we are always left to writing
   *    entry.getKey() and entry.getValue() respectively, instead of (key, value) -> {...}
   */
  private void streamsForMaps() {
    Map<String, Integer> m = new HashMap<>();
    m.put("e", 101);
    m.put("a", 97);
    m.put("c", 99);
    m.put("g", 103);
    m.put("b", 98);
    m.put("f", 102);
    m.put("d", 100);

    //    m.stream()    // nope!

    // this is possible, though
    m.entrySet().stream() //
      .filter((Map.Entry<String, Integer> entry) -> {
        System.out.printf("filter! key: %s, val: %s %n", entry.getKey(), entry.getValue());
        return (entry.getValue() % 2) == 0;
      })  //
      .forEach(e -> System.out.println(e.getKey() + " " + e.getValue()));

    // what i would like to have is a "destructuring" Map.Entry such as:
    //    m.entrySet().stream().forEach((key, value) -> System.out.println(key + " " + value));
  }

  /**
   * Count the number of elements in a stream.
   *
   * Terminal operation.
   */
  private void count() {
    long count = strings.stream().count();
    System.out.println(count);

    Predicate<String> divisibleBy2 = (it) -> Integer.parseInt(it.substring(1, 2)) % 2 == 0;

    count = strings.stream() //
      .filter(divisibleBy2.negate()) //
      .count();
    System.out.println(count);
  }

  /**
   * Check whether some predicate matches parts of the stream.
   *
   * Terminal operation.
   */
  private void match() {
    boolean anyStartsWithA =
      strings.stream()
        .anyMatch((s) -> s.startsWith("a"));

    System.out.println("anyStartsWithA: " + anyStartsWithA);

    boolean allStartsWithA =
      strings.stream()
        .filter(Objects::nonNull)
        .allMatch((s) -> s.startsWith("a"));

    System.out.println("allStartsWithA: " + allStartsWithA);

    boolean noneStartsWithZ =
      strings.stream()
        .noneMatch((s) -> s.startsWith("z"));

    System.out.println("noneStartsWithZ: " + noneStartsWithZ);
  }

  private void map() {

    List<Integer> ints = strings.stream()  //
      .map((String it) -> Integer.parseInt(it.substring(1, 2)))  //
      .collect(Collectors.toList());

    System.out.println(ints);
  }

  /**
   * Sort data.
   *
   * Similar to {@link J1Lambda#comparatorDecreasingly()}
   *
   * sorting data in streams does not modify the original collection
   */
  private void sorted() {
    strings.stream().sorted().forEach(System.out::println);

    System.out.println(strings);

    strings.stream() //
      .sorted((it1, it2) -> it1.substring(1, 2).compareTo(it2.substring(1, 2))) //
      .forEach(System.out::println);

    List<String> collect = strings.stream() //
      .sorted(Comparator.comparing(it -> it.substring(1, 2))) //
      .collect(Collectors.toList());

    System.out.println(collect);
    System.out.println(strings);
  }

  /**
   * select only those elements which pass certain tests
   */
  private void filter() {
    for (String it : strings) {
      if (Integer.parseInt(it.substring(1, 2)) % 2 == 0) {
        System.out.println(it);
      }
    }

    System.out.println();
    System.out.println();

    strings.stream() //
      .filter((it) -> Integer.parseInt(it.substring(1, 2)) % 2 == 0) //
      .forEach(System.out::println);

    System.out.println();
    System.out.println();

    Predicate<String> divisibleBy2 = (it) -> Integer.parseInt(it.substring(1, 2)) % 2 == 0;
    strings.stream() //
      .filter(divisibleBy2.negate()) //
      .forEach(System.out::println);
  }

  /**
   * Notice (again) that consumer composition does not work on the "result" of the previous consumer,
   * but actually works on the original objects.
   */
  private void streamConsumer() {
//    strings.stream().forEach((it) -> System.out.println(it));

    Consumer<String> c1 = (it) -> System.out.printf("c1.(%s,%s) ", it, it.substring(0, 1));
    Consumer<String> c2 = (it) -> System.out.printf("c2=(%s,%s) ||", it, it.substring(1, 2));

    strings.stream().forEach(c1.andThen(c2));
  }

}
