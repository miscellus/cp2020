package cp.week9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StreamExercise3
{
	/*
	- Create a stream of lines for the file created in StreamExercise1.
	- Use Stream::filter and Stream::count to count how many lines
	  contain the letter "L".
	*/
    
    public static void main(String[] args) throws IOException {
        long numLinesWithL = Files.lines(Paths.get("week9/legal.txt"))
                .filter(line -> line.contains("L"))
                .count();
        
        System.out.println(numLinesWithL);
                
    }
}
