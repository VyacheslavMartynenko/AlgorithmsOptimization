package algorithm;

import math.Function;
import model.Transposition;

import java.util.*;

public class Genetic implements Algorithm {
    private Random random;
    private int bugLength;
    private int numberOfAttempts;
    private ArrayList<Integer> startList;
    private ArrayList<Transposition> population;
    private HashMap<Transposition, Integer> populationWithFitness;
    private Function function;

    public Genetic(int numberOfAttempts, int bugLength, ArrayList<Integer> startList) {
        this.numberOfAttempts = numberOfAttempts;
        this.bugLength = bugLength;
        this.startList = startList;
        this.random = new Random();
        this.function = new Function();
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
            Transposition firstParent = getParent();
            Transposition secondParent = getParent();
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
        return Collections.min(populationWithFitness.values());
    }

    private void replaceInPopulation(HashMap<Transposition, Integer> childrenWithFitness, HashMap<Transposition, Integer> populationWithFitness) {
        Iterator<Map.Entry<Transposition, Integer>> childIterator = childrenWithFitness.entrySet().iterator();
        Iterator<Map.Entry<Transposition, Integer>> parentIterator = populationWithFitness.entrySet().iterator();
        Map.Entry<Transposition, Integer> child = childIterator.next();

        Transposition key = null;
        int value = Collections.max(populationWithFitness.values());

        while (parentIterator.hasNext()) {
            Map.Entry<Transposition, Integer> element = parentIterator.next();
            if (element.getValue() == value) {
                key = element.getKey();
            }
        }

        if (child.getValue() < value) {
            populationWithFitness.remove(key);
            populationWithFitness.put(child.getKey(), child.getValue());
        }

        this.populationWithFitness = populationWithFitness;
    }

    private Transposition getMutateChild(Transposition crossoverChild) {
        Transposition mutateChild = new Transposition(crossoverChild.getElementsList());
        if (Math.random() > 0.95) {
            Collections.swap(mutateChild.getElementsList(),
                    random.nextInt(mutateChild.getElementsList().size()),
                    random.nextInt(mutateChild.getElementsList().size()));
        }
        return mutateChild;
    }

    private Transposition getCrossoverChild(Transposition firstParent, Transposition secondParent) {
        Transposition children;
        int gensSize = firstParent.getElementsList().size();
        ArrayList<Integer> firstParentGens = firstParent.getElementsList();
        ArrayList<Integer> secondParentGens = secondParent.getElementsList();
        ArrayList<Integer> childGens = new ArrayList<>();

        int start = random.nextInt(gensSize);
        int finish = random.nextInt(gensSize);

        for (int position = start; position < finish; position++) {
            childGens.add(firstParentGens.get(position));
        }
        for (int position = finish; position < gensSize; position++) {
            int gen = secondParentGens.get(position);
            if (!childGens.contains(gen) ||
                    Collections.frequency(childGens, gen) < Collections.frequency(secondParentGens, gen)) {
                childGens.add(gen);
            }
        }
        for (int position = 0; position < start; position++) {
            int gen = secondParentGens.get(position);
            if (!childGens.contains(gen) ||
                    Collections.frequency(childGens, gen) < Collections.frequency(secondParentGens, gen)) {
                childGens.add(gen);
            }
        }

        children = new Transposition(childGens);
        return children;
    }

    private Transposition getParent() {
        ArrayList<Transposition> randomParents = new ArrayList<>();
        for (int parent = 0; parent < 8; parent++) {
            randomParents.add(population.get(random.nextInt(population.size())));
        }
        Collections.sort(randomParents, new TranspositionComparator());
        return randomParents.get(0);
    }

    private HashMap<Transposition, Integer> calculateFitness(ArrayList<Transposition> population) {
        HashMap<Transposition, Integer> populationWithFitness = new HashMap<>();
        for (Transposition parent : population) {
            int fitness = function.getLength(parent, bugLength);
            populationWithFitness.put(parent, fitness);
        }
        return populationWithFitness;
    }

    private ArrayList<Transposition> generatePopulation(Transposition startTransposition) {
        ArrayList<Transposition> population = new ArrayList<>();

        for (int populationSize = 0; populationSize < 1000; populationSize++) {
            Transposition modifiedTransposition = new Transposition(new ArrayList<>(startTransposition.getElementsList()));
            for (int swapCount = 0; swapCount < 1000; swapCount++) {
                Collections.swap(modifiedTransposition.getElementsList(),
                        random.nextInt(modifiedTransposition.getElementsList().size()),
                        random.nextInt(modifiedTransposition.getElementsList().size()));
            }
            population.add(modifiedTransposition);
        }

        return population;
    }

    private class TranspositionComparator implements Comparator<Transposition> {
        public int compare(Transposition t1, Transposition t2) {
            return Integer.valueOf(function.getLength(t1, bugLength)).compareTo(function.getLength(t2, bugLength));
        }
    }
}