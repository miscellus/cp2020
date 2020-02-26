package cp.week9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class StreamExercise5
{
	/* ! (Exercises marked with ! are more difficult.)
	
	- Create a stream of lines for the file created in StreamExercise1.
	- Use Stream::map to map each line to a HashMap<String, Integer> that
	  stores how many times each character appears in the line.
	  For example, for the line "abbc" you would produce a map with entries:
	    a -> 1
	    b -> 2
	    c -> 1
	- Use Stream::reduce(T identity, BinaryOperator<T> accumulator)
	  to produce a single HashMap<String, Integer> that stores
	  the results for the entire file.
	*/
    
    public static void main(String[] args) throws IOException {
        HashMap<String, Integer> totalHistogram = Files.lines(Paths.get("week9/legal.txt"))
                .map((line) -> {
                    HashMap<String, Integer> histogram = new HashMap<>();
                    
                    for (String s : line.split("")) {
                        //histogram.merge(s, 1, (oldValue, newValue) -> oldValue + newValue);
                        histogram.merge(s, 1, Integer::sum);
                    }
                    
                    return histogram;
                })
                .reduce(new HashMap<String, Integer>(),
                        (result, next) -> {
                            next.forEach((k, v) -> result.merge(k, v, Integer::sum));
                            return result;
                        });
        
        totalHistogram.forEach((k, v) -> System.out.println(k + ": " + v));
        
    }
    
    
}
