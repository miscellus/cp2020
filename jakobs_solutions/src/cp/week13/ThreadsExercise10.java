package cp.week13;

import common.Words;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise10
{
	/*
	Modify ThreadsExercise9 to use Files.walk over the data directory in the Threads project, such
	that you recursively visit all files in that directory instead of using a fixed list of filenames.
	*/
    
    public static void main(String[] args) throws IOException {
		// word -> number of times that it appears over all files
        
        Map< Character, Set<String> > globalWordSetsPerCharacter = new ConcurrentHashMap<>();
        
        // NOTE(jakob): The CountDownLatch no longer keeps track of the
        // amount of threads left, it instead is a blocking barrier
        // that the main thread will wait for to open, which happens
        // once our AtomicInteger (which is our actual
        // thread counter) reaches zero.
		CountDownLatch latch = new CountDownLatch( 1 ); // Wait and see (^:
        AtomicInteger activeThreadCount = new AtomicInteger(0);
        
		Files.walk(Path.of("files/"))
            .filter(Files::isRegularFile)
			.map( path -> new Thread( () -> {
                activeThreadCount.incrementAndGet();
				computeOccurrences( path, globalWordSetsPerCharacter );
				
                // Now this is a bit tricky.
                // It is in essence a check-then-act race condition.
                // The thread safety of this relies on the fact that only the
                // last thread to finish will get a return value of 0 from the
                // decrementAndGet method on the AtomicInt (Since it is atomic).
                if (activeThreadCount.decrementAndGet() == 0) {
                    latch.countDown();
                }
			} ) )
			.forEach( Thread::start );

		try {
			latch.await();
		} catch( InterruptedException e ) {
			e.printStackTrace();
		}
		
		globalWordSetsPerCharacter.forEach(
                (character, words) -> System.out.println( character + ": " + words ) );
	}
	
	private static void computeOccurrences( Path textFile, Map<Character, Set<String>> globalWordSetsPerCharacter)
	{
        final Set<String> wordsAlreadySeenByThisThread = new HashSet<>();
		try {
			Files.lines( textFile )
				.flatMap( Words::extractWords )
				.map( String::toLowerCase )
				.forEach( (String word) -> {
                    // NOTE(jakob): It is not strictly nessecary for us to have
                    // this wordsAlreadySeenByThisThread set; we use it as an
                    // optimization because we know that if this thread has
                    // already encountered a word, then that word would have
                    // already been added to the global sets for all characters
                    // contained within that word.
                    if (!wordsAlreadySeenByThisThread.contains(word)) {
                        wordsAlreadySeenByThisThread.add(word);
                        
                        // Sometimes a good old for loop is all we need.
                        for (int i = 0; i < word.length(); ++i) {
                            char character = word.charAt(i);
                            
                            // Since globalWordSetsPerCharacter
                            // is a concurrent hash map, we do not need to
                            // surround this statement with a synchronized
                            // block.
                            globalWordSetsPerCharacter.compute(
                                    character,
                                    (unusedKey, theSet)-> {
                                        if (theSet == null) {
                                            theSet = new HashSet<>();
                                        }
                                        theSet.add(word);
                                        return theSet;
                                    });
                        }
                    }
				} );
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}
}
