package cp.week8;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Fabrizio Montesi
 */
public class LambdaExercise5
{
	/*
	- Write a static method Box::applyToAll that, given
	  a list of Box(es) with the same type and a BoxFunction with compatible type,
	  applies the BoxFunction to all the boxes and returns a list
	  that contains the result of each BoxFunction invocation.
	*/
    
    public static void main(String args[]) {
        List<Box<Integer>> list = new ArrayList<>();
        list.add(new Box<>(10));
        list.add(new Box<>(-8));
        list.add(new Box<>(14142));
        
        List<Integer> list_prime = Box.applyToAll(list, x -> x*x);
        
        list_prime.forEach(System.out::println);
    }
    
    public static class Box<T> {
        private final T content;
        
        public Box(T content) throws IllegalArgumentException {
            if (content == null) {
                throw new IllegalArgumentException();
            }
            this.content = content;
        }
        
        public T content() {
            return this.content;
        }
        
        public <O> O apply(BoxFunction<T, O> fn) {
            return fn.apply(content);
        }
        
        public static <I,O> List<O> applyToAll(List<Box<I>> list, BoxFunction<I, O> fn) {
            final ArrayList<O> result = new ArrayList<>();
            list.forEach((box) -> {
                result.add(box.apply(fn));
            });
            return result;
        }
    }
        
    public interface BoxFunction<I,O> {
        O apply(I input);
    }
}
