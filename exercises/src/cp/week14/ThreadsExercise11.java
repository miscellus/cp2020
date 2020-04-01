package cp.week14;

import cp.week13.Words;
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
		
        
        long t1, t2;
        t1 = System.currentTimeMillis();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        t2 = System.currentTimeMillis();
        System.out.println( "Time to initialize executorService: " + (t2-t1) + "ms" );	
        
		filenames.stream()
            .map( filename -> "data/" + filename )
            .forEach(filename -> {
                executor.submit(() -> {
                    computeOccurrences( filename, wordSetsPerCharacter );
                });
            });
        
		try {
			//executor.shutdownNow();
            t1 = System.currentTimeMillis();
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.HOURS);
            t2 = System.currentTimeMillis();
		} catch( InterruptedException e ) {
			e.printStackTrace();
            t1 = 0;
            t2 = 0;
		}
		
        //wordSetsPerCharacter.forEach( (startingChar, setOfWordsStartingWithStartingChar) -> System.out.println( startingChar + ": " + setOfWordsStartingWithStartingChar ) );
        System.out.println( "Elapsed time: " + (t2-t1) + "ms" );	
//		occurrences.forEach( (word, n) -> System.out.println( word + ": " + n ) );
	}
	
	private static void computeOccurrences( String filename, Map< Character, Set<String> > wordSetsPerCharacter )
	{

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
