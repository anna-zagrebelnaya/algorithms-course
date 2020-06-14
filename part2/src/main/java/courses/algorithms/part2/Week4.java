package courses.algorithms.part2;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class Week4 {

    private static final int TARGET_LOWER_BOUND = -10000;
    private static final int TARGET_UPPER_BOUND = 10000;
    private static final int TARGET_LOWER_BOUND_SMALL = -20;
    private static final int TARGET_UPPER_BOUND_SMALL = 20;

    public static class SimpleExample {
        public static void main(String[] args) throws IOException, URISyntaxException {
            List<Long> longList = readLongs("week4/SmallIntegerArray.txt");
            runAlgorithm(longList, TARGET_LOWER_BOUND_SMALL, TARGET_UPPER_BOUND_SMALL);
        }
    }

    /*
    * run this in parallel, otherwise it takes too much time
    */
    public static void main(String[] args) throws IOException, URISyntaxException, ExecutionException, InterruptedException {
        List<Long> longList = readLongs("week4/BigLongArray.txt");
        runAlgorithmInParallel(longList, TARGET_LOWER_BOUND, TARGET_UPPER_BOUND);
    }

    private static void runAlgorithm(List<Long> longList, int lowerBound, int upperBound) {
        int targetSatisfiedCounter = 0;
        Set<Long> numbersSet = new HashSet<>(longList);
        for (long t=lowerBound; t<=upperBound; t++) {
            boolean targetIsSatisfied = false;
            for (long number: longList) {
                if (numbersSet.contains(t - number)
                        && (t - number != number)) {
                    targetSatisfiedCounter++;
                    targetIsSatisfied = true;
                    System.out.println("target: " + t + ", satisfied, " +
                            "numbers: (" + number + ", " + (t - number) + ") ");
                    break;
                }
            }
            if (!targetIsSatisfied) {
                System.out.println("target: " + t + ", NOT satisfied");
            }
        }
        System.out.println("Target in the interval [" + lowerBound + ";" + upperBound + "] " +
                "was satisfied " + targetSatisfiedCounter + " times");
    }

    private static void runAlgorithmInParallel(List<Long> longList, int lowerBound, int upperBound) throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();
        CompletionService<Boolean> completionService = assignTasks(longList, lowerBound, upperBound);
        int targetSatisfiedCounter = processResults(completionService, lowerBound, upperBound);
        System.out.println("Target in the interval [" + lowerBound + ";" + upperBound + "] " +
                "was satisfied " + targetSatisfiedCounter + " times");
        long minutes = (System.currentTimeMillis() - start) / (1000*60);
        System.out.println("Process took " + minutes + " minutes");
    }

    private static CompletionService<Boolean> assignTasks(List<Long> longList, int lowerBound, int upperBound) {
        int threadCount = Runtime.getRuntime().availableProcessors();
        System.out.println("Start assigning tasks, threads count: " + threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        Set<Long> numbersSet = new HashSet<>(longList);
        CompletionService<Boolean> completionService = new ExecutorCompletionService<>(executorService);
        for (long t=lowerBound; t<=upperBound; t++) {
            TargetSatisfactionTask task = new TargetSatisfactionTask(numbersSet, t);
            completionService.submit(task);
        }
        System.out.println("Tasks are assigned");
        executorService.shutdown();
        return completionService;
    }

    private static int processResults(CompletionService<Boolean> completionService, int lowerBound, int upperBound) throws InterruptedException, ExecutionException {
        int totalTargets = upperBound - lowerBound;
        int targetsProcessed = 0;
        int targetSatisfiedCounter = 0;
        System.out.println("Start processing results");
        for (int i=0; i<=totalTargets; i++) {
            Future<Boolean> future = completionService.take();
            if (future.get()) {
                targetSatisfiedCounter++;
            }
            targetsProcessed++;
            double percent = ((double) targetsProcessed / totalTargets) * 100;
            if (percent%1 == 0) {
                System.out.println("Targets processed: " + percent + "% (" + targetsProcessed + "/" + totalTargets + ")");
            }
        }
        return targetSatisfiedCounter;
    }

    private static class TargetSatisfactionTask implements Callable<Boolean> {

        private Set<Long> numbers;
        private long target;

        public TargetSatisfactionTask(Set<Long> numbers, long target) {
            this.numbers = numbers;
            this.target = target;
        }

        @Override
        public Boolean call() {
            for (long number: numbers) {
                if (numbers.contains(target - number)
                        && (target - number != number)) {
//                    System.out.println("target: " + target + ", satisfied, " +
//                            "numbers: (" + number + ", " + (target - number) + ") ");
                    return true;
                }
            }
            return false;
        }
    }

    private static List<Long> readLongs(String fileName) throws IOException, URISyntaxException {
        Path path = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
        return Files.readAllLines(path)
                .stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }


}
