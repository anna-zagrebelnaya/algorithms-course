package courses.algorithms.part2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Week3 {

    private static final int SUM_MODULO = 10000;
    private static final int SUM_MODULO_SMALL = 10;

    public static class SimpleExample {
        public static final void main(String[] args) throws IOException, URISyntaxException {
            List<Integer> integerList = readIntegers("week3/SmallIntegerArray.txt");
            runAlgorithm(integerList, SUM_MODULO_SMALL);
        }
    }

    public static final void main(String[] args) throws IOException, URISyntaxException {
        List<Integer> integerList = readIntegers("week3/BigIntegerArray.txt");
        runAlgorithm(integerList, SUM_MODULO);
    }

    /*      min heap
     *       \ 6 |
     *       \ 5 |
     *       \_4_|
     *
     *  ___
     * | 3 \
     * | 2 \
     * | 1 \
     * max heap
     * */
    private static void runAlgorithm(List<Integer> integerList, int sumModulo) {
        List<Integer> medians = new ArrayList<>();
        Queue<Integer> heapMax = new PriorityQueue<>(Comparator.reverseOrder());
        Queue<Integer> heapMin = new PriorityQueue<>();
        for (int i=0; i<integerList.size(); i++) {
            int newElement = integerList.get(i);
            if (heapMax.isEmpty() || newElement < heapMax.element()) {
                heapMax.offer(newElement);
            } else {
                heapMin.offer(newElement);
            }
            //re-balance
            if (heapMax.size() - heapMin.size() > 1) {
                heapMin.offer(heapMax.poll());
            } else if (heapMin.size() - heapMax.size() > 1) {
                heapMax.offer(heapMin.poll());
            }
            int median = defineMedian(heapMax, heapMin);
            medians.add(median);
//            System.out.println("heapMax: " + heapMax);
//            System.out.println("heapMin: " + heapMin);
//            System.out.println("median: " + median);
        }
        int sum = medians.stream().mapToInt(m -> m).sum();
        System.out.println("sum: " + sum);
        System.out.println("result: " + sum % sumModulo);
    }

    private static int defineMedian(Queue<Integer> heapMax, Queue<Integer> heapMin) {
        return heapMax.size() >= heapMin.size() ?
                heapMax.element() :
                heapMin.element();
    }

    private static List<Integer> readIntegers(String fileName) throws IOException, URISyntaxException {
        Path path = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
        return Files.readAllLines(path)
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }


}
