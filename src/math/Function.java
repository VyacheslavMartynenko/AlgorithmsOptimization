package math;

import model.Objective;
import model.Transposition;

import java.util.ArrayList;
import java.util.Collections;

public class Function {
    public int getLength(Transposition transposition, int lengthBound) {
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

        return count;
    }

    public double getLengthWithRemains(Transposition transposition, int lengthBound) {
        double remains = 0;
        int value = 0;
        int count = 0;
        for (int element : transposition.getElementsList()) {
            if (value + element > lengthBound) {
                remains += Math.pow(lengthBound - value, 2);
                value = 0;
                count++;
            }
            value += element;
        }

        count++;
        remains += Math.pow(lengthBound - value, 2);
        remains = Math.sqrt(remains);

        if (remains == 0) {
            return count;
        } else {
            return count + 1 / remains;
        }
    }

    private Objective getLengthAndRemains(Transposition transposition, int lengthBound) {
        ArrayList<Integer> remains = new ArrayList<>();
        int value = 0;
        int count = 0;
        for (int element : transposition.getElementsList()) {
            if (value + element > lengthBound) {
                remains.add(lengthBound - value);
                value = 0;
                count++;
            }
            value += element;
        }

        count++;
        remains.add(lengthBound - value);

        Collections.sort(remains, Collections.reverseOrder());

        return new Objective(count, remains);
    }

    public boolean compareTranspositions(Transposition firstTransposition, Transposition secondTransposition, int bugLength) {
        Objective firstObjective = getLengthAndRemains(firstTransposition, bugLength);
        Objective secondObjective = getLengthAndRemains(secondTransposition, bugLength);
        ArrayList<Integer> firstRemains = firstObjective.getRemains();
        ArrayList<Integer> secondRemains = secondObjective.getRemains();
        if (firstObjective.getLength() < secondObjective.getLength()) {
            return true;
        } else if (firstObjective.getLength() > secondObjective.getLength()) {
            return false;
        } else {
            for (int i = 0; i < firstObjective.getRemains().size(); i++) {
                if (firstRemains.get(0) > secondRemains.get(0)) {
                    return true;
                } else if (firstRemains.get(0) < secondRemains.get(0)) {
                    return false;
                }
            }
            return true;
        }
    }
}
