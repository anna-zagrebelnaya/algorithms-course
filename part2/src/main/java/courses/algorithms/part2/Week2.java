package courses.algorithms.part2;

import com.sun.tools.javac.util.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

//TODO: OPTIONAL: For those of you seeking an additional challenge, try implementing the heap-based version.
//  Note this requires a heap that supports deletions, and you'll probably need to maintain some kind of
//  mapping between vertices and their positions in the heap.
public class Week2 {

    private static final int SOURCE_NODE = 1;
    private static final List<Integer> VERTICES_INCLUDED_IN_RESULT_SMALL = asList(1,3);
    private static final List<Integer> VERTICES_INCLUDED_IN_RESULT = asList(7,37,59,82,99,115,133,165,188,197);

    public static class SmallExample {
        public static void main(String[] args) throws IOException, URISyntaxException {
            Map<Integer, Map<Integer, Integer>> graph = readUndirectedWeightedGraph("week2/SmallUndirectedWeightedGraph.txt");
            runAlgorithm(graph, VERTICES_INCLUDED_IN_RESULT_SMALL);
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        Map<Integer, Map<Integer, Integer>> graph = readUndirectedWeightedGraph("week2/BigUndirectedWeightedGraph.txt");
        runAlgorithm(graph, VERTICES_INCLUDED_IN_RESULT);
    }

    public static void runAlgorithm(Map<Integer, Map<Integer, Integer>> graph, List<Integer> verticesIncludedInResult) {
        Map<Integer,Integer> shortestPaths = dijkstra(SOURCE_NODE, graph);
        List<Integer> result = verticesIncludedInResult.stream()
                .map(shortestPaths::get)
                .collect(Collectors.toList());
        System.out.println(result);
    }

    private static Map<Integer, Integer> dijkstra(int source, Map<Integer, Map<Integer, Integer>> graph) {
        Set<Integer> unexploredNodes = new HashSet<>(graph.keySet());
        unexploredNodes.remove(source);
        Set<Integer> exploredNodes = new HashSet<>();
        exploredNodes.add(source);

        Map<Integer, Integer> scores = new HashMap<>();
        scores.put(source, 0);

        while (!unexploredNodes.isEmpty()) {
            Integer minScore = null;
            Integer minNode = null;
            //define edges (i,j) where i is explored, j is not explored
            for (int i: exploredNodes) {
                Integer iScore = scores.get(i);
                for (Map.Entry<Integer, Integer> jAndWeight: graph.get(i).entrySet()) {
                    if (!unexploredNodes.contains(jAndWeight.getKey())) {
                        continue;
                    }
                    int currentScore = iScore + jAndWeight.getValue();
                    //pick one that minimizes scores[i] + w
                    if (minScore == null || currentScore < minScore) {
                        minScore = currentScore;
                        minNode = jAndWeight.getKey();
                    }
                }
            }
            //add it to explored, update scores
            exploredNodes.add(minNode);
            unexploredNodes.remove(minNode);
            scores.put(minNode, minScore);
        }
        return scores;
    }

    private static Map<Integer, Map<Integer, Integer>> readUndirectedWeightedGraph(String fileName) throws IOException, URISyntaxException {
        Path path = Paths.get(ClassLoader.getSystemResource(fileName).toURI());
        return Files.readAllLines(path)
                .stream()
                .map(s -> Arrays.stream(s.split("\t"))
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toMap(list -> Integer.parseInt(list.get(0)),
                        list -> list.stream()
                                .filter(s -> s.contains(","))
                                .map(s -> {
                                    String[] nodeAndWeight = s.split(",");
                                    return Pair.of(
                                            Integer.parseInt(nodeAndWeight[0]),
                                            Integer.parseInt(nodeAndWeight[1])
                                            );
                                })
                        .collect(Collectors.toMap(pair -> pair.fst, pair -> pair.snd))
                ));
    }
}
