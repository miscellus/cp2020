package cp.week12;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

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
    
    public static void main(String[] args)
	{
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
		
        AtomicCounter globalLWordCount = new AtomicCounter(0);
                
        for (int i = 0; i < filenames.size(); ++i) {
            String filename = filenames.get(i);
            Thread t = new Thread( () -> {
                computeOccurrences( filename, occurrences, globalLWordCount);
                latch.countDown();
            });
            t.start();
        }

		try {
			latch.await();
		} catch( InterruptedException e ) {
			e.printStackTrace();
		}
		
		//occurrences.forEach( (word, n) -> System.out.println( word + ": " + n ) );
        
        //System.out.println("Words beginning with the letter L: " + IntStream.of(counters).sum() );
        System.out.println("Words beginning with the letter L: " + globalLWordCount.get() );
	}
    
	private static void computeOccurrences( String filename, Map< String, Integer > occurrences, AtomicCounter globalLWordCounter )
	{
        
        try {
			Files.lines( Paths.get( filename ) )
				.flatMap( Words::extractWords )
				//.map( String::toLowerCase )
				.forEach( s -> {
                    //if (s.startsWith("L"))
                    if (s.charAt(0) == 'L') {
                       // Increment our global counter here
                       globalLWordCounter.increment();
                    }
                    // Farts == farts
					synchronized( occurrences ) {
						occurrences.merge( s.toLowerCase(), 1, Integer::sum );
					}
				} );
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}
    
    public static class UnsafeCounter {
        private int i;
        
        public UnsafeCounter(int initialValue) {i = initialValue;}
        
        public void increment() {
            ++i;
        }
        
        public void decrement() {
            --i;
        }
        
        public int get() {
            return i;
        }
    }
    
    public static class AtomicCounter {
        private AtomicInteger i;
        
        public AtomicCounter(int i) {
            this.i = new AtomicInteger(i);
        }
        
        public void increment() {
            i.incrementAndGet();
        }
        
        public void decrement() {
            i.decrementAndGet();
        }
        
        public int get() {
            return i.get();
        }
    }
}
