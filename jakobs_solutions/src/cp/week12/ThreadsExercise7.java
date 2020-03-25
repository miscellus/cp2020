package cp.week12;

import common.Words;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise7
{
	/*
	- Modify Threads/cp/threads/SynchronizedMap such that:
		* Each threads also counts the total number of times that any word
		  starting with the letter "L" appears.
		* Each thread should have its own total (no shared global counter).
		* The sum of all totals is printed at the end.
	*/

	public static void main(String[] args) {
		// word -> number of times that it appears over all files
		Map< String, Integer > occurrences = new HashMap<>();
		
		List< String > filenames = List.of(
			"files/text1.txt",
			"files/text2.txt",
			"files/text3.txt",
			"files/text4.txt",
			"files/text5.txt",
			"files/text6.txt",
			"files/text7.txt",
			"files/text8.txt",
			"files/text9.txt",
			"files/text10.txt"
		);
		
		CountDownLatch latch = new CountDownLatch( filenames.size() );
        
        // NOTE(jakob): We the array of counts words starting with L final.
        // This makes it accessable from within the thread.
        // All lambda expressions require variables defined in outer scopes to
        // be final or effectively final (effective just meaning that no one
        // ever modifies the value, so it could just as well have been
        // declared final).
        final int lWordCounts[] = new int[filenames.size()];
        
        for (int i = 0; i < filenames.size(); ++i) {
            
            // For the same reason as above, these are declared final.
            final String filename = filenames.get(i);
            // Notice especially how we have to make a final copy of i.
            // Since i is incremented, it is not effectively final.
            final int threadIndex = i;
            
            new Thread( () -> {
                // We can still change stuff within the array since the array
                // reference itself stays unchanged.
				lWordCounts[threadIndex] = computeOccurrences( filename, occurrences );
				latch.countDown();
			}).start();
        }

		try {
			latch.await();
		} catch( InterruptedException e ) {
			e.printStackTrace();
		}
        
        System.out.println("Number of words in total starting with the letter L: " + IntStream.of(lWordCounts).sum());
		
//		occurrences.forEach( (word, n) -> System.out.println( word + ": " + n ) );
	}
	
	private static int computeOccurrences( String filename, Map< String, Integer > occurrences ) {
		
        // Here we use the same trick of having a final reference to an array.
        // This allows us to modify our count outside of the lambda expression
        // used in the stream forEach.
        // To reiterate, it is not that we need this variable to be an array,
        // in fact, we give it a size of 1 since we are really only doing it to
        // circumvent Java's rules for lambda expressions.
        final int lWordCount[] = new int[1];
        
        try {
			Files.lines( Paths.get( filename ) )
				.flatMap( Words::extractWords )
                .forEach(s -> {
                    if (s.startsWith("L")) {
                        ++lWordCount[0];
                    }
                    
                    s = s.toLowerCase();
                    
                    synchronized( occurrences ) {
						occurrences.merge( s, 1, Integer::sum );
					}
                });
		} catch( IOException e ) {
			e.printStackTrace();
		}
        
        return lWordCount[0];
	}
	
}
