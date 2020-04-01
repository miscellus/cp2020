package cp.week14;

import common.Words;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise11
{
	/*
	Modify ThreadsExercise9 to use executors.
	Try different kinds of executor (cached thread pool or fixed thread pool) and different fixed pool sizes.
	Which executor runs faster?
	Can you explain why?
	*/
    
    public static void main(String[] args) {
		// word -> number of times that it appears over all files
        
        Map< Character, Set<String> > globalWordSetsPerCharacter = new ConcurrentHashMap<>();		
		List< String > filenames = List.of(
			"text1.txt",
			"text2.txt",
			"text3.txt",
			"text4.txt",
			"text5.txt",
			"text6.txt",
			"text7.txt",
			"text8.txt",
			"text9.txt",
			"text10.txt"
		);
        
        ExecutorService executor = Executors.newCachedThreadPool();
        
		//CountDownLatch latch = new CountDownLatch( filenames.size() );
		
		filenames.stream()
            .map( filename -> "data/" + filename )
			.forEach( filename -> executor.submit( () -> {
				computeOccurrences( filename, globalWordSetsPerCharacter );
				//latch.countDown();
			} ) );
		try {
			executor.shutdown();
            executor.awaitTermination(1, TimeUnit.DAYS);
            //latch.await();
		} catch( InterruptedException e ) {
			e.printStackTrace();
		}
		
		globalWordSetsPerCharacter.forEach(
                (character, words) -> System.out.println( character + ": " + words ) );
	}
	
	private static void computeOccurrences( String filename, Map<Character, Set<String>> globalWordSetsPerCharacter)
	{
        final Set<String> wordsAlreadySeenByThisThread = new HashSet<>();
		try {
			Files.lines( Paths.get( filename ) )
				.flatMap( Words::extractWords )
				.forEach( (String word) -> {
                    // NOTE(jakob): It is not strictly nessecary for us to have
                    // this wordsAlreadySeenByThisThread set; we use it as an
                    // optimization because we know that if this thread has
                    // already encountered a word, then that word would have
                    // already been added to the global sets for all characters
                    // contained within that word.
                    if (!wordsAlreadySeenByThisThread.contains(word)) {
                        wordsAlreadySeenByThisThread.add(word);
                        
                        char startingCharacter = word.charAt(0);

                        // Since globalWordSetsPerCharacter
                        // is a concurrent hash map, we do not need to
                        // surround this statement with a synchronized
                        // block.
                        globalWordSetsPerCharacter.compute(
                                startingCharacter,
                                (unusedKey, theSet)-> {
                                    if (theSet == null) {
                                        theSet = new HashSet<>();
                                    }
                                    theSet.add(word);
                                    return theSet;
                                });
                    }
				} );
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}
}
