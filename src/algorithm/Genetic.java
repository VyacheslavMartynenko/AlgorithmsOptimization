package algorithm;

import math.Function;
import model.Transposition;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Genetic implements Algorithm {
    private Random random;
    private int bugLength;
    private int numberOfAttempts;
    private ArrayList<Integer> startList;
    private ArrayList<Transposition> population;
    private HashMap<Transposition, Integer> populationWithFitness;

    public Genetic(int numberOfAttempts, int bugLength, ArrayList<Integer> startList) {
        this.numberOfAttempts = numberOfAttempts;
        this.bugLength = bugLength;
        this.startList = startList;
        random = new Random();
    }

    @Override
    public int getBestLength() {
        Transposition startTransposition = new Transposition(startList);
        int count = 0;
        //generate population - 10 transposition
        population = generatePopulation(startTransposition);
        //calculate fitness
        populationWithFitness = calculateFitness(population);
        //repeat
        do {
            //parent selection - 2 transpositions
            ArrayList<Transposition> parents = getParents();
            Transposition firstParent = parents.get(0);
            Transposition secondParent = parents.get(1);
            //children - 2 transpositions by crossover
            Transposition crossoverChild = getCrossoverChild(firstParent, secondParent);

            //children - 2 transpositions by mutate previous
            Transposition mutateChildren = getMutateChild(crossoverChild);
            //calculate children fitness
            ArrayList<Transposition> childPopulation = new ArrayList<>();
            childPopulation.add(mutateChildren);
            HashMap<Transposition, Integer> childrenWithFitness = calculateFitness(childPopulation);
            //replace children in population
            replaceInPopulation(childrenWithFitness, populationWithFitness);
            //until convergence
            count++;
        } while (count < numberOfAttempts);
        return findBest(populationWithFitness);
    }

    private int findBest(HashMap<Transposition, Integer> populationWithFitness) {
        Stream<Map.Entry<Transposition, Integer>> sorted = populationWithFitness.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()));
        Map<Transposition, Integer> sortedPopulation = sorted.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return sortedPopulation.entrySet().iterator().next().getValue();
    }

    private void replaceInPopulation(HashMap<Transposition, Integer> childrenWithFitness, HashMap<Transposition, Integer> populationWithFitness) {
        Stream<Map.Entry<Transposition, Integer>> sorted = populationWithFitness.entrySet().stream()
                .sorted(Map.Entry.comparingByValue());
        Map<Transposition, Integer> sortedPopulation = sorted.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Iterator<Map.Entry<Transposition, Integer>> parentIterator = sortedPopulation.entrySet().iterator();
        Iterator<Map.Entry<Transposition, Integer>> childIterator = childrenWithFitness.entrySet().iterator();

        Map.Entry<Transposition, Integer> child = childIterator.next();
        Map.Entry<Transposition, Integer> parent = parentIterator.next();

        if (child.getValue() < parent.getValue()) {
            populationWithFitness.remove(parent.getKey());
            populationWithFitness.put(child.getKey(), child.getValue());
        }

        this.populationWithFitness = populationWithFitness;
    }

    private Transposition getMutateChild(Transposition crossoverChild) {
        Transposition mutateChild = new Transposition(crossoverChild.getElementsList());

        for (int gen = 0; gen < mutateChild.getElementsList().size(); gen++) {
            if (Math.random() > 0.97) {
                Collections.swap(mutateChild.getElementsList(),
                        random.nextInt(mutateChild.getElementsList().size()),
                        random.nextInt(mutateChild.getElementsList().size()));
            }
        }

        return mutateChild;
    }

    private Transposition getCrossoverChild(Transposition firstParent, Transposition secondParent) {
        Transposition children;
        int gensSize = firstParent.getElementsList().size();
        ArrayList<Integer> firstParentGens = firstParent.getElementsList();
        ArrayList<Integer> secondParentGens = secondParent.getElementsList();
        ArrayList<Integer> childGens = new ArrayList<>();

        for (int genPosition = 0; genPosition < firstParentGens.size() / 2; genPosition++) {
            childGens.add(firstParentGens.get(genPosition));
        }

        for (int gen : secondParentGens) {
            if (!childGens.contains(gen) ||
                    Collections.frequency(childGens, gen) < Collections.frequency(secondParentGens, gen)) {
                childGens.add(gen);
            }

            if (childGens.size() == gensSize) break;
        }

        children = new Transposition(childGens);
        return children;
    }

    private ArrayList<Transposition> getParents() {
        ArrayList<Transposition> parents = new ArrayList<>();
        for (int parent = 0; parent < 2; parent++) {
            parents.add(population.get(random.nextInt(population.size())));
        }
        return parents;
    }

    private HashMap<Transposition, Integer> calculateFitness(ArrayList<Transposition> population) {
        HashMap<Transposition, Integer> populationWithFitness = new HashMap<>();
        for (Transposition parent : population) {
            int fitness = Function.getLength(parent, bugLength);
            populationWithFitness.put(parent, fitness);
        }
        return populationWithFitness;
    }

    private ArrayList<Transposition> generatePopulation(Transposition startTransposition) {
        ArrayList<Transposition> population = new ArrayList<>();

        for (int populationSize = 0; populationSize < 10; populationSize++) {
            Transposition modifiedTransposition = new Transposition(new ArrayList<>(startTransposition.getElementsList()));
            for (int swapCount = 0; swapCount < 2; swapCount++) {
                Collections.swap(modifiedTransposition.getElementsList(),
                        random.nextInt(modifiedTransposition.getElementsList().size()),
                        random.nextInt(modifiedTransposition.getElementsList().size()));
            }
            population.add(modifiedTransposition);
        }

        return population;
    }
}