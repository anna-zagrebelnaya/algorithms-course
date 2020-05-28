import com.sun.tools.javac.util.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class Week4 {

    private static Random RANDOM = new Random();

    public static final void main(String[] args) throws IOException, URISyntaxException {
        Map<Integer, List<Integer>> graph = readGraph("week4/BigGraph.txt");

        int n = graph.size();
        //int N = (int) (n * n * Math.log(n));
        int N = n * n;
        int minCut = n;
        for (int i = 0; i < N; i++) {
            int currentMinCut = defineMinCut(graph);
            System.out.println(i + "/" + N + " currentMinCut: " + currentMinCut);
            if (currentMinCut < minCut) {
                minCut = currentMinCut;
            }
        }
        System.out.println("minCut: " + minCut);
    }

    private static int defineMinCut(Map<Integer, List<Integer>> graph) {
        //clone graph
        Map<Integer, List<Integer>> updatedGraph = graph.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue())));
        while (updatedGraph.size() > 2) {
            updatedGraph = runKargerIteration(updatedGraph);
        }
        return updatedGraph.entrySet().iterator().next().getValue().size();
    }

    private static Map<Integer, List<Integer>> runKargerIteration(Map<Integer, List<Integer>> graph) {
        Pair<Integer, Integer> remainingEdge = pickEdgeRandomly(graph);
        //System.out.println("edge: " + remainingEdge);
        return mergeVerticesOfEdge(graph, remainingEdge);
    }

    private static Pair<Integer, Integer> pickEdgeRandomly(Map<Integer, List<Integer>> graph) {
        int randomFirstNodeIndex = RANDOM.nextInt(graph.size());
        List<Integer> firstNodes = new ArrayList<>(graph.keySet());
        int randomFirstNode = firstNodes.get(randomFirstNodeIndex);
        List<Integer> edgesOfRandomNode = graph.get(randomFirstNode);
        int randomSecondNodeIndex = RANDOM.nextInt(edgesOfRandomNode.size());
        int randomSecondNode = edgesOfRandomNode.get(randomSecondNodeIndex);
        return Pair.of(randomFirstNode, randomSecondNode);
    }

    /** uncomment to test merge
    System.out.println(graph.size());
    System.out.println(graph.get(1));
    System.out.println(graph.get(2));
    System.out.println(graph.get(3));
    System.out.println(graph.get(4));
    mergeVerticesOfEdge(graph, Pair.of(1,2));
    System.out.println("");
    System.out.println("merging");
    System.out.println("");
    System.out.println(graph.size());
    System.out.println(graph.get(1));
    System.out.println(graph.get(2));
    System.out.println(graph.get(3));
    System.out.println(graph.get(4));
    mergeVerticesOfEdge(graph, Pair.of(1,3));
    System.out.println("");
    System.out.println("merging");
    System.out.println("");
    System.out.println(graph.size());
    System.out.println(graph.get(1));
    System.out.println(graph.get(2));
    System.out.println(graph.get(3));
    System.out.println(graph.get(4));
    */
    private static Map<Integer, List<Integer>> mergeVerticesOfEdge(Map<Integer, List<Integer>> graph,
                                                                   Pair<Integer, Integer> remainingEdge) {
        Integer firstNode = remainingEdge.fst;
        Integer secondNode = remainingEdge.snd;
        List<Integer> firstNodeEdges = graph.get(firstNode);
        List<Integer> secondNodeEdges = graph.get(secondNode);
        //remove remaining edge
        while (firstNodeEdges.contains(secondNode)) {
            firstNodeEdges.remove(secondNode);
        }
        while (secondNodeEdges.contains(firstNode)) {
            secondNodeEdges.remove(firstNode);
        }
        //remove second node
        graph.remove(secondNode);
        //merge edges
        firstNodeEdges.addAll(secondNodeEdges);
        //replace second node with first node in rest edges
        for (Integer nodeAdjacentToSecond: secondNodeEdges) {
            List<Integer> nodes = graph.get(nodeAdjacentToSecond);
            nodes.remove(secondNode);
            nodes.add(firstNode);
        }
        return graph;
    }

    private static Map<Integer, List<Integer>> readGraph(String fileName) throws IOException, URISyntaxException {
        Path path = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
        return Files.readAllLines(path)
                .stream()
                .map(s -> Arrays.stream(s.split("\t"))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toMap(list -> list.get(0),
                        list -> {
                            list.remove(0);
                            return list;
                        }));
    }
}
