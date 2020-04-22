package cp.week17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise14_alt
{
	/*
	Modify Threads/cp/WalkExecutorFuture such that, instead of word occurrences,
	it computes a map of type Map< Path, FileInfo >, which maps the Path of each file found in "data"
	to an object of type FileInfo that contains:
		- the size of the file;
		- the number of lines contained in the file;
		- the number of lines starting with the uppercase letter "L".
	*/
    
    // NOTE(jakob): This is the alternative implementation of using a
    // Map.Entry as return value from computeFileInfo instead of a custom
    // wrapper class.
    
    public static void main(String[] args) {

        Map< Path, FileInfo > infoForFiles = new HashMap<>();
		ExecutorService executor = Executors.newWorkStealingPool();

		try {
			List< Future< Map.Entry<Path, FileInfo> > > futures =
				Files.walk( Paths.get( "data" ) )
					.filter( Files::isRegularFile )
					.map( filepath ->
						executor.submit( () -> computeFileInfo( filepath ) )
					)
					.collect( Collectors.toList() );

			for( Future< HashMap.Entry<Path, FileInfo> > future : futures ) {
				HashMap.Entry<Path, FileInfo> result = future.get();
                infoForFiles.put(result.getKey(), result.getValue());
			}
		} catch( InterruptedException | ExecutionException | IOException e ) {
			e.printStackTrace();
		}
		
		try {
			executor.shutdown();
			executor.awaitTermination( 1, TimeUnit.DAYS );
		} catch( InterruptedException e ) {
			e.printStackTrace();
		}
		
		infoForFiles.forEach( (key, value) -> System.out.println( key + ": " + value ) );
	}
	
	private static Map.Entry<Path, FileInfo> computeFileInfo( Path textFile )
	{
        
        long numLines = 0;
        long numLinesStartingWithL = 0;
        long sizeOfFile = -1;

		try {
            sizeOfFile = Files.size(textFile);
			for (String line : Files.readAllLines(textFile)) {
                ++numLines;
                if (line.startsWith("L")) {
                    ++numLinesStartingWithL;
                }
            }
		} catch( IOException e ) {
			e.printStackTrace();
		}
		
        return new HashMap.SimpleEntry<>(textFile, new FileInfo(sizeOfFile, numLines, numLinesStartingWithL));
    }
    
    private static class FileInfo {
        // the size of the file;
		public final long sizeOfFile;
        // the number of lines contained in the file;
		public final long numLines;
        // the number of lines starting with the uppercase letter "L".
        public final long numLinesStartingWithL;
        
        public FileInfo(long sizeOfFile, long numLines, long numLinesStartingWithL) {
            this.sizeOfFile = sizeOfFile;
            this.numLines = numLines;
            this.numLinesStartingWithL = numLinesStartingWithL;
        }
        
        @Override
        public String toString() {
            return String.format("(filesize:%d, numlines:%d, num_l_lines:%d)", sizeOfFile, numLines, numLinesStartingWithL);
        }
    }
}
