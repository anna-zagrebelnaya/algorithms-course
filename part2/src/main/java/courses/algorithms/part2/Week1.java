package courses.algorithms.part2;

import com.sun.tools.javac.util.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Week1 {

    public static class SimpleExample {
        public static final void main(String[] args) throws IOException, URISyntaxException {
            List<Pair<Integer, Integer>> directedGraph = readDirectedGraph("week1/SmallDirectedGraph.txt");
            runAlgorithm(directedGraph);
        }
    }

    /*
    * To be able to run this algorithm on a big graph, need to increase stack size (-Xss10m)

        reading input graph...
        reading took: 5405ms
        converting graph...
        converting took: 1065ms
        reverting graph...
        reverting took: 3572ms
        nodes count (n): 875714
        edges count (m): 5105043
        start 1st pass
        finishingTimeMap (key: t, value: node) size: 875714
        finished 1st pass: 1272ms
        ==================
        start 2d pass
        nodesInFinishingTimeOrder size: 875714
        explorationMap size: 875714
        leadersMap (key: node, value: leader) size: 875714
        finished 2d pass: 1052ms
        ==================
        start post-processing
        leadersMap (key: leader, value: nodes count(CSC size)) size: 371762
        finished post processing: 119ms
        ==================
        top 5 csc sizes:
        [434821, 968, 459, 313, 211]

    */
    public static final void main(String[] args) throws IOException, URISyntaxException {
        long start = System.currentTimeMillis();
        System.out.println("reading input graph...");
        List<Pair<Integer, Integer>> directedGraph = readDirectedGraph("week1/BigDirectedGraph.txt");
        System.out.println("reading took: " + (System.currentTimeMillis() - start) + "ms");
        runAlgorithm(directedGraph);
    }

    private static void runAlgorithm(List<Pair<Integer, Integer>> directedGraph) {
        List<Integer> cscSizes = dfsLoop(directedGraph);
        System.out.println("top 5 csc sizes:");
        System.out.println(cscSizes);
    }

    private static List<Integer> dfsLoop(List<Pair<Integer, Integer>> graphEdges) {
        long start = System.currentTimeMillis();
        System.out.println("converting graph...");
        Map<Integer, List<Integer>> originalGraph = convertToMap(graphEdges);
        long finishConverting = System.currentTimeMillis();
        System.out.println("converting took: " + (finishConverting - start) + "ms");
        List<Integer> originalNodes = new ArrayList<>(originalGraph.keySet());
        System.out.println("reverting graph...");
        Map<Integer, List<Integer>> revertedGraph = revertAndConvertToMap(graphEdges);
        long finishReverting = System.currentTimeMillis();
        System.out.println("reverting took: " + (finishReverting - finishConverting) + "ms");
        List<Integer> revertedNodes = new ArrayList<>(revertedGraph.keySet());
        revertedNodes.sort(Collections.reverseOrder());
        Set<Integer> nodes = new HashSet<>();
        nodes.addAll(originalNodes);
        nodes.addAll(revertedNodes);

        System.out.println("nodes count (n): " + nodes.size());
        System.out.println("edges count (m): " + graphEdges.size());

        Map<Integer, Integer> finishingTimeMap = getFinishingTimes(nodes, revertedNodes, revertedGraph);
        Map<Integer, Integer> leadersMap = getLeadersMap(nodes, finishingTimeMap, originalGraph);
        List<Integer> cscSizesDesc = getCscSizesDesc(leadersMap);
        return cscSizesDesc.stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    private static Map<Integer, Integer> getFinishingTimes(Set<Integer> nodes,
                                                           List<Integer> revertedNodes,
                                                           Map<Integer, List<Integer>> revertedGraph) {
        long start = System.currentTimeMillis();
        System.out.println("start 1st pass");
        //1st pass
        Map<Integer, Boolean> explorationMap = new HashMap<>();
        nodes.forEach(node -> explorationMap.put(node, false));
        Map<Integer, Integer> finishingTimeMap = new HashMap<>();

        int t = 0;

        for (int i: revertedNodes) {
            if (explorationMap.get(i)) {
                continue;
            }
            t = dfsPass1(revertedGraph, i, t, explorationMap, finishingTimeMap);
        }
        System.out.println("finishingTimeMap (key: t, value: node) size: " + finishingTimeMap.size());
        //System.out.println(finishingTimeMap);
        long finish = System.currentTimeMillis();
        System.out.println("finished 1st pass: " + (finish-start) + "ms");
        return finishingTimeMap;
    }

    private static Map<Integer, Integer> getLeadersMap(Set<Integer> nodes,
                                                       Map<Integer, Integer> finishingTimeMap,
                                                       Map<Integer, List<Integer>> originalGraph) {
        System.out.println("==================");
        System.out.println("start 2d pass");
        //2d pass
        long start = System.currentTimeMillis();
        Map<Integer, Boolean> explorationMap = new HashMap<>();
        nodes.forEach(node -> explorationMap.put(node, false));
        List<Integer> nodesInFinishingTimeOrder = IntStream.rangeClosed(1, explorationMap.size())
                .boxed()
                .sorted(Collections.reverseOrder())
                .map(finishingTimeMap::get)
                .collect(Collectors.toList());
        System.out.println("nodesInFinishingTimeOrder size: " + nodesInFinishingTimeOrder.size());
        //System.out.println(nodesInFinishingTimeOrder);
        nodes.forEach(node -> explorationMap.put(node, false));
        System.out.println("explorationMap size: " + explorationMap.size());
        Map<Integer, Integer> leadersMap = new HashMap<>();

        int s;

        for (int i: nodesInFinishingTimeOrder) {
            if (explorationMap.get(i)) {
                continue;
            }
            s = i;
            dfsPass2(originalGraph, i, s, explorationMap, leadersMap);
        }

        System.out.println("leadersMap (key: node, value: leader) size: " + leadersMap.size());
        //System.out.println(leadersMap);
        long finish = System.currentTimeMillis();
        System.out.println("finished 2d pass: " + (finish-start) + "ms");
        return leadersMap;
    }

    private static List<Integer> getCscSizesDesc(Map<Integer, Integer> leadersMap) {
        long start = System.currentTimeMillis();
        System.out.println("==================");
        System.out.println("start post-processing");
        //revert map - key: leader, value: nodes count (CSC size)
        Map<Integer, Integer> revertedLeadersMapWithCount = leadersMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue,
                        entry -> 1,
                        Integer::sum));

        System.out.println("leadersMap (key: leader, value: nodes count(CSC size)) size: " + revertedLeadersMapWithCount.size());
        //System.out.println(revertedLeadersMapWithCount);

        //sorted CSC sizes:
        List<Integer> sizes = new ArrayList<>(revertedLeadersMapWithCount.values());
        sizes.sort(Collections.reverseOrder());

        for (int i= 5 - sizes.size(); i>0; i--) {
            sizes.add(0);
        }

        long finish = System.currentTimeMillis();
        System.out.println("finished post processing: " + (finish - start) + "ms");
        System.out.println("==================");
        //System.out.println("csc sizes:");
        //System.out.println(sizes);
        return sizes;
    }

    private static int dfsPass1(Map<Integer, List<Integer>> graph, int node, int t,
                                 Map<Integer, Boolean> explorationMap,
                                 Map<Integer, Integer> finishingTimeMap) {
        explorationMap.put(node, true);
        if (graph.get(node) != null) {
            for (int j: graph.get(node)) {
                if (explorationMap.get(j) == null) {
                    System.out.println("null for " + j);
                    continue;
                } else if (explorationMap.get(j)) {
                    continue;
                }
                t = dfsPass1(graph, j, t, explorationMap, finishingTimeMap);
            }
        }
        t++;
        finishingTimeMap.put(t, node);
        return t;
    }

    private static void dfsPass2(Map<Integer, List<Integer>> graph, int node, int s,
                            Map<Integer, Boolean> explorationMap,
                            Map<Integer, Integer> leadersMap) {
        explorationMap.put(node, true);
        leadersMap.put(node, s);
        if (graph.get(node) == null)
            return;
        for (int j: graph.get(node)) {
            if (explorationMap.get(j)) {
                continue;
            }
            dfsPass2(graph, j, s, explorationMap, leadersMap);
        }
    }

    private static List<Pair<Integer, Integer>> readDirectedGraph(String fileName) throws IOException, URISyntaxException {
        Path path = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
        return Files.readAllLines(path)
                .stream()
                .map(s -> {
                    String[] nodes = s.split(" ");
                    return new Pair<>(Integer.parseInt(nodes[0]), Integer.parseInt(nodes[1]));
                })
                .collect(Collectors.toList());
    }

    private static Map<Integer, List<Integer>> convertToMap(List<Pair<Integer, Integer>> graph) {
        return graph.stream()
                .collect(Collectors.toMap(pair -> pair.fst,
                        pair -> Collections.singletonList(pair.snd),
                        (list1, list2) -> {
                            List<Integer> newList = new ArrayList<>();
                            newList.addAll(list1);
                            newList.addAll(list2);
                            return newList;
                        }));
    }

    private static Map<Integer, List<Integer>> revertAndConvertToMap(List<Pair<Integer, Integer>> graph) {
        return graph.parallelStream()
                .collect(Collectors.toMap(pair -> pair.snd,
                        pair -> Collections.singletonList(pair.fst),
                        (list1, list2) -> {
                            List<Integer> newList = new ArrayList<>();
                            newList.addAll(list1);
                            newList.addAll(list2);
                            return newList;
                        }));
    }
}
