package cp.week10;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise1
{
	/*
	- Create a Counter class storing an integer (a field called i), with an increment and decrement method.
	- Make Counter thread-safe (see Chapter 2 in the book)
	- Does it make a different to declare i private or public?
	*/
    
    public static void main(String[] args) throws InterruptedException {
        
        final UnsafeCounter c1 = new UnsafeCounter(0);
        final CounterUsingAtomic c2 = new CounterUsingAtomic(0);
        final CounterUsingSynchronize c3 = new CounterUsingSynchronize(0);
        
        Runnable task = () -> {
            for (int i = 0; i < 1000; ++i) {
                c1.increment();
                c2.increment();
                c3.increment();
            }
        };
        
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();
        
        System.out.println("Unsafe counter: " + c1.get());
        System.out.println("Counter using atomic: " + c2.get());
        System.out.println("Counter using synchronized: " + c3.get());
        
    }
    
    public static class UnsafeCounter {
        private int i;
        
        public UnsafeCounter(int initialValue) {i = initialValue;}
        
        public void increment() {
            ++i;
        }
        
        public void decrement() {
            --i;
        }
        
        public int get() {
            return i;
        }
    }
    
    public static class CounterUsingAtomic {
        private AtomicInteger i;
        
        public CounterUsingAtomic(int initialValue) {
            i = new AtomicInteger(initialValue);
        }
        
        public void increment() {
            i.incrementAndGet();
        }
        
        public void decrement() {
            i.decrementAndGet();
        }
        
        public int get() {
            return i.get();
        }
    }
    
    public static class CounterUsingSynchronize {
        private int i;
        
        public CounterUsingSynchronize(int initialValue) {
            i = initialValue;
        }
        
        public void increment() {
            synchronized(this) {
                ++i;
            }
        }
        
        public void decrement() {
            synchronized(this) {
                --i;
            }
        }
        
        public int get() {
            synchronized(this) {
                return i;
            }
        }
    }
}
