package cp.week12;

import common.Words;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise8
{
	/*
	- As ThreadExercise7, but now use a global counter among all threads instead.
	- Reason about the pros and cons of the two concurrency strategies
	  (write them down).
	*/
    
    public static void main(String[] args) {
		// word -> number of times that it appears over all files
		Map< String, Integer > occurrences = new HashMap<>();
		
		List< String > filenames = List.of(
			"data/text1.txt",
			"data/text2.txt",
			"data/text3.txt",
			"data/text4.txt",
			"data/text5.txt",
			"data/text6.txt",
			"data/text7.txt",
			"data/text8.txt",
			"data/text9.txt",
			"data/text10.txt"
		);
		
		CountDownLatch latch = new CountDownLatch( filenames.size() );
        
        // NOTE(jakob): So now we pass a reference to a global atomic integer,
        // globalLWordCount, used as a counter.
        // Try to remember what we learned about AtomicInteger when we
        // used it to make a counter previously
        // (CounterUsingAtomic in ThreadsExercise1 - week10)
        //
        // It is much more lightweight than using Java's implicit locks
        // associated with the synchronized keyword. On most modern hardware
        // platforms there are specific CPU instructions that facilitate these
        // kinds of read–modify–write atomic operations.
        final AtomicInteger globalLWordCount = new AtomicInteger(0);
        
        filenames.stream().forEach(filename -> {
            new Thread( () -> {
                computeOccurrences( filename, occurrences, globalLWordCount );
                
                // If we are the slowest of the threads, we will count the latch
                // down from 1 to 0, and the latch.await(); call on line 71 will
                // "wake up" the main thread which will then continue by
                // printing the results on line 77.
                latch.countDown();
            }).start();
        });

		try {
            // NOTE(jakob): We again use the CountDownLatch to wait for the last
            // thread to finish before we continue. The main thread will wait here.
			latch.await();
            // <- on this line, we know that all the threads are done.
		} catch( InterruptedException e ) {
			e.printStackTrace();
		}
        
        System.out.println("Number of words in total starting with the letter L: " + globalLWordCount.get());
		
//		occurrences.forEach( (word, n) -> System.out.println( word + ": " + n ) );
	}
	
	private static void computeOccurrences( String filename, Map< String, Integer > occurrences, AtomicInteger globalLWordCounter ) {
        
        try {
			Files.lines( Paths.get( filename ) )
				.flatMap( Words::extractWords )
                .forEach(s -> {
                    if (s.startsWith("L")) {
                        // We atomically increment the same global counter in
                        // all of the threads.
                        // Read about atomicity in the cource book if you are
                        // unclear about what it means.
                        globalLWordCounter.incrementAndGet();
                    }
                    
                    s = s.toLowerCase();
                    
                    synchronized( occurrences ) {
						occurrences.merge( s, 1, Integer::sum );
					}
                });
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}
}
