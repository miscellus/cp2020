package cp.week17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
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
    
    public static void main(String[] args) {
		// word -> number of times it appears over all files
        Map< Path, FileInfo > infoForFiles = new ConcurrentHashMap<>();
         
        try {
            List<CompletableFuture<Void>> futures =
                    Files.walk( Paths.get( "data" ) )
                    .filter( Files::isRegularFile )
                    .map( filepath -> CompletableFuture
                            .supplyAsync( () -> computeFileInfo( filepath ) )
                            .thenAccept( (FileInfo result) -> {
                                infoForFiles.put(filepath, result);
                            })
                    )
                    .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            infoForFiles.forEach( (key, value) -> System.out.println( key + ": " + value ) );
                
        } catch (IOException e) {
            e.printStackTrace();
        }	
	}
	
	private static FileInfo computeFileInfo( Path textFile )
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
		
		return new FileInfo(sizeOfFile, numLines, numLinesStartingWithL);
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
