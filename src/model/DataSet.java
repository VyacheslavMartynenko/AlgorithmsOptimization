package model;

import java.util.ArrayList;

public class DataSet {
    public int container;
    public int elements;
    public int solution;
    public ArrayList<Integer> data;

    public DataSet(int container, int elements, int solution, ArrayList<Integer> data) {
        this.container = container;
        this.elements = elements;
        this.solution = solution;
        this.data = data;
    }
}
