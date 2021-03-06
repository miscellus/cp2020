package cp.end_of_slides2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

public class ExercisesFromSlides {
    /*
    Opt: Define a generic class Pair<K,V> that can store pairs of values of any types.
    Opt: Create a List of Pair<String, Integer> with some values. For each pair containing a string s and an integer n, we say that s is associated to n.
    Opt: For each string (first value of a pair) in the list, print the sum of all integers associated to that string.
    */
    
    public static void main(String args[]) {
        List<Pair<String, Integer>> list = new ArrayList<>();
        list.add(new Pair<>("John", 1));
        list.add(new Pair<>("John", 2));
        list.add(new Pair<>("Paul", 3));
        list.add(new Pair<>("Paul", 2));
        list.add(new Pair<>("Paul", 4));
        list.add(new Pair<>("Ringo", 7));
        list.add(new Pair<>("Ringo", 2));
        list.add(new Pair<>("Ringo", 5));
        list.add(new Pair<>("George", 16));
        
        Map<String, Integer> stringToSum = new Hashtable<>();
        
        for (Pair<String, Integer> p : list) {
            // Using our own implementation of a functional interface (Which is what a lambda is):
            stringToSum.merge(p.key, p.value, new OurMergeFunction());
            /*
            // Using a lambda function:
            stringToSum.merge(p.key, p.value,
                    (Integer currentValue, Integer newValue) -> currentValue+newValue);
            */
        }
        
        for (Entry<String, Integer> e: stringToSum.entrySet()) {
            System.out.println(e.getKey()+ ": " + e.getValue());
        }
    }
    
    // NOTE(Jakob): This is what the labmda above expands to behind the scenes:
    //              The implemented interface `BiFunction' will vary depending
    //              on the amount of arguments the lambda takes;
    //              for instance, if the function should only take one argument
    //              the implemented interface would be just `Function'.
    //              These interfaces, `BiFunction' and `Function' are called
    //              *functional interfaces*, you can also define your own
    //              functional interfaces; they really are just interfaces with
    //              a single method.
    public static class OurMergeFunction implements BiFunction<Integer, Integer, Integer> {
        @Override
        public Integer apply(Integer currentValue, Integer newValue) {
            return currentValue + newValue;
        }
    }
    
    public static class Pair<K, V> {
        public final K key;
        public final V value;
        
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
        
    }
}
