package cp.week11;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise5
{
	/*
	Apply the technique for fixing Listing 4.14 to Listing 4.15 in the book, but to the following:
	- Create a thread-safe Counter class that stores an int and supports increment and decrement.
	- Create a new thread-safe class Point, which stores two Counter objects.
	- The two counter objects should be public.
	- Implement the method boolean areEqual() in Point, which returns true if the two counters store the same value.
	
	Question: Is the code you obtained robust with respect to client-side locking (see book)?
	          Would it help if the counters were private?
	*/
    
    public static void main(String[] args) {
        final Point a = new Point(0, 10);
        
        // Since the counters are final in the Point class, we cannot make
        // another Point object, `b', using the same counters as `a'
        // but with x and y exchanged which could have otherwise lead to deadlocks.
        //
        //     Point b = new Point(0, 0);
        //     b.x = a.y;  <--- Compilation error
        //     b.y = a.x;  <--- Compilation error
        
        Thread t1 = new Thread(() -> {
            while (a.x.increment() < 10) {
                if (a.areEqual()) {
                    System.out.println("They were equal");
                }
                else {
                    System.out.println("He");
                }
            }
        });
        
        Thread t2 = new Thread(() -> {
            while (a.y.decrement() > 0) {
                
                // Client side locking with reverse locking order:
                // ...In the areEqual method, first x is locked, then y;
                // here it is reversed which could lead to a deadlock.
                // Think about why, and run the code to see if it gets stuck.

                // Q: Would it help to make x and y private?
                // A: It would certainly prevent us from writing this:
            
                synchronized (a.y) {
                    synchronized (a.x) {
                        if (a.x.get() == a.y.get()) {
                            System.out.println("They were equal");
                        }
                        else {
                            System.out.println("Oh");
                        }
                    }
                }
            }
        });
        
        t1.start();
        t2.start();
        
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException ex) {}
    }
    
    public static final class Point {
        
        // Public by request
        public final SafeCounter x;
        public final SafeCounter y;
        
        public Point(int x, int y) {
            this.x = new SafeCounter(x);
            this.y = new SafeCounter(y);
        }
        
        public boolean areEqual() {
            synchronized(x) {
                synchronized(y) {
                    System.out.println(this);
                    return x.get() == y.get();
                }
            }
        }
        
        @Override
        public String toString() {
            synchronized(x) {
                synchronized(y) {
                    return "Point("+x.get()+", "+y.get()+")";
                }
            }
        }
        
    }
    
    public static final class SafeCounter {
        private final AtomicInteger i;
        
        public SafeCounter(int i) {
            this.i = new AtomicInteger(i);
        }
        
        public int increment() {
            return i.incrementAndGet();
        }
        
        public int decrement() {
            return i.decrementAndGet();
        }
        
        public int get() {
            return i.get();
        }
    }
}
