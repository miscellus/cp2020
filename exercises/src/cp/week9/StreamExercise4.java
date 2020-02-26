package cp.week9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StreamExercise4
{
	/*
	- Create a stream of lines for the file created in StreamExercise1.
	- Use Stream::mapToInt and IntStream::sum to count how many times
	  the letter "C" occurs in the entire file.
	*/
    
    
    
    public static void main(String[] args) throws IOException {
        int numCsTotal = Files.lines(Paths.get("week9/legal.txt"))
                .filter((line) -> line.contains("C"))
                /*
                .mapToInt((line) -> {
                    //"Hej med dig Casper." -> ["Hej med dig ", "asper."]
                    String[] splitByCs = line.split("C");
                    return splitByCs.length - 1;
                })
                //*/
                //*
                //.map((line) -> {
                .mapToInt((line) -> {
                    int numCs = 0;
                    for (int i = 0; i < line.length(); ++i) {
                        if (line.charAt(i) == 'C') {
                            ++numCs;
                        }
                    }
                    return numCs;
                })
                //*/
                .sum();
                //.reduce(0, Integer::sum);
        
        System.out.println(numCsTotal);
    }
}
