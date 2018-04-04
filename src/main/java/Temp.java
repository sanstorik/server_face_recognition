public class Temp {
    public static void main(String[] args) {
        while(true) {
            try {
                Thread.sleep(1000);
                System.out.println("HELLO");
            } catch(InterruptedException e) {}

            System.out.println("Worker process woke up");
        }
    }
}
