package cp.week8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Fabrizio Montesi
 */
public class LambdaExercise3
{
	/*
	NOTE: When I write Class::methodName, I don't mean to use a method reference (lambda expression), I'm simply
	talking about a particular method.
	*/
	
	/*
	- Create a Box that contains an ArrayList<String> with some elements of your preference.
	- Now compute a sorted version of your list by invoking Box::apply, passing a lambda expression that uses List::sort.
	*/
    
    public static void main(String args[]) {
        final ArrayList<String> list = new ArrayList<>();
        list.add("B");
        list.add("F");
        list.add("E");
        list.add("A");
        
        LambdaExercise2.Box<ArrayList<String>> boxed_list_of_strings
                = new LambdaExercise2.Box<>(list);
        
        boxed_list_of_strings.apply(
            (ArrayList<String> l) -> {
                l.sort(String::compareTo);
                return l;
            });

        list.forEach(System.out::println);
    }
}
