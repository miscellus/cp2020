package cp.week9;

import java.io.IOException;
import java.nio.file.Files;
import static java.nio.file.Files.lines;
import static java.nio.file.Files.lines;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class StreamExercise2
{
	/*
	- Create a stream of lines for the file created in StreamExercise1.
	- Use Stream::filter and Stream::collector (the one with three parameters)
	  to create an ArrayList of all lines that start with a "C".
	- Suggestion: look at https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html#collect-java.util.function.Supplier-java.util.function.BiConsumer-java.util.function.BiConsumer-
	*/
    
    public static void main(String[] args) throws IOException {
       
        /*
        ArrayList<String> resultList = Files.lines(Paths.get("week9/legal.txt"))
                .filter((line) -> line.startsWith("C"))
                .collect(
                        () -> new ArrayList<String>(),
                        (ArrayList<String> list, String nextElement) -> list.add(nextElement),
                        (list1, list2) -> list1.addAll(list2));
        */
        
        ArrayList<String> resultList = Files.lines(Paths.get("week9/legal.txt"))
                .filter((line) -> line.startsWith("C"))
                .collect(
                        ArrayList<String>::new,
                        ArrayList<String>::add,
                        ArrayList<String>::addAll);
        
        
        System.out.println(resultList);
        
    }
}
