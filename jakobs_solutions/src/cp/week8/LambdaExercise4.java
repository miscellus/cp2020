package cp.week8;

import java.util.ArrayList;

/**
 * 
 * @author Fabrizio Montesi
 */
public class LambdaExercise4
{
	/*
	- Create a list of type ArrayList<String> with some elements of your preference.
	- Create a Box that contains the list.
	- Now compute the sum of the lengths of all strings in the list inside of the box,
	  by invoking Box::apply with a lambda expression.
	*/

    public static void main(String args[]) {
        final ArrayList<String> list = new ArrayList<>();
        list.add("en");
        list.add("fem");
        list.add("otte");
        list.add("niogtyve");
        list.add("seksoghalvtredsenstyvende");
        
        LambdaExercise2.Box<ArrayList<String>> boxed_list_of_strings
                = new LambdaExercise2.Box<>(list);
        
        int outer_sum = boxed_list_of_strings.apply(
            (ArrayList<String> l) -> {
                int sum = 0;
                for (String s : l) {
                    sum += s.length();
                }
                return sum;
            });

        System.out.println(outer_sum);
    }
}
