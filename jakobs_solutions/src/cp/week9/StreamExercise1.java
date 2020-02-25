package cp.week9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class StreamExercise1
{
	/*
	- Create a file with many (>100) lines of text.
	  For example, you can use this website: loremipsum.io
	- Use Files.lines to get a stream of the lines contained within the file.
	- Use Stream::filter and Stream::forEach to print on screen each line that ends with a dot.
	*/
    
    public static void main(String args[]) {
        //Path path = Paths.get("week9/legal.txt");
        Path path = Path.of("week9/legal.txt");
        
        // try with resource (look it up), it basically just auto closes the
        // object in the parenthesis which implements the Closable interface.
        try (Stream<String> stream = Files.lines(path)) {
            
            stream.filter(line -> line.endsWith("."))
                    .forEach(System.out::println);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}