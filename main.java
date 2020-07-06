import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Random random = new Random(20200705);
        AVLTree tree = new AVLTree();

        for (int i = 0; i < 1000; i++) {
            int r = random.nextInt(10000);
            try {
                tree.insert(r);
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
            }
        }
        tree.verify();
    }
}
