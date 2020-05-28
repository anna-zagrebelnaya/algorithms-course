import com.sun.tools.javac.util.Pair;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Week2 {

    public static final void main(String[] args) throws IOException, URISyntaxException {
        List<Integer> integerList = readIntegers("week2/BigIntegerArray.txt");
        BigInteger inversionsCount = countInversions(integerList).fst;
        System.out.println("inversions count: " + inversionsCount);
    }

    private static List<Integer> readIntegers(String fileName) throws IOException, URISyntaxException {
        Path path = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
        return Files.readAllLines(path)
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private static Pair<BigInteger, List<Integer>> countInversions(List<Integer> numbers) {
        if (numbers.size() == 1) {
            return new Pair<>(BigInteger.ZERO, numbers);
        }
        List<Integer> firstSubList = numbers.subList(0, numbers.size()/2);
        Pair<BigInteger, List<Integer>> first = countInversions(firstSubList);

        List<Integer> secondSubList = numbers.subList(numbers.size()/2, numbers.size());
        Pair<BigInteger, List<Integer>> second = countInversions(secondSubList);

        Pair<BigInteger, List<Integer>> split = sortAndCountSplitInversions(numbers, first.snd, second.snd);

        BigInteger inversionsCount = first.fst.add(second.fst).add(split.fst);
        return new Pair<>(inversionsCount, split.snd);
    }

    private static Pair<BigInteger, List<Integer>> sortAndCountSplitInversions(List<Integer> numbers,
                                                                            List<Integer> firstSubList,
                                                                            List<Integer> secondSubList) {
        int i = 0;
        int j = 0;
        BigInteger inversionsCount = BigInteger.ZERO;
        List<Integer> sorted = new ArrayList<>(firstSubList.size() + secondSubList.size());
        for (int k=0; k<numbers.size(); k++) {
            if (firstSubList.size() == i) {
                sorted.add(secondSubList.get(j));
                j++;
                continue;
            }
            if (secondSubList.size() == j) {
                sorted.add(firstSubList.get(i));
                i++;
                continue;
            }

            if (firstSubList.get(i) < secondSubList.get(j)) {
                sorted.add(firstSubList.get(i));
                i++;
            } else {
                sorted.add(secondSubList.get(j));
                j++;
                inversionsCount = inversionsCount.add(BigInteger.valueOf(firstSubList.size() - i));
            }
        }
        return new Pair<>(inversionsCount, sorted);
    }
}
