package cp.week10;

import java.awt.Color;
import javax.swing.JColorChooser;

/**
 *
 * @author Fabrizio Montesi <fmontesi@imada.sdu.dk>
 */
public class ThreadsExercise2
{
	/*
	- Read and replicate the examples in: https://docs.oracle.com/javase/tutorial/essential/concurrency/immutable.html
	*/
    
    public static void main(String[] args) {
        
        
        SynchronizedRGB color = new SynchronizedRGB(0, 0, 0, "Pitch Black");
        // ...
        int myColorInt = color.getRGB();      //Statement 1
        { // Other thread changes color!!!
            color.set(255, 128, 0, "Donald Trump");
        }
        String myColorName = color.getName(); //Statement 2
        
        System.out.println(myColorInt + ":" + myColorName);
        JColorChooser.showDialog(null, "myColorName: " + myColorName, new Color(myColorInt));
    }
    
    public static class SynchronizedRGB {

        // Values must be between 0 and 255.
        private int red;
        private int green;
        private int blue;
        private String name;

        private void check(int red,
                           int green,
                           int blue) {
            if (red < 0 || red > 255
                || green < 0 || green > 255
                || blue < 0 || blue > 255) {
                throw new IllegalArgumentException();
            }
        }

        public SynchronizedRGB(int red,
                               int green,
                               int blue,
                               String name) {
            check(red, green, blue);
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.name = name;
        }

        public void set(int red,
                        int green,
                        int blue,
                        String name) {
            check(red, green, blue);
            synchronized (this) {
                this.red = red;
                this.green = green;
                this.blue = blue;
                this.name = name;
            }
        }

        public synchronized int getRGB() {
            return ((red << 16) | (green << 8) | blue);
        }

        public synchronized String getName() {
            return name;
        }

        public synchronized void invert() {
            red = 255 - red;
            green = 255 - green;
            blue = 255 - blue;
            name = "Inverse of " + name;
        }
    }
    
    final public class ImmutableRGB {

        // Values must be between 0 and 255.
        final private int red;
        final private int green;
        final private int blue;
        final private String name;

        private void check(int red,
                           int green,
                           int blue) {
            if (red < 0 || red > 255
                || green < 0 || green > 255
                || blue < 0 || blue > 255) {
                throw new IllegalArgumentException();
            }
        }

        public ImmutableRGB(int red,
                            int green,
                            int blue,
                            String name) {
            check(red, green, blue);
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.name = name;
        }


        public int getRGB() {
            return ((red << 16) | (green << 8) | blue);
        }

        public String getName() {
            return name;
        }

        public ImmutableRGB invert() {
            return new ImmutableRGB(255 - red,
                           255 - green,
                           255 - blue,
                           "Inverse of " + name);
        }
    }
}
