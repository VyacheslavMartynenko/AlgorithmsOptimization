package model;

import java.util.ArrayList;

public class Objective {
    private int length;
    private ArrayList<Integer> remains;

    public Objective(int length, ArrayList<Integer> remains) {
        this.length = length;
        this.remains = remains;
    }

    public int getLength() {
        return length;
    }

    public ArrayList<Integer> getRemains() {
        return remains;
    }
}
