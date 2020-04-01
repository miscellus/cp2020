package cp.week14;

import common.Words;
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
		
        AtomicBoolean nullaSeen = new AtomicBoolean(false);
        
		try {
			Files.walk( Paths.get( "data" ) )
				.filter( Files::isRegularFile )
				.forEach( filepath -> {
					executor.submit( () -> {
						computeOccurrences( filepath, occurrences, nullaSeen );
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
	
	private static void computeOccurrences( Path textFile, Map< String, Integer > occurrences, AtomicBoolean nullaSeen )
	{
		try {
			Files.lines( textFile )
				.flatMap( Words::extractWords )
				.map( String::toLowerCase )
                .takeWhile( s -> {
                    boolean isNulla = s.equals("nulla");
                    // NOTE(jakob): We use compareAndSet because we only
                    // want to replace false with true in `nullaSeen'.
                    // compareAndSet returns true if the value of the
                    // AtomicBoolean is the same as the first argument passed
                    // (called the expected value)
                    return nullaSeen.compareAndSet(false, isNulla) && !isNulla;
                    
                    // NOTE(jakob): This is wrong:
                    //
                    //     return !isNulla && nullaSeen.compareAndSet(false, isNulla);
                    //
                    // We have to be careful with the order of
                    // operands in logical expressions. Java uses something called
                    // short-circuit evaluation which (in part) means that when evaluating
                    // a boolean AND, if the first condition is false,
                    // then java won't even evaluate the remaining conditions
                    // (since false AND <anything> is always false).
                    // And since in this case our compareAndSet has the side-effect
                    // of writing the value of isNulla to the AtomicBoolean, this
                    // will not happen once the !isNulla has been evaluated to false.
                    // Hence, our shared AtomicBoolean, nullaSeen, will never be
                    // set to true.
                    // Look up 'short-curcuit evaluation' to learn more.
                })
				.forEach( s -> {
                    occurrences.merge( s, 1, Integer::sum ); 
                });
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}
}
