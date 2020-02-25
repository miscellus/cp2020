package cp.week9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class StreamExercise4
{
	/*
	- Create a stream of lines for the file created in StreamExercise1.
	- Use Stream::mapToInt and IntStream::sum to count how many times
	  the letter "C" occurs in the entire file.
	*/
    
    public static void main(String[] args) {
        Path path = Paths.get("week9/legal.txt");
        
        long numLinesWithL;
        
        try(Stream<String> lines = Files.lines(path)) {
            
            //*
            numLinesWithL =
                    lines.mapToLong(
                            line -> line.chars()
                                    .filter(x -> x == 'C')
                                    .count()
                    )
                    .sum();
            //*/
            
            // Using flat map (map from "one thing" to "many things")
            /*
            numLinesWithL =
                    lines.flatMapToInt(
                            line -> line.chars()
                    )
                    .filter(x -> x == 'C')
                    .count();
            //*/
            
            System.out.println(numLinesWithL);
        }
        catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
