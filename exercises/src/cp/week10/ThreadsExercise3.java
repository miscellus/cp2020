package cp.week10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise3
{
	/*
	Modify the threads/cp/SynchronizedMap2T example such that:
	- There are now two maps (instead of just one) for accumulating results, one for each thread.
	- Each thread uses only its own map, without synchronizing on it.
	- After the threads terminate, create a new map where you merge the results of the two dedicated maps.
	
	Questions:
	- Does the resulting code work? Can you explain why?
	- Does the resulting code perform better or worse than the original example SynchronizedMap2T?
	- Can you hypothesise why?
	*/
    
    public static void main(String[] args) {
        
        Map< String, Integer > resultsSerial = new HashMap<>();
        
        {
            Thread t1 = new Thread( () -> {
                try {
                    Files.lines( Paths.get( "week10/text1.txt" ) )
                        .flatMap( s -> Stream.of( s.split( " " ) ) )
                        .forEach( word -> {
                            synchronized( resultsSerial ) {
                                resultsSerial.merge( word, 1, Integer::sum );
                            }
                        } );
                } catch( IOException e ) {
                    e.printStackTrace();
                }
            });

            Thread t2 = new Thread( () -> {
                try {
                    Files.lines( Paths.get( "week10/text2.txt" ) )
                        .flatMap( s -> Stream.of( s.split( " " ) ) )
                        .forEach( word -> {
                            synchronized( resultsSerial ) {
                                resultsSerial.merge( word, 1, Integer::sum );
                            }
                        } );
                } catch( IOException e ) {
                    e.printStackTrace();
                }
            });
            
            t1.start();
            t2.start();
            try {
                t1.join();
                t2.join();
            } catch( InterruptedException e ) {
                e.printStackTrace();
            }
        }
        
		Map< String, Integer > results1 = new HashMap<>();
        Map< String, Integer > results2 = new HashMap<>();
		
		Thread t1 = new Thread( () -> {
			try {
				Files.lines( Paths.get( "week10/text1.txt" ) )
					.flatMap( s -> Stream.of( s.split( " " ) ) )
					.forEach( word -> {
						//synchronized( results1 ) {
							results1.merge( word, 1, Integer::sum );
						//}
					} );
			} catch( IOException e ) {
				e.printStackTrace();
			}
		});
		
		Thread t2 = new Thread( () -> {
			try {
				Files.lines( Paths.get( "week10/text2.txt" ) )
					.flatMap( s -> Stream.of( s.split( " " ) ) )
					.forEach( word -> {
						//synchronized( results2 ) {
							results2.merge( word, 1, Integer::sum );
						//}
					} );
			} catch( IOException e ) {
				e.printStackTrace();
			}
		});
		
		t1.start();
		t2.start();
		try {
			t1.join();
			t2.join();
		} catch( InterruptedException e ) {
			e.printStackTrace();
		}
        
        
        
        results1.forEach((word, count) -> {
            results2.merge(word, count, Integer::sum);
        });
        
        results2.forEach((word, count) -> System.out.println("Word: " + word + " Count: " + count));
        
        System.out.println("It works!: " + results2.equals(resultsSerial));
        
	}
}
