package algorithm;

import math.Function;
import model.Transposition;

import java.util.*;

public class Genetic implements Algorithm {
    private static final int POPULATION_SIZE = 100;
    //public static final int COUNT_OF_TOURNIR_CANDIDATE = 8;
    private Random random;
    private int bugLength;
    private int numberOfAttempts;
    private ArrayList<Integer> rankMap;
    private ArrayList<Integer> startList;
    private ArrayList<Transposition> population;
    private List<FitnessDecision> populationWithFitness;
    private Function function;

    private Comparator<FitnessDecision> fitnessDecisionComparator;

    public Genetic(int numberOfAttempts, int bugLength, ArrayList<Integer> startList) {
        this.numberOfAttempts = numberOfAttempts;
        this.bugLength = bugLength;
        this.startList = startList;
        this.random = new Random();
        this.function = new Function();

        fitnessDecisionComparator = Comparator.comparing(x -> x.fitness);
        fitnessDecisionComparator = fitnessDecisionComparator.reversed();

        this.rankMap = new ArrayList<>(POPULATION_SIZE);
    }

    @Override
    public int getBestLength() {

        int sumOfRank = calcSumOfRank(POPULATION_SIZE);

        Transposition startTransposition = new Transposition(startList);
        int count = 0;
        //generate population - N transposition
        population = generatePopulation(startTransposition);
        //calculate fitness
        populationWithFitness = calculateFitness(population);

        System.out.println("start:" + findBest(populationWithFitness)
                + "-" + "\t" + findWorst(populationWithFitness));
        do {
            //parent selection - 2 transpositions
            List<Transposition> list = getRankedRandomDecision(populationWithFitness, sumOfRank);
            Transposition firstParent = list.get(0);
            Transposition secondParent = list.get(1);

            Transposition crossoverChild = getCrossoverChild(firstParent, secondParent);
            Transposition secondCrossoverChild = getCrossoverChild(secondParent, firstParent);

            Transposition mutateChildren = getMutateChild(crossoverChild);
            Transposition secondMutateChildren = getMutateChild(secondCrossoverChild);

            ArrayList<Transposition> childPopulation = new ArrayList<>();
            childPopulation.add(mutateChildren);
            childPopulation.add(secondMutateChildren);
            List<FitnessDecision> childrenWithFitness = calculateFitness(childPopulation);

            replaceInPopulation(childrenWithFitness, populationWithFitness);

            count++;
        } while (count < numberOfAttempts);


        System.out.println("end:" + findBest(populationWithFitness)
                + "-" + "\t" + findWorst(populationWithFitness));

        return findBest(populationWithFitness);
    }

    private int findBest(List<FitnessDecision> populationWithFitness) {
        populationWithFitness.sort(fitnessDecisionComparator);
        Transposition key = populationWithFitness.get(POPULATION_SIZE - 1).decision;
        return function.getLength(key, bugLength);
    }

    private int findWorst(List<FitnessDecision> populationWithFitness) {
        populationWithFitness.sort(fitnessDecisionComparator);
        Transposition key = populationWithFitness.get(0).decision;
        return function.getLength(key, bugLength);
    }

    private void replaceInPopulation(List<FitnessDecision> childrenWithFitness, List<FitnessDecision> populationWithFitness) {
        for (FitnessDecision childrenWithFitnes : childrenWithFitness) {
            populationWithFitness.sort(fitnessDecisionComparator);
            populationWithFitness.set(0, childrenWithFitnes);
        }
        this.populationWithFitness = populationWithFitness;
    }

    private Transposition getMutateChild(Transposition crossoverChild) {
        Transposition mutateChild = new Transposition(crossoverChild.getElementsList());
        if (Math.random() > 0.80) {
            Collections.swap(mutateChild.getElementsList(),
                    random.nextInt(mutateChild.getElementsList().size()),
                    random.nextInt(mutateChild.getElementsList().size()));
        }
        return mutateChild;
    }

