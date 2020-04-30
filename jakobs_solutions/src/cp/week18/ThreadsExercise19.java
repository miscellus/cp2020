package cp.week18;

import common.Words;
import cp.week17.ThreadsExercise14;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise19
{
	/*
	Modify Threads/cp/WalkParallelStream5 to compute a Map< Path, FileInfo >
	that maps each file (the Path) to a FileInfo object that contains
		- the Path of the file;
		- the size of the file;
		- the number of lines contained in the file;
		- the number of lines starting with the uppercase letter "L".
	*/
    
    public static void main(String[] args) throws IOException {
        Map< Path, FileInfo > infoForFiles =
            Files
                .walk( Paths.get( "data" ) )
                .filter( Files::isRegularFile )
                .collect( Collectors.toList() )
                .parallelStream()
                .map(path -> computeFileInfo(path))
                .collect( Collectors.toMap(
                    (FileInfoAndPath fiap) -> fiap.filepath,
                    (FileInfoAndPath fiap) -> fiap.fileInfo,
                    (a, b) -> b
                ) );
		
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
