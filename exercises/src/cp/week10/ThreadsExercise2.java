package cp.week10;

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
        SynchronizedRGB color =
            new SynchronizedRGB(0, 0, 0, "Pitch Black");
        //...
        int myColorInt = color.getRGB();      //Statement 1
        { // Pretend this is another thread
            color = new SynchronizedRGB(255, 128, 0, "Donald Trump");
        }
        String myColorName = color.getName(); //Statement 2
        
        int myColorInt2;
        String myColorName2;
        // This is better:
        synchronized (color) {
            myColorInt2 = color.getRGB();
            myColorName2 = color.getName();
        }
        
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
        
        public synchronized SynchronizedRGB getClone() {
            String name = getName();
            
            return new SynchronizedRGB(this.red, this.green, this.blue, name);
        }

        public synchronized void invert() {
            red = 255 - red;
            green = 255 - green;
            blue = 255 - blue;
            name = "Inverse of " + name;
        }
}
}