    private Transposition getCrossoverChild(Transposition firstParent, Transposition secondParent) {
        final Random random = new Random();
        Transposition children;
        int gensSize = firstParent.getElementsList().size();
        ArrayList<Integer> firstParentGens = firstParent.getElementsList();
        ArrayList<Integer> secondParentGens = secondParent.getElementsList();
        Integer childGens[] = new Integer[gensSize];

        int start = random.nextInt(gensSize / 2);
        int finish = gensSize / 2 + random.nextInt(gensSize - gensSize / 2);

        for (int position = start; position < finish; position++) {
            childGens[position] = firstParentGens.get(position);
        }

        int index = finish;
        for (int position = finish; position < gensSize; position++) {
            int gen = secondParentGens.get(position);
            if (index == gensSize) {
                index = 0;
            }
            if (!Arrays.asList(childGens).contains(gen) ||
                    Collections.frequency(Arrays.asList(childGens), gen) < Collections.frequency(secondParentGens, gen)) {
                childGens[index] = gen;
                index++;
            }
        }

        for (int position = 0; position < finish; position++) {
            int gen = secondParentGens.get(position);
            if (index == gensSize) {
                index = 0;
            }
            if (!Arrays.asList(childGens).contains(gen) ||
                    Collections.frequency(Arrays.asList(childGens), gen) < Collections.frequency(secondParentGens, gen)) {
                childGens[index] = gen;
                index++;
            }
        }

        List<Integer> list = Arrays.asList(childGens);
        ArrayList<Integer> gensList = new ArrayList<>();
        gensList.addAll(list);
        children = new Transposition(gensList);
        return children;
    }

//    private Transposition getParent() {
//        ArrayList<Transposition> randomParents = new ArrayList<>();
//        for (int parent = 0; parent < 8; parent++) {
//            randomParents.add(population.get(random.nextInt(population.size())));
//        }
//        Collections.sort(randomParents, new TranspositionComparator());
//        return randomParents.get(0);
//    }

    private List<FitnessDecision> calculateFitness(ArrayList<Transposition> population) {
        List<FitnessDecision> populationWithFitness = new ArrayList<>(POPULATION_SIZE);
        for (Transposition parent : population) {
            double fitness = function.getLengthWithRemains(parent, bugLength);
            populationWithFitness.add(new FitnessDecision(parent, fitness));
        }
        return populationWithFitness;
    }

    private ArrayList<Transposition> generatePopulation(Transposition startTransposition) {
        ArrayList<Transposition> population = new ArrayList<>();

        for (int populationSize = 0; populationSize < POPULATION_SIZE; populationSize++) {
            Transposition modifiedTransposition = new Transposition(new ArrayList<>(startTransposition.getElementsList()));
            Collections.shuffle(modifiedTransposition.getElementsList());
            population.add(modifiedTransposition);
        }

        return population;
    }

    private List<Transposition> getRankedRandomDecision(List<FitnessDecision> population, int sumOfRank) {
        List<Transposition> list = new ArrayList<>();
        int topIndex1 = getTopIndex(sumOfRank);
        int topIndex2 = getTopIndex(sumOfRank, topIndex1);

        Transposition first = population.get(topIndex1).decision;
        Transposition second = population.get(topIndex2).decision;

        list.add(first);
        list.add(second);

        return list;
    }

    private int indexNodeByRank(int rank) {
        for (int i = 0; i < rankMap.size(); i++) {
            if (rank < rankMap.get(i)) {
                return i;
            }
        }
        return -1;
    }

    private int calcSumOfRank(int populationSize) {
        int sumOfRank;

        if (populationSize == 1) {
            sumOfRank = populationSize;
        } else {
            sumOfRank = populationSize + calcSumOfRank(populationSize - 1);
        }

        rankMap.add(sumOfRank);

        return sumOfRank;
    }

    private int getTopIndex(int sumOfRank, int disallowedIndex) {
        int topIndex;

        do {
            topIndex = getTopIndex(sumOfRank);
        } while (topIndex == disallowedIndex);

        return topIndex;
    }

    private int getTopIndex(int sumOfRank) {
        return indexNodeByRank(random.nextInt(sumOfRank));
    }

//    private class TranspositionComparator implements Comparator<Transposition> {
//        public int compare(Transposition t1, Transposition t2) {
//            double firstLength = function.getLengthWithRemains(t1, bugLength);
//            double secondLength = function.getLengthWithRemains(t2, bugLength);
//            if (firstLength < secondLength) {
//                return -1;
//            } else if (firstLength > secondLength) {
//                return 1;
//            } else {
//                return 0;
//            }
//        }
//    }

    private final static class FitnessDecision {
        private Transposition decision;
        private Double fitness;

        private FitnessDecision(Transposition decision, Double fitness) {
            this.decision = decision;
            this.fitness = fitness;
        }

        @Override
        public String toString() {
            return decision.toString() + "(" + fitness + ")";
        }
    }

}