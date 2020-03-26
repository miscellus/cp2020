package cp.week11;

import cp.end_of_slides2.ExercisesFromSlides.Pair;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise6
{
	/*
	This exercise generalises Threads/cp/SharedMap2T.
	Feel free to borrow the appropriate pieces of code from that example.
	
	Implement a method 
		public static Map< String, Integer > computeOccurrences( Stream< String > filenames )
	that returns a map of how many times each word occurs (as in SharedMap2T) in the files named
	in the stream parameter.
	
	Try first to implement the method sequentially (no threads), then try
	to implement it such that each file is processed by a dedicated thread (all threads
	should run concurrently and be waited for).
	*/

    public static void main(String[] args)
    {
        
        // The text files still exist in the week10 directory
        final String[] filanameArray = new String[] {
                "data/text1.txt",
                "data/text2.txt",
                "data/text3.txt",
                "data/text4.txt"};
        
        Stream<String> filenames1 = Stream.of(filanameArray);
        Stream<String> filenames2 = Stream.of(filanameArray);
        
        Map< String, Integer > resultsSequential = computeOccurrencesSequential(filenames1);
        Map< String, Integer > resultsConcurrent = computeOccurrencesConcurrent(filenames2);
        
        
//        resultsConcurrent.forEach((word, count) ->
//                System.out.println("Word: " + word + " Count: " + count));

        System.out.println("It works!: " + resultsSequential.equals(resultsConcurrent));
    }
    
    public static Map< String, Integer > computeOccurrencesSequential( Stream< String > filenames ) {
        
        final Map< String, Integer > results = new HashMap<>();
        
        filenames.map(s -> Path.of(s))
                .forEach((path) -> {
                    try {
                        Files.lines(path)
                                // First we turn lines to words.
                                // We use flatMap to turn our stream
                                // of lines into a stream of words
                                .flatMap(line -> Stream.of(line.split(" ")))
                                .forEach( word -> {
                                    results.merge( word, 1, Integer::sum );
                                });
                    } catch (IOException ex) {
                        System.err.println(ex);
                    }
                });
        
        return results;
    }
    
    public static Map< String, Integer > computeOccurrencesConcurrent( Stream< String > filenames ) {
        
        // NOTE(jakob):
        // I tried to stick with what we should know by now.
        // I reused the Pair<K,V> class, that we implemented during the first
        // exercise class, to get a stream of two things at the same time,
        // the key being used for the result occurrence map, and the value being
        // used for the Thread owning that map.
        //
        // We will learn some stuff later like parallel streams
        // and Executors that would have made this implementation a lot less yuck.
        //
        // Feel free to try and learn these things on your own and improve this.
     
        
        final Map< String, Integer > results = filenames.map(Paths::get)
                .map(ThreadsExercise6::pathToMapAndThread)
                .peek(pair -> pair.value.start())
                .peek(pair -> {
                    try {
                        pair.value.join();
                    } catch (InterruptedException ex) {}
                })
                .map(pair -> pair.key)
                .reduce(new HashMap<>(), ThreadsExercise6::mergeAll);
        
        return results;
    }
    
    private static Map<String, Integer> mergeAll(Map<String,Integer> acc, Map<String,Integer> next) {
        next.forEach((key, value) -> acc.merge(key, value, Integer::sum));
        return acc;
    }
    
    private static Pair<Map<String, Integer>, Thread> pathToMapAndThread(Path path) {
        final Map<String, Integer> localResult = new HashMap<>();
        
        final Thread thread = new Thread(() -> {
            try {
                Files.lines(path)
                        // First we turn lines to words.
                        // We use flatMap to turn our stream
                        // of lines into a stream of words
                        .flatMap(line -> Stream.of(line.split(" ")))
                        .forEach( word -> {
                            localResult.merge( word, 1, Integer::sum );
                        });
            } catch (IOException ex) {
                System.err.println(ex);
            }
        });
        
        return new Pair<>(localResult, thread);
    }
}
