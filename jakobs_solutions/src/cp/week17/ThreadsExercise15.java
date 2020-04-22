package cp.week17;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ThreadsExercise15
{
	/*
	Adapt your program from ThreadsExercise14 to use an ExecutorCompletionService, as in Threads/cp/WalkCompletionService.
	*/
    
    public static void main(String[] args) {
		// word -> number of times it appears over all files
		Map< Path, FileInfo > infoForFiles = new HashMap<>();
		ExecutorService executor = Executors.newWorkStealingPool();
        
        ExecutorCompletionService<FileInfoAndPath> completionService =
                new ExecutorCompletionService<>(executor);
        
		try {
			long pendingTasks =
				Files.walk( Paths.get( "data" ) )
					.filter( Files::isRegularFile )
					.map( filepath ->
						completionService.submit( () -> computeFileInfo( filepath ) )
					)
					.count();

            while (pendingTasks > 0) {
                Future<FileInfoAndPath> future = completionService.take();
				FileInfoAndPath result = future.get();
                infoForFiles.put(result.filepath, result.fileInfo);
                --pendingTasks;
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
	
	private static FileInfoAndPath computeFileInfo( Path textFile )
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
