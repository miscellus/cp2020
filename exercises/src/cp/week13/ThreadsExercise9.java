package cp.week13;

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
    
    public static void main(String[] args)
	{
		// word -> number of times that it appears over all files
		//Map< String, Integer > occurrences = new ConcurrentHashMap<>();
        Map< Character, Set<String>> wordSetsPerCharacter = new ConcurrentHashMap<>();
		
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
				computeOccurrences( filename, wordSetsPerCharacter );
				latch.countDown();
			} ) )
			.forEach( Thread::start );

		try {
			latch.await();
		} catch( InterruptedException e ) {
			e.printStackTrace();
		}
		
        wordSetsPerCharacter.forEach( (startingChar, setOfWordsStartingWithStartingChar) -> System.out.println( startingChar + ": " + setOfWordsStartingWithStartingChar ) );
//		occurrences.forEach( (word, n) -> System.out.println( word + ": " + n ) );
	}
	
	private static void computeOccurrences( String filename, Map< Character, Set<String> > wordSetsPerCharacter )
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
			Files.lines( Paths.get( filename ) )
				.flatMap( Words::extractWords )
				//.map( String::toLowerCase )
				.forEach( word -> {
                    char startingCharacter = word.charAt(0);
                    
                    // This is wrong!
//                    Set<String> initialSet = new HashSet<>();
//                    initialSet.add(word);
//                    
//                    Set<String> existingSet = wordSetsPerCharacter.getOrDefault(startingCharacter, initialSet);
//                    
                    
                    
                    if (false) {
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
