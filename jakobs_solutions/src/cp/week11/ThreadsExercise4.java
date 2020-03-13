package cp.week11;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise4
{
	/*
	- Write the example from Listing 4.2 in the book.
	- Add a method that returns a reference to the internal field mySet.
	- Use the new method from concurrent threads to create unsafe access to mySet.
	*/
    
    public static void main(String[] args) {
        
        final PersonSet safelyUsedPersonSet = new PersonSet();
        final PersonSet unsafelyUsedPersonSet = new PersonSet();
        
        Runnable safelyAdd1000Persons = () -> {
            for (int i = 0; i < 1000; ++i) {
                safelyUsedPersonSet.addPerson(new Person());
            }
        };
        
        Runnable unsafelyAdd1000Persons = () -> {
            Set<Person> myEscapedSet = unsafelyUsedPersonSet.unsafePublicationOfMySet();
            
            for (int i = 0; i < 1000; ++i) {
                myEscapedSet.add(new Person());
            }
        };
        
        Thread safeThread1 = new Thread(safelyAdd1000Persons);
        Thread safeThread2 = new Thread(safelyAdd1000Persons);
        Thread unsafeThread1 = new Thread(unsafelyAdd1000Persons);
        Thread unsafeThread2 = new Thread(unsafelyAdd1000Persons);
        
        safeThread1.start();
        safeThread2.start();
        unsafeThread1.start();
        unsafeThread2.start();
        
        try {
            safeThread1.join();
            safeThread2.join();
            unsafeThread1.join();
            unsafeThread2.join();
        } catch (InterruptedException e) {}
        
        System.out.println("number of persons in safely used person set: "
                + safelyUsedPersonSet.numberOfPersonsInSet());
        System.out.println("number of persons in unsafely used person set: "
                + unsafelyUsedPersonSet.numberOfPersonsInSet());
        
        /* example output:
        number of persons in safely used person set: 2000
        number of persons in unsafely used person set: 1969
        */
    }
    
    public static class PersonSet {
        //@GuardedBy("this")
        private final Set<Person> mySet = new HashSet<Person>();
        public synchronized void addPerson(Person p) {
            mySet.add(p);
        }
        public synchronized boolean containsPerson(Person p) {
            return mySet.contains(p);
        }
        
        
        // The keyword synchronized really does not help us in this context,
        // it only ensures us that only one thread at a time can get the
        // reference to the otherwise private field.
        //
        // The danger here is that we can circumvent the syncronized methods
        // above by directly using the refence to `mySet' that this method
        // returns.
        public synchronized Set<Person> unsafePublicationOfMySet() {
            return mySet;
        }
        
        public synchronized int numberOfPersonsInSet() {
            return mySet.size();
        }
        
    }
    
    public static final class Person {
        // Philosophical question: What in truth makes a person? (^;
    }
}
