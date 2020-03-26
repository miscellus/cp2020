package cp.week13;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    
    public static void main(String[] args) throws IOException
	{
		// word -> number of times that it appears over all files
		//Map< String, Integer > occurrences = new ConcurrentHashMap<>();
        Map< Character, Set<String>> wordSetsPerCharacter = new ConcurrentHashMap<>();
        
        final List<Thread> threads = new ArrayList<>();
        
		Files.walk(Path.of("data"))
            .filter(Files::isRegularFile)
            .filter(path -> path.toString().endsWith(".txt"))
			.forEach( path -> {
                    Thread t = new Thread( () -> {
                        computeOccurrences( path, wordSetsPerCharacter );
                    });
                    threads.add(t);
                    t.start();
            } );

		try {
            for (Thread t : threads) {
                t.join();
            }
		} catch( InterruptedException e ) {
			e.printStackTrace();
		}
		
        wordSetsPerCharacter.forEach( (startingChar, setOfWordsStartingWithStartingChar) -> System.out.println( startingChar + ": " + setOfWordsStartingWithStartingChar ) );
//		occurrences.forEach( (word, n) -> System.out.println( word + ": " + n ) );
	}
	
	private static void computeOccurrences( Path path, Map< Character, Set<String> > wordSetsPerCharacter )
	{
//        "this"
//        "is"
//        "words"
//        "this"
//        "is"
//        "words"
//        "this"
//        "is"
//        "words"
//        "this"
//        "is" 
//            "words"
        
		try {
			Files.lines( path )
				.flatMap( Words::extractWords )
				//.map( String::toLowerCase )
				.forEach( word -> {
                    char startingCharacter = word.charAt(0);
                    
                    if (true) {
                        Set<String> someSet = new HashSet<>();
                        someSet.add(word);
                        wordSetsPerCharacter.merge(startingCharacter, someSet, (existingSet, addedSet) -> {
                           if (existingSet == null) {
                               return addedSet;
                           }
                           else {
                               existingSet.addAll(addedSet);
                               return existingSet;
                           }
                        });
                    }
                    else {
                        wordSetsPerCharacter.compute(startingCharacter, (unsusedKey, existingSet) -> {
                            if (existingSet == null) {
                               Set<String> initialSet = new HashSet<>();
                               initialSet.add(word);
                               return initialSet;
                            }
                            else {
                                existingSet.add(word);
                                return existingSet;
                            }
                        });
                    }
				} );
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}
}
