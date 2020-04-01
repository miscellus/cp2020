package cp.week14;

import common.Words;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise12
{
	/*
	Modify Threads/cp/WalkBlockingDeque such that only files ending with a ".txt" suffix are put in the tasks deque.
	*/
    
    public static void main(String[] args)
	{
		// word -> number of times it appears over all files
		Map< String, Integer > occurrences = new ConcurrentHashMap<>();
		
		int maxThreads = Runtime.getRuntime().availableProcessors();
		CountDownLatch latch = new CountDownLatch( maxThreads );
		final BlockingDeque< Optional< Path > > tasks = new LinkedBlockingDeque<>();

		IntStream.range( 0, maxThreads ).forEach( i -> {
			new Thread( () -> {
				try {
					Optional< Path > task;
					do {
						task = tasks.take();
						task.ifPresent( filepath -> computeOccurrences( filepath, occurrences ) );
					} while( task.isPresent() );
					tasks.add( task );
				} catch( InterruptedException e ) {}
				latch.countDown();
			} ).start();
		} );
				
		try {
			Files.walk( Paths.get( "data" ) )
				.filter( Files::isRegularFile )
                .filter( path -> path.toString().endsWith(".txt"))
				.forEach( path -> tasks.add( Optional.of( path ) ) );
		} catch( IOException e ) {
			e.printStackTrace();
		}
		
		tasks.add( Optional.empty() );
		
		try {
			latch.await();
		} catch( InterruptedException e ) {
			e.printStackTrace();
		}
		
		occurrences.forEach( (word, n) -> System.out.println( word + ": " + n ) );
	}
	
	private static void computeOccurrences( Path textFile, Map< String, Integer > occurrences )
	{
		try {
			Files.lines( textFile )
				.flatMap( Words::extractWords )
				.map( String::toLowerCase )
				.forEach( s -> occurrences.merge( s, 1, Integer::sum ) );
		} catch( IOException e ) {
			e.printStackTrace();
		}
	}
}
