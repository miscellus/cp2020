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
        
        final AtomicCounter counter = new AtomicCounter(0);
        
        
        Runnable task = () -> {
            
            //This is bad: synchronized (counter) {
                for (int i = 0; i < 1000; ++i) {
                    counter.increment();
                }
            //}
        };
        
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        
        t1.start();
        t2.start();
        
        t1.join();
        t2.join();
        
        System.out.println(counter.get());
        
    }
    
    public static class SafeCounter {
        private int i;
        
        public SafeCounter(int i) {
            this.i = i;
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
            return i;
        }
    }
    
    
    public static class AtomicCounter {
        private AtomicInteger i;
        
        public AtomicCounter(int i) {
            this.i = new AtomicInteger(i);
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
    
    public static class UnsafeCounter {
        int i;
        
        public UnsafeCounter(int i) {
            this.i = i;
        }
        
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
    
    
}
