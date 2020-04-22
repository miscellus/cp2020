package cp.week17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise17
{
	/*
	Adapt your program from ThreadsExercise15 to stop as soon as a task that has processed a file with more than 10 lines is completed.
	*/
    
    public static void main(String[] args) throws IOException {
		Map< Path, FileInfo > infoForFiles = new HashMap<>();
		ExecutorService executor = Executors.newWorkStealingPool();
        
        AtomicBoolean cancelled = new AtomicBoolean(false);
        
        ExecutorCompletionService<FileInfoAndPath> completionService =
                new ExecutorCompletionService<>(executor);
        
        List<Future<FileInfoAndPath>> futures =
            Files.walk( Paths.get( "data" ) )
                .filter( Files::isRegularFile )
                .map( filepath -> completionService
                        .submit( () -> computeFileInfo( filepath, cancelled ))
                )
                .collect(Collectors.toList());
        
        executor.shutdown();

        for (int i = futures.size(); i > 0; --i) {
            try {
                FileInfoAndPath result = completionService.take().get();

                if (result != null) {
                    futures.forEach(f -> f.cancel(true));
                    infoForFiles.put(result.filepath, result.fileInfo);
                }
            }
            catch (ExecutionException | InterruptedException | CancellationException ex) {
                System.err.println(ex);
            }
        }
		
		infoForFiles.forEach( (key, value) -> System.out.println( key + ": " + value ) );
	}
	
	private static FileInfoAndPath computeFileInfo( Path textFile, AtomicBoolean cancelled )
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
		} catch( IOException e ) {}
		
        // NOTE(jakob): only set cancelled to numLines>10 if it is already
        // false (the first argument).
        // `compareAndExchange' returns the old value of cancelled.
        // If the old value was true, we had already cancelled in another thread;
        // hence, we throw a cancellation exception to invalidate the result
        // of this thread.
        if (cancelled.compareAndExchange(false, numLines>10)) {
            throw new CancellationException();
        }
        
		return new FileInfoAndPath(new FileInfo(sizeOfFile, numLines, numLinesStartingWithL), textFile);
    }

    private static class FileInfoAndPath {
        public final FileInfo fileInfo;
        public final Path filepath;
        
        public FileInfoAndPath(FileInfo fileInfo, Path filepath) {
            this.fileInfo = fileInfo;
            this.filepath = filepath;
        }
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
