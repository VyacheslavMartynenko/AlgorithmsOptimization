package math;

public class Function {
    public static double getLength(Transposition transposition, int lengthBound) {
        int value = 0;
        int count = 0;
        for (int element : transposition.getElementsList()) {
            if (value + element > lengthBound) {
                value = 0;
                count++;
            }
            value += element;
        }
        count++;

        System.out.println(transposition.getElementsList());
        System.out.println(count);

        return count;
    }
}
