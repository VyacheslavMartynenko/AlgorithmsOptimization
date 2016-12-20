package algorithm;

import math.Function;
import math.Transposition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class HillClimbing implements Algorithm {
    private Random random;
    private int numberOfAttempts;
    private int bugLength;
    private int transpositionCount;

    public HillClimbing(int numberOfAttempts, int bugLength, int transpositionCount) {
        this.numberOfAttempts = numberOfAttempts;
        this.bugLength = bugLength;
        this.transpositionCount = transpositionCount;
        this.random = new Random();
    }

    public Transposition getBestLength() {
        boolean found;
        ArrayList<Integer> startList = new ArrayList<>(Arrays.asList(5, 1, 2, 3, 4, 6, 7, 8, 9, 5));
//        select x from X at Random
        Transposition startTransposition = new Transposition(startList);
//            Repeat
        int tmp = 0;
        do {
//                found <- false
            found = false;
//                for i=1 to Number of Attempts
            for (int i = 0; i < numberOfAttempts; i++) {
                System.out.println("tmp:" + ++tmp);
//                select y From Ne(x) at Random
                Transposition modifiedTransposition = new Transposition(new ArrayList<>(startTransposition.getElementsList()));

                for (int swapCount = 0; swapCount < transpositionCount; swapCount++) {
                    Collections.swap(modifiedTransposition.getElementsList(),
                            random.nextInt(modifiedTransposition.getElementsList().size()),
                            random.nextInt(modifiedTransposition.getElementsList().size()));
                }

//                if F(y) < F(x)
                if (Function.getLength(modifiedTransposition, bugLength) < Function.getLength(startTransposition, bugLength)) {
//                    x <- y
                    startTransposition = new Transposition(modifiedTransposition.getElementsList());
//                    found <- true
                    found = true;
                }
            }
//            Until not found
        }
        while (!found);
//         return x
        System.out.println(startTransposition.getElementsList());
        return startTransposition;
    }
}
