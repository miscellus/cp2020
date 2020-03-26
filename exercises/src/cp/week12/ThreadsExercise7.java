package cp.week12;

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
public class ThreadsExercise7
{
	/*
	- Modify Threads/cp/threads/SynchronizedMap such that:
		* Each threads also counts the total number of times that any word
		  starting with the letter "L" appears.
		* Each thread should have its own total (no shared global counter).
		* The sum of all totals is printed at the end.
	*/
    public static void main(String[] args)
	{
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
		
        AtomicInteger globalLWordCount = new AtomicInteger(0);
        
        int[] counters = new int[filenames.size()];
        
        for (int i = 0; i < filenames.size(); ++i) {
            String filename = filenames.get(i);
            final int counterIndex = i;
            Thread t = new Thread( () -> {
                int localLCount = computeOccurrences( filename, occurrences );
                globalLWordCount.addAndGet(localLCount);
                //counters[counterIndex] = localLCount;
                
                latch.countDown();
            });
            t.start();
        }

		try {
			latch.await();
		} catch( InterruptedException e ) {
			e.printStackTrace();
		}
		
		occurrences.forEach( (word, n) -> System.out.println( word + ": " + n ) );
        
        //System.out.println("Words beginning with the letter L: " + IntStream.of(counters).sum() );
        System.out.println("Words beginning with the letter L: " + globalLWordCount.get() );
	}
    
	private static int computeOccurrences( String filename, Map< String, Integer > occurrences )
	{
        //final int numWordsStartingWithL[] = new int[1];
		final Counter lWordsCounter = new Counter(0);
        
        try {
			Files.lines( Paths.get( filename ) )
				.flatMap( Words::extractWords )
				//.map( String::toLowerCase )
				.forEach( s -> {
                    //if (s.startsWith("L"))
                    if (s.charAt(0) == 'L') {
                       //++numWordsStartingWithL[0];
                        lWordsCounter.increment();
                    }
                    // Farts == farts
					synchronized( occurrences ) {
						occurrences.merge( s.toLowerCase(), 1, Integer::sum );
					}
				} );
		} catch( IOException e ) {
			e.printStackTrace();
		}
        
        return lWordsCounter.get();
	}
    
    public static class Counter {
        private int i;
        
        public Counter(int initialValue) {i = initialValue;}
        
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
