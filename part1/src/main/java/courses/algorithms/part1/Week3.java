package courses.algorithms.part1;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Week3 {

    //                      initial     reverse     with swaps  with final swap     fixed getting item by index
    //pivot first:          157946      158774      163145      162085              162085
    //pivot last:           162330      162776      181160      175556              164123
    //pivot median three:   145788      131305      158253      150055              138382
    public static final void main(String[] args) throws IOException, URISyntaxException {
        List<Integer> integerList = readIntegers("week3/BigIntegerArray.txt");
        Integer comparisonsCountPivotFirst = countComparisons(integerList, Week3::definePivotIndex_first);
        Integer comparisonsCountPivotLast = countComparisons(integerList, Week3::definePivotIndex_last);
        Integer comparisonsCountPivotMedianThree = countComparisons(integerList, Week3::definePivotIndex_medianThree);
        System.out.println("pivot first: " + comparisonsCountPivotFirst);
        System.out.println("pivot last: " + comparisonsCountPivotLast);
        System.out.println("pivot median three: " + comparisonsCountPivotMedianThree);
    }

    private static List<Integer> readIntegers(String fileName) throws IOException, URISyntaxException {
        Path path = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
        return Files.readAllLines(path)
                .stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private static Integer countComparisons(List<Integer> list, Function<List<Integer>, Integer> definePivotFunc) {
        if (list.size() < 2) return 0;

        Integer pivotIndex = definePivotFunc.apply(list);
        Integer pivot = list.get(pivotIndex);

        Integer[] array = list.toArray(new Integer[]{});

        //partitioning
        int i = 1;
        if (pivotIndex != 0) {
            swap(array, 0, pivotIndex);
        }
        for (int j=1; j<list.size(); j++) {
            if (array[j] < pivot) {
                swap(array, i, j);
                i++;
            }
        }
        swap(array, 0, i-1);

        List<Integer> lessThanPivot = Arrays.asList(Arrays.copyOfRange(array, 0, i - 1));
        Integer comparisons1 = countComparisons(lessThanPivot, definePivotFunc);

        List<Integer> moreThanPivot = Arrays.asList(Arrays.copyOfRange(array, i, list.size()));
        Integer comparisons2 = countComparisons(moreThanPivot, definePivotFunc);

        return list.size() - 1 + comparisons1 + comparisons2;
    }

    private static void swap(Integer[] array, int i1, Integer i2) {
        int value = array[i1];
        array[i1] = array[i2];
        array[i2] = value;
    }

    private static Integer definePivotIndex_first(List<Integer> list) {
        return 0;
    }

    private static Integer definePivotIndex_last(List<Integer> list) {
        return list.size()-1;
    }

    //size = 4 => median index = 1
    //size = 5 => median index = 2
    private static Integer definePivotIndex_medianThree(List<Integer> list) {
        Integer firstIndex = 0;
        Integer lastIndex = list.size()-1;
        Integer medianIndex = list.size()%2 == 1 ? list.size()/2 : list.size()/2 - 1;

        Integer first = list.get(firstIndex);
        Integer last = list.get(lastIndex);
        Integer median = list.get(medianIndex);
        System.out.println(first + ";" + median + ";" + last);
        if ((first > last && first < median) ||
                (first < last && first > median)) {
            return firstIndex;
        } else if ((last > first && last < median) ||
                (last < first && last > median)) {
            return lastIndex;
        } else {
            return medianIndex;
        }
    }
}
