package cp.week9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

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
        Path path = Path.of("week9/legal.txt");
        
        Map<String, Integer> result = Files.lines(path)
                .map(line -> line.chars()
                        .mapToObj(Character::toString)
                        .collect(HashMap<String,Integer>::new,
                                (map, character) -> map.merge(character, 1, Integer::sum),
                                StreamExercise5::mergeAll
                        )
                )
                .reduce(
                        new HashMap<>(),
                        StreamExercise5::mergeAll
                );
        
        result.forEach((String character, Integer occurrences) -> {
            System.out.printf("%s: %d\n", character, occurrences);
        });
    }
    
    public static HashMap<String, Integer> mergeAll(HashMap<String,Integer> acc, HashMap<String,Integer> next) {
        next.forEach((key, value) -> acc.merge(key, value, Integer::sum));
        return acc;
    }
}
