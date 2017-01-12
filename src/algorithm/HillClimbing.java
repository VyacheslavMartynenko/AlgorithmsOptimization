package algorithm;

import math.Function;
import model.Transposition;

import java.util.*;

public class HillClimbing implements Algorithm {
    private Random random;
    private Function function;
    private int numberOfAttempts;
    private int bugLength;
    private int transpositionCount;
    private ArrayList<Integer> startList;

    public HillClimbing(int numberOfAttempts, int bugLength, int transpositionCount, ArrayList<Integer> startList) {
        this.numberOfAttempts = numberOfAttempts;
        this.bugLength = bugLength;
        this.transpositionCount = transpositionCount;
        this.startList = startList;
        this.random = new Random();
        this.function = new Function();
    }

    @Override
    public int getBestLength() {
        boolean found;
        Transposition startTransposition = new Transposition(startList);
        //int tmp = 0;
        do {
            found = false;
            for (int i = 0; i < numberOfAttempts; i++) {
                //System.out.println("tmp:" + ++tmp);
                Transposition modifiedTransposition = new Transposition(new ArrayList<>(startTransposition.getElementsList()));

                for (int swapCount = 0; swapCount < transpositionCount; swapCount++) {
                    Collections.swap(modifiedTransposition.getElementsList(),
                            random.nextInt(modifiedTransposition.getElementsList().size()),
                            random.nextInt(modifiedTransposition.getElementsList().size()));
                }

                if (function.getLengthWithRemains(modifiedTransposition, bugLength) <
                        function.getLengthWithRemains(startTransposition, bugLength)) {
                    startTransposition = new Transposition(modifiedTransposition.getElementsList());
                    found = true;
                }
            }
        }
        while (found);
        System.out.println(startTransposition.getElementsList());
        return function.getLength(startTransposition, bugLength);
    }
}
