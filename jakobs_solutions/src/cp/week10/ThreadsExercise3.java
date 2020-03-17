package cp.week10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise3
{
	/*
	Modify the threads/cp/SynchronizedMap2T example such that:
	- There are now two maps (instead of just one) for accumulating results, one for each thread.
	- Each thread uses only its own map, without synchronizing on it.
	- After the threads terminate, create a new map where you merge the results of the two dedicated maps.
	
	Questions:
	- Does the resulting code work? Can you explain why?
	- Does the resulting code perform better or worse than the original example SynchronizedMap2T?
	- Can you hypothesise why?
	*/
    
    public static void main(String[] args)
	{
		List<Path> paths = new ArrayList<>();
		paths.add( Paths.get( "files/text1.txt" ) );
		paths.add( Paths.get( "files/text2.txt" ) );
		paths.add( Paths.get( "files/text3.txt" ) );
		paths.add( Paths.get( "files/text4.txt" ) );
		
		// word -> number of times it appears
		final List<Map<String, Integer>> occurrenceMaps = new ArrayList<>(paths.size());
        for (int i = 0; i < paths.size(); ++i) {
            occurrenceMaps.add(new HashMap<>());
        }
        
		List<Thread> workers = new ArrayList<>();
		for( int i = 0; i < paths.size(); ++i ) {
			final Path path = paths.get(i);
            final Map<String, Integer> workerOccurrences = occurrenceMaps.get(i);
            workers.add( new Thread( () -> {
				computeOccurrences( path, workerOccurrences );
			}));
		}
		
		for( Thread t : workers ) {
			t.start();
		}
		
		for( Thread t : workers ) {
			try {
				t.join();
			} catch( InterruptedException e ) {
				e.printStackTrace();
			}
		}
        
        Map<String, Integer> occurrences = occurrenceMaps.stream().reduce(new HashMap<String, Integer>(), ThreadsExercise3::mergeAll);
        
        occurrences.forEach((word, count) -> System.out.println(word + ": " + count));	
    }
    
    private static Map<String, Integer> mergeAll(Map<String,Integer> acc, Map<String,Integer> next) {
        next.forEach((key, value) -> acc.merge(key, value, Integer::sum));
        return acc;
    }
	
	private static void computeOccurrences( Path path, Map<String, Integer> occurrences )
	{
		try {
			String[] lines = Files.lines( path ).toArray( String[]::new );
			List<String> words = new ArrayList<>();
			for( String line : lines ) {
				for( String word : line.split( " " ) ) {
					words.add( word );
				}
			}
			for( String word : words ) {
				//synchronized( occurrences ) {
                if ( occurrences.containsKey( word ) ) {
                    occurrences.put( word, occurrences.get( word ) + 1 );
                } else {
                    occurrences.put( word, 1 );
                }
				//}
			}
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}
}
