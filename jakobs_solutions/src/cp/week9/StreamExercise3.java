package cp.week9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class StreamExercise3
{
	/*
	- Create a stream of lines for the file created in StreamExercise1.
	- Use Stream::filter and Stream::count to count how many lines
	  contain the letter "L".
	*/
    
    public static void main(String[] args) {
        Path path = Paths.get("week9/legal.txt");
        
        try(Stream<String> lines = Files.lines(path)) {
            
            long numLinesWithL = lines
                    .filter(x -> 0 <= x.indexOf("L"))
                    .count();
            
            System.out.println(numLinesWithL);
        }
        catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
