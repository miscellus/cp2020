package cp.week17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class ThreadsExercise14
{
	/*
	Modify Threads/cp/WalkExecutorFuture such that, instead of word occurrences,
	it computes a map of type Map< Path, FileInfo >, which maps the Path of each file found in "data"
	to an object of type FileInfo that contains:
		- the size of the file;
		- the number of lines contained in the file;
		- the number of lines starting with the uppercase letter "L".
	*/
    
    public static void main(String[] args)
	{
		// word -> number of times it appears over all files
		Map< Path, FileInfo > infoForFiles = new HashMap<>();
		ExecutorService executor = Executors.newWorkStealingPool();

		try {
			List< Future< FileInfoAndPath > > futures =
				Files.walk( Paths.get( "data" ) )
					.filter( Files::isRegularFile )
					.map( filepath ->
						executor.submit( () -> computeFileInfo( filepath ) )
					)
					.collect( Collectors.toList() );
			
            for( Future< FileInfoAndPath > future : futures ) {
				FileInfoAndPath result = future.get();
				infoForFiles.put( result.filePath, result.fileInfo );
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
		
		infoForFiles.forEach( (word, n) -> System.out.println( word + ": " + n ) );
	}
    
	private static FileInfoAndPath computeFileInfo( Path textFile )
	{
        long filesize = 0;
        final long[] numLines = new long[1];
        final long[] numLLines = new long[1];
        
		try {
            filesize = Files.size(textFile);
            Files.lines(textFile)
                    .forEach(line -> {
                        numLines[0]++;
                        if (line.startsWith("L")) {
                            numLLines[0]++;
                        }
                    });
		} catch( IOException e ) {
			e.printStackTrace();
		}
		
		return new FileInfoAndPath(new FileInfo(filesize, numLines[0], numLLines[0]), textFile);
	}
    
    private static class FileInfoAndPath {
        public FileInfo fileInfo;
        public Path filePath;

        public FileInfoAndPath(FileInfo fileInfo, Path filePath) {
            this.fileInfo = fileInfo;
            this.filePath = filePath;
        }
    }
    
    private static class FileInfo {
//        - the size of the file;
//		- the number of lines contained in the file;
//		- the number of lines starting with the uppercase letter "L".
        public final long filesize;
        public final long numLines;
        public final long numLLines;

        public FileInfo(long filesize, long numLines, long numLLines) {
            this.filesize = filesize;
            this.numLines = numLines;
            this.numLLines = numLLines;
        }
        
        @Override
        public String toString() {
            return String.format("(filesize:%d, numlines:%d, num_l_lines:%d)", filesize, numLines, numLLines);
        }
    }
    
}
