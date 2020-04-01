package cp.week14;

import cp.week13.Words;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise13
{
	/*
	Modify Threads/cp/WalkExecutor such that all threads stop processing as soon as the word "nulla" is found.
	Hint: Use an atomic boolean shared by all threads. Try not to make it a static field, but rather a local variable within the main method.
	*/
    public static void main(String[] args)
	{
		// word -> number of times it appears over all files
		Map< String, Integer > occurrences = new ConcurrentHashMap<>();
//		ExecutorService executor = Executors.newCachedThreadPool();
		ExecutorService executor = Executors.newFixedThreadPool( Runtime.getRuntime().availableProcessors() );
		
        AtomicBoolean seenNulla = new AtomicBoolean(false);
        
		try {
			Files.walk( Paths.get( "data" ) )
				.filter( Files::isRegularFile )
				.forEach( filepath -> {
					executor.submit( () -> {
						computeOccurrences( filepath, occurrences, seenNulla, executor );
					} );
				} );
		} catch( IOException e ) {
			e.printStackTrace();
		}
		
		try {
			executor.shutdown();
			executor.awaitTermination( 1, TimeUnit.DAYS );
		} catch( InterruptedException e ) {
			e.printStackTrace();
		}
		
		occurrences.forEach( (word, n) -> System.out.println( word + ": " + n ) );
	}
	
	private static void computeOccurrences( Path textFile, Map< String, Integer > occurrences, AtomicBoolean seenNulla, ExecutorService executor )
	{
		try {
			Files.lines( textFile )
				.flatMap( Words::extractWords )
				.map( String::toLowerCase )
                .takeWhile( word -> {
                    boolean isCurrentWordNulla = word.equals("nulla");                    
                    boolean notFoundAlready = seenNulla.compareAndSet(false, isCurrentWordNulla);
                    /*
                    // The line,
                    //     boolean notFoundAlready = seenNulla.compareAndSet(false, isCurrentWordNulla);
                    // corresponds to the following code:
                    
                    boolean notFoundAlready;
                    synchronized (seenNulla) {
                        boolean oldValue = seenNulla.get();
                        if (!oldValue) {
                            seenNulla.set(isCurrentWordNulla);
                        }
                        notFoundAlready = (oldValue == false);
                    }
                    //*/
                    
                    return  !isCurrentWordNulla && notFoundAlready;

                })
				.forEach( s -> {
                    /*if (seenNulla.get() || s.equals("nulla")) {
                        if(!seenNulla.get()){
                            seenNulla.set(true);
                            occurrences.merge( s, 1, Integer::sum );
                        }
                        executor.shutdownNow();
                    }
                    else {*/
                    occurrences.merge( s, 1, Integer::sum );
                    //}
                });
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}
}
