package cp.week17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise16
{
	/*
	Adapt your program from ThreadsExercise15 to use CompletableFuture, as in Threads/cp/WalkCompletableFuture.
	*/
    
    public static void main(String[] args)
	{
		// word -> number of times it appears over all files
		Map< Path, FileInfo > infoForFiles = new ConcurrentHashMap<>();
        
		try {
			CompletableFuture<Void>[] completableFutures =
				Files.walk( Paths.get( "data" ) )
					.filter( Files::isRegularFile )
					.map( filepath ->
						CompletableFuture.supplyAsync(() -> computeFileInfo( filepath ) )
                            //.exceptionally( (Throwable throwable) -> System.err.println(throwable))
                            .thenAccept((FileInfoAndPath result) -> {
                                infoForFiles.put( result.filePath, result.fileInfo );
                            })
                            
                            //computeFileInfo( filepath )
					)
					.collect(Collectors.toList()).toArray(new CompletableFuture[0]);
            
            CompletableFuture.allOf(completableFutures).join();
            
		} catch( IOException e ) {
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
