package cp.week18;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise21
{
	/*
	Modify Threads/cp/WalkParallelStreamFindAny such that it returns a boolean
	telling whether there exists at least one file with more than 10 lines.
	*/
    
    public static void main(String[] args) {
		try {
			boolean found =
				Files
					.walk( Paths.get( "data" ) )
					.filter( Files::isRegularFile )
					.collect( Collectors.toList() )
					.parallelStream()
                    .map(path -> computeFileInfo(path))
					.anyMatch( Objects::nonNull );
			System.out.println( found );
		} catch( IOException e ) {
			e.printStackTrace();
		}
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
                
                
                if (numLines > 10) {
                    return null;
                }
                
                
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
