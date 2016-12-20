package algorithm;

import math.Function;
import math.Transposition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class SimulatedAnnealing implements Algorithm {
    private Random random;
    private int numberOfAttempts;
    private int bugLength;
    private int transpositionCount;

    private double alpha = 0.025;
    private double initTemperature = 1;

    public SimulatedAnnealing(int numberOfAttempts, int bugLength, int transpositionCount,
                              double alpha, double initTemperature) {
        this.numberOfAttempts = numberOfAttempts;
        this.bugLength = bugLength;
        this.transpositionCount = transpositionCount;

        this.alpha = alpha;
        this.initTemperature = initTemperature;

        this.random = new Random();
    }

    public Transposition getBestLength() {
        ArrayList<Integer> startList = new ArrayList<>(Arrays.asList(5, 1, 2, 3, 4, 6, 7, 8, 9, 5));
        Transposition startTransposition = new Transposition(startList);

        boolean frozen;
        int accepted = 0, rejected = 0, isFreezing = 0;
        double t = calculateInitialT(numberOfAttempts, startTransposition, initTemperature);

        do {
            frozen = false;
            Transposition modifiedTransposition = new Transposition(new ArrayList<>(startTransposition.getElementsList()));

            for (int swapCount = 0; swapCount < transpositionCount; swapCount++) {
                Collections.swap(modifiedTransposition.getElementsList(),
                        random.nextInt(modifiedTransposition.getElementsList().size()),
                        random.nextInt(modifiedTransposition.getElementsList().size()));
            }

            double dF = Function.getLength(modifiedTransposition, bugLength) - Function.getLength(startTransposition, bugLength);
            if (random.nextDouble() < Math.pow(Math.E, - dF / t)) {
                startTransposition = new Transposition(modifiedTransposition.getElementsList());
                accepted++;
            } else {
                rejected++;
            }

            if (accepted == transpositionCount) {
                t = (1 - alpha) * t;
                accepted = 0;
                rejected = 0;

                isFreezing = 0;
            } else if (rejected == 2 * transpositionCount) {
                t = (1 - alpha) * t;
                accepted = 0;
                rejected = 0;

                isFreezing++;
                if (isFreezing == 3) frozen = true;
            }

        } while (!frozen);

        System.out.println(startTransposition.getElementsList());

        return startTransposition;
    }

    private double calculateInitialT(int numberOfAttempts, Transposition transposition, double initialT) {
        Transposition startTransposition = new Transposition(new ArrayList<>(transposition.getElementsList()));
        double t = initialT;
        double accepted = 0, rejected = 0;

        for (int i = 0; i < numberOfAttempts; i++) {
            Transposition modifiedTransposition = new Transposition(new ArrayList<>(startTransposition.getElementsList()));

            for (int swapCount = 0; swapCount < transpositionCount; swapCount++) {
                Collections.swap(modifiedTransposition.getElementsList(),
                        random.nextInt(modifiedTransposition.getElementsList().size()),
                        random.nextInt(modifiedTransposition.getElementsList().size()));
            }

            double dF = Function.getLength(modifiedTransposition, bugLength) - Function.getLength(startTransposition, bugLength);
            if (random.nextDouble() < Math.pow(Math.E, - dF / t)) {
                startTransposition = new Transposition(modifiedTransposition.getElementsList());
                accepted++;
            } else {
                rejected++;
            }

            double rate = accepted / (accepted + rejected);

            System.out.println("rate : " + rate + " t: " + t);

            if (rate >= 0.945 && rate <= 0.955) {
                return t;
            }

            if (rate < 0.945) {
                t = t / (1 - alpha);
            } else {
                t = t * (1 - alpha);
            }
        }

        return t;
    }
}
