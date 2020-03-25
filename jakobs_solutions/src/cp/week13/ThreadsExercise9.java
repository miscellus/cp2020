package cp.week13;

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

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise9
{
	/*
	Modify Threads/cp/ConcurrentMap to compute a map of type Map<Character, Set<String>>.
	The map should map a character to the set of words that start with that character (case sensitive).
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
		
		CountDownLatch latch = new CountDownLatch( filenames.size() );
		
		filenames.stream()
            .map( filename -> "files/" + filename )
			.map( filename -> new Thread( () -> {
				computeOccurrences( filename, globalWordSetsPerCharacter );
				latch.countDown();
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
	
	private static void computeOccurrences( String filename, Map<Character, Set<String>> globalWordSetsPerCharacter)
	{
        final Set<String> wordsAlreadySeenByThisThread = new HashSet<>();
		try {
			Files.lines( Paths.get( filename ) )
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
