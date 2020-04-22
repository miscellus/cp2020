package cp.week17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise18
{
	/*
	Adapt your program from ThreadsExercise16 to stop as soon as a task that has processed a file with more than 10 lines is completed.
	
	Hint: use CompletableFuture.anyOf
	*/
    
    public static void main(String[] args) {
		// word -> number of times it appears over all files
        Map< Path, FileInfo > infoForFiles = new ConcurrentHashMap<>();
        AtomicBoolean cancelled = new AtomicBoolean(false);
        
        try {
            List<CompletableFuture<Void>> futures =
                    Files.walk( Paths.get( "data" ) )
                    .filter( Files::isRegularFile )
                    .map( filepath -> CompletableFuture
                            .supplyAsync( () -> computeFileInfo( filepath, cancelled ) )
                            .thenAccept( (FileInfo result) -> {
                                if (result != null){
                                    infoForFiles.put(filepath, result);
                                }
                            })
                            .exceptionally(throwable -> {
                                System.err.println(throwable); return null;
                            })

                    )
                    .collect(Collectors.toList());

            CompletableFuture.anyOf(futures.toArray(new CompletableFuture[0])).join();

            infoForFiles.forEach( (key, value) -> System.out.println( key + ": " + value ) );
                
        } catch (IOException e) {
            e.printStackTrace();
        }	
	}
	
	private static FileInfo computeFileInfo( Path textFile, AtomicBoolean cancelled )
	{
        long numLines = 0;
        long numLinesStartingWithL = 0;
        long sizeOfFile = -1;
        
		try {
            sizeOfFile = Files.size(textFile);
			for (String line : Files.readAllLines(textFile)) {
                if (cancelled.get()) {
                    throw new CancellationException();
                }
                
                ++numLines;
                if (line.startsWith("L")) {
                    ++numLinesStartingWithL;
                }
            }
		} catch( IOException e ) {
			e.printStackTrace();
		}

        // NOTE(jakob): only set cancelled to numLines>10 if it is already
        // false (the first argument).
        // `compareAndExchange' returns the old value of cancelled.
        // If the old value was true, we had already cancelled in another thread;
        // hence, we throw a cancellation exception to invalidate the result
        // of this thread.
        if (cancelled.compareAndExchange(false, numLines > 10)) {
            throw new CancellationException();
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
