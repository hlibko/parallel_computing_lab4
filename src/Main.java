import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {

        final List<Integer> a1 = Arrays.asList(10, 2, 2, 3, 4, 5, 6, 7, 8, 9, 120, 12, 11, 20, 33);
        final List<Integer> a2 = Arrays.asList(10, 20, 30, 40, 50, 600, 700, 800, 900, 1000);
        final List<Integer> a3 = Arrays.asList(10, 200, 300, 400, 500, 600, 700, 800, 900, 1000);

        CompletableFuture<List<Integer>> firstFuture, secondFuture, thirdFuture, resultFuture;

        // У першому масиві - елементи помножити на 5
        firstFuture = CompletableFuture.supplyAsync(() -> a1)
                .thenApplyAsync(first -> first.stream().parallel().map(el -> el * 5).collect(Collectors.toList()));

        // Залишити тільки парні.
        secondFuture = CompletableFuture
                .supplyAsync(() -> a2)
                .thenApplyAsync(first -> first.stream().parallel().filter(el -> el % 2 == 0).collect(Collectors.toList()));

        // У третьому - залишити елементи в діапазоні від 0.4 до 0.6 максимального значення.
        thirdFuture = CompletableFuture.supplyAsync(() -> a3)
                .thenApplyAsync(list -> {
                    int max = Collections.max(list);
                    return list.stream().parallel().filter(el -> el >= (max * 0.4) && el <= (max * 0.6)).collect(Collectors.toList());
                });

        // Відсортувати масиви і злити в один масив елементи відсортований масив в якому є елементи які входять в усі масиви.
        resultFuture = firstFuture
                .thenCombine(secondFuture, (first, second) -> {
                    Set<Integer> set = new HashSet<>(first);
                    set.retainAll(second);
                    return new ArrayList<>(set);
                }).thenCombine(thirdFuture, (first, second) -> {
                    Set<Integer> set = new HashSet<>(first);
                    set.retainAll(second);
                    return new ArrayList<>(set);
                }).thenApplyAsync(list -> {
                    Collections.sort(list);
                    return list;
                });

        try {
            System.out.println("Result 1: " + firstFuture.get());
            System.out.println("Result 2: " + secondFuture.get());
            System.out.println("Result 3: " + thirdFuture.get());
            System.out.println("\nResult: " + resultFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
