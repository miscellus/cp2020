package cp.end_of_slides2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class ExercisesFromSlides {
    /*
    Opt: Define a generic class Pair<K,V> that can store pairs of values of any types.
    Opt: Create a List of Pair<String, Integer> with some values. For each pair containing a string s and an integer n, we say that s is associated to n.
    Opt: For each string (first value of a pair) in the list, print the sum of all integers associated to that string.
    */

    public static class Pair<K,V> {
        public final K key;
        public final V value;
        
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }
    
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

        HashMap<String, Integer> sums = new HashMap<>();
        
        for (Pair<String, Integer> p : list) {
            Integer sum_now = sums.get(p.key);
            if (sum_now == null) {
                sums.put(p.key, p.value);
            }
            else {
                sums.put(p.key, p.value+sum_now);
            }
        }
        
        for (Entry<String,Integer> e : sums.entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
        }

    }
}
