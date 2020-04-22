package cp.week17;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise17
{
	/*
	Adapt your program from ThreadsExercise15 to stop as soon as a task that has processed a file with more than 10 lines is completed.
	*/
    
    public static void main(String[] args) throws IOException
	{
		// word -> number of times it appears over all files
		Map< Path, FileInfo > infoForFiles = new HashMap<>();
		ExecutorService executor = Executors.newWorkStealingPool();
        
        AtomicBoolean cancelled = new AtomicBoolean(false);
        
        ExecutorCompletionService completionService = new ExecutorCompletionService(executor);

        long pendingTasks =
            Files.walk( Paths.get( "data" ) )
                .filter( Files::isRegularFile )
                .map( filepath ->
                    completionService.submit( () -> computeFileInfo( filepath, cancelled ) )
                )
                .count();

        executor.shutdown();

        while ( pendingTasks > 0 ) {
            try {
                Future<FileInfoAndPath> future = completionService.take();
                FileInfoAndPath result = future.get();
                infoForFiles.put( result.filePath, result.fileInfo );

            } catch ( InterruptedException | ExecutionException ex) {
                System.err.println(ex);
            }
            --pendingTasks;
        }
		
        try {
            executor.awaitTermination( 1, TimeUnit.DAYS );
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
		
		infoForFiles.forEach( (word, n) -> System.out.println( word + ": " + n ) );
	}
    
	private static FileInfoAndPath computeFileInfo( Path textFile, AtomicBoolean cancelled )
	{
        long filesize = 0;
        final long[] numLines = new long[1];
        final long[] numLLines = new long[1];
        
		try {
            filesize = Files.size(textFile);
            Files.lines(textFile)
                    .forEach(line -> {
                        if (cancelled.get()) {
                            throw new CancellationException();
                        }
                        
                        numLines[0]++;
                        if (line.startsWith("L")) {
                            numLLines[0]++;
                        }
                    });
		} catch( IOException e ) {
			e.printStackTrace();
		}
		
        if (cancelled.compareAndExchange(false, numLines[0] > 10)) {
            throw new CancellationException();
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
