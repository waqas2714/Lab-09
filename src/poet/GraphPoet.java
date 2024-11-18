package poet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import graph.ConcreteGraph;
import graph.Graph;

/**
 * A graph-based poetry generator.
 * 
 * <p>GraphPoet is initialized with a corpus of text, which it uses to derive a
 * word affinity graph.
 * Vertices in the graph are words. Words are defined as non-empty
 * case-insensitive strings of non-space non-newline characters. They are
 * delimited in the corpus by spaces, newlines, or the ends of the file.
 * Edges in the graph count adjacencies: the number of times "w1" is followed by
 * "w2" in the corpus is the weight of the edge from w1 to w2.
 * 
 * <p>For example, given this corpus:
 * <pre>    Hello, HELLO, hello, goodbye!    </pre>
 * <p>the graph would contain two edges:
 * <ul><li> ("hello,") -> ("hello,")   with weight 2
 *     <li> ("hello,") -> ("goodbye!") with weight 1 </ul>
 * <p>where the vertices represent case-insensitive {@code "hello,"} and
 * {@code "goodbye!"}.
 * 
 * <p>Given an input string, GraphPoet generates a poem by attempting to
 * insert a bridge word between every adjacent pair of words in the input.
 * The bridge word between input words "w1" and "w2" will be some "b" such that
 * w1 -> b -> w2 is a two-edge-long path with maximum-weight weight among all
 * the two-edge-long paths from w1 to w2 in the affinity graph.
 * If there are no such paths, no bridge word is inserted.
 * In the output poem, input words retain their original case, while bridge
 * words are lower case. The whitespace between every word in the poem is a
 * single space.
 * 
 * <p>For example, given this corpus:
 * <pre>    This is a test of the Mugar Omni Theater sound system.    </pre>
 * <p>on this input:
 * <pre>    Test the system.    </pre>
 * <p>the output poem would be:
 * <pre>    Test of the system.    </pre>
 * 
 * <p>PS2 instructions: this is a required ADT class, and you MUST NOT weaken
 * the required specifications. However, you MAY strengthen the specifications
 * and you MAY add additional methods.
 * You MUST use Graph in your rep, but otherwise the implementation of this
 * class is up to you.
 */
public class GraphPoet {

    private final Graph<String> wordGraph = new ConcreteGraph<>();  // The word affinity graph

    // Abstraction function:
    //   - Represents a word affinity graph where vertices are words, and edges are weighted by adjacency frequency.
    // Representation invariant:
    //   - Graph vertices represent case-insensitive words extracted from the corpus.
    //   - Edge weights are non-negative integers, representing the frequency of word adjacency.
    // Safety from rep exposure:
    //   - The graph is encapsulated and not directly exposed outside of the class.
    //   - The class does not expose any mutable references to internal state, ensuring safe data encapsulation.

    /**
     * Create a new poet with the graph constructed from the given corpus text file.
     * This method processes the corpus to build a graph where vertices are words, 
     * and edges represent word adjacency with weighted counts.
     *
     * @param corpus text file from which to derive the poet's affinity graph
     * @throws IOException if the corpus file cannot be found or read
     */
    public GraphPoet(File corpus) throws IOException {
        List<String> linesFromCorpus = Files.readAllLines(corpus.toPath());
        StringBuilder fullText = new StringBuilder();
        
        // Append each line to the content string
        for (String line : linesFromCorpus) {
            fullText.append(line).append(" ");
        }

        // Split the content into words and build the word graph
        String[] wordsInCorpus = fullText.toString().toLowerCase().split("\\s+");
        for (int i = 0; i < wordsInCorpus.length - 1; i++) {
            String firstWord = wordsInCorpus[i];
            String secondWord = wordsInCorpus[i + 1];
            // Set the adjacency count for the edge from firstWord to secondWord
            int currentWeight = wordGraph.set(firstWord, secondWord, wordGraph.targets(firstWord).getOrDefault(secondWord, 0) + 1);
        }

        verifyRep();  // Ensure that the representation invariant holds
    }

    /**
     * Check the representation invariant to ensure that all graph edges have non-negative weights.
     */
    private void verifyRep() {
        // Check that all edge weights are non-negative
        for (String vertex : wordGraph.vertices()) {
            for (int edgeWeight : wordGraph.targets(vertex).values()) {
                assert edgeWeight >= 0 : "Edge weight must be non-negative";
            }
        }
    }

    /**
     * Generate a poem by inserting bridge words between adjacent words in the input string.
     * The bridge word is chosen such that the path from firstWord to bridge to secondWord has the maximum weight.
     * 
     * @param input string from which to create the poem
     * @return poem (with bridge words inserted as described)
     */
    public String generatePoem(String input) {
        String[] wordsInInput = input.split("\\s+");
        StringBuilder poemResult = new StringBuilder();

        // Iterate through the input words and insert bridge words where possible
        for (int i = 0; i < wordsInInput.length - 1; i++) {
            String firstWord = wordsInInput[i].toLowerCase();  // Lowercase firstWord for graph consistency
            String secondWord = wordsInInput[i + 1].toLowerCase();  // Lowercase secondWord for graph consistency

            // Find the best bridge word
            String bridgeWord = null;
            int highestWeight = 0;

            // Iterate through the possible target words for firstWord
            for (Map.Entry<String, Integer> adjacent : wordGraph.targets(firstWord).entrySet()) {
                String possibleBridge = adjacent.getKey();
                // Calculate the combined weight for a possible bridge
                int bridgeWeight = adjacent.getValue() + wordGraph.targets(possibleBridge).getOrDefault(secondWord, 0);
                
                // If a path exists and it has the highest weight so far, choose this bridge
                if (wordGraph.targets(possibleBridge).containsKey(secondWord) && bridgeWeight > highestWeight) {
                    bridgeWord = possibleBridge;
                    highestWeight = bridgeWeight;
                }
            }

            // Append the first word and optionally the bridge word
            poemResult.append(wordsInInput[i]).append(" ");
            if (bridgeWord != null) {
                poemResult.append(bridgeWord).append(" ");
            }
        }

        // Append the last word in the input
        poemResult.append(wordsInInput[wordsInInput.length - 1]);
        return poemResult.toString();  // Return the generated poem
    }

    /**
     * Returns a string representation of the GraphPoet, including details of the underlying graph.
     * 
     * @return string representation of this GraphPoet
     */
    @Override
    public String toString() {
        return "GraphPoet with graph: " + wordGraph.toString();  // Return the string form of the graph
    }
}
