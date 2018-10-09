package Algorithms;

import FileReader.City;

import java.util.*;
import java.util.Random;

public class ACS implements Algorithm {
    private Random random;
    private int[][] distanceMatrix;
    private double[][] pheromoneMatrix;
    private double t0;
    private int ncities;
    private ArrayList<Integer> bestTour;
    private int bestSize;
    private int bestSizeKnown;
    private double alpha;
    private double phi;
    private double q0;
    private int ANTS_NUMBER;

    public ACS(final int[][] distanceMatrix, int nnei, final Random random, final ArrayList<Integer> nnTour, final int bestSizeKnown, ArrayList<City> cities, final double alpha, final double q0, final int numAnts) {
        this.random = random;
        this.distanceMatrix = distanceMatrix;
        ncities = cities.size();
        pheromoneMatrix = new double[ncities][ncities];
        bestSize = Integer.MAX_VALUE;
        bestTour = new ArrayList<>(nnTour);
        this.alpha = alpha;
        phi = alpha;
        this.q0 = q0;
        ANTS_NUMBER = numAnts;

        if (bestSizeKnown == -1) {
            System.out.println("Best size known wrong!!");
        }
        this.bestSizeKnown = bestSizeKnown;

        t0 = 1 / ((double) nnei * (double) ncities);
        for (int i = 0; i < ncities; i++) {
            for (int j = 0; j < ncities; j++) {
                if (i == j) {
                    pheromoneMatrix[i][j] = 0;
                    continue;
                }
                pheromoneMatrix[i][j] = t0;
            }
        }
    }

    @Override
    public ArrayList<Integer> run() {
        //System.out.println("--- ACS ---");
        long start = System.currentTimeMillis();
        long stop = System.currentTimeMillis();

        while ((stop - start) < 165000) {
            Ant[] ants = new Ant[ANTS_NUMBER];
            //create ANT population
            for (int i = 0; i < ANTS_NUMBER; i++) {
                ants[i] = new Ant(i, ncities, distanceMatrix);
                int starterCity = random.nextInt(ncities);
                ants[i].addCity(starterCity);
            }

            for (int c = 0; c < ncities - 1; c++) {
                for (int a = 0; a < ANTS_NUMBER; a++) {
                    int nextCity;

                    //apply the state transition rule
                    float q = random.nextFloat();

                    //exploitation
                    if (q < q0) {
                        nextCity = exploitation(ants[a]);
                    }
                    //exploration
                    else {
                        nextCity = exploration(ants[a]);
                    }

                    //apply the local trail updating rule
                    localTrailUpdating(ants[a].getLastCityVisited(), nextCity);
                    ants[a].addCity(nextCity);
                }
            }
            //apply local search  (2-opt/ 3-opt)
            ArrayList<Integer> antTours[] = new ArrayList[ANTS_NUMBER];
            Integer toursSize[] = new Integer[ANTS_NUMBER];

            for (int i = 0; i < ANTS_NUMBER; i++) {
                Algorithm to = new TwoOpt(ants[i].getTour(), distanceMatrix, ants[i].getTourLength());
                antTours[i] = to.run();
                toursSize[i] = to.getTourLength();
            }

            //apply the global trail updating rule
            int best = getBestTour(antTours, toursSize);

            if (toursSize[best] < bestSize) {
                Collections.copy(bestTour, antTours[best]);
                bestSize = toursSize[best];
            }

            double tBest = 1 / (double) bestSize;
            globalTrailUpdating(bestTour, tBest);

            if (bestSize == bestSizeKnown) {
                return bestTour;
            }
            stop = System.currentTimeMillis();
        }

        return bestTour;
    }

    @Override
    public int getTourLength() {
        return bestSize;
    }

    private int getBestTour(ArrayList<Integer> ants[], Integer toursSize[]) {
        int bestSize = Integer.MAX_VALUE;
        int bestTour = -1;

        for (int i = 0; i < ants.length; i++) {
            if (toursSize[i] < bestSize) {
                bestSize = toursSize[i];
                bestTour = i;
            }
        }

        return bestTour;
    }

    private int exploration(Ant ant) {
        //int exploitation = exploitation(ant);

        //implement exploration
        double totalDistance = 0;
        int lastCityVisited = ant.getLastCityVisited();

        ArrayList<Integer> notVisitedCities = ant.getNotVisitedCities();
        Map<Integer, Double> cityProbability = new HashMap<>();

        if (notVisitedCities.size() == 1) {
            return notVisitedCities.get(0);
        }

        //per ora dalle probabilità escludo anche quello con probabilità più alta(come fare exploitation), se riduco il parametro soglia toglierò l'if
        for (int i = 0; i < notVisitedCities.size(); i++) {
            int city = notVisitedCities.get(i);
            //if (city != exploitation) {
            double probability = pheromoneMatrix[city][lastCityVisited] * (1 / (double) distanceMatrix[city][lastCityVisited]);
            totalDistance += probability;
            cityProbability.put(city, probability);
            //}
        }

        double randProbability = random.nextDouble();
        double incrementProbability = 0;

        for (Map.Entry<Integer, Double> entry : cityProbability.entrySet()) {
            incrementProbability += entry.getValue() / totalDistance;
            if (randProbability < incrementProbability) {
                return entry.getKey();
            }
            return entry.getKey();
        }
        return -1;
    }

    private int exploitation(Ant ant) {
        double max = Integer.MIN_VALUE;
        int candidateCity = -1;
        int lastCityVisited = ant.getLastCityVisited();
        ArrayList<Integer> notVisitedCities = ant.getNotVisitedCities();

        for (int i = 0; i < notVisitedCities.size(); i++) {
            Integer city = notVisitedCities.get(i);
            double distance = pheromoneMatrix[city][lastCityVisited] * (1 / (double) distanceMatrix[city][lastCityVisited]);
            if (distance > max) {
                max = distance;
                candidateCity = city;
            }
        }

        return candidateCity;
    }

    private void globalTrailUpdating(final ArrayList<Integer> bestTour, final double tBest) {
        int next = 1;
        for (int i = 0; i < bestTour.size(); i++) {
            pheromoneMatrix[bestTour.get(i)][bestTour.get(next)] = (1 - alpha) * pheromoneMatrix[bestTour.get(i)][bestTour.get(next)] + alpha * tBest;
            next = (next + 1) % bestTour.size();
        }
    }

    private void localTrailUpdating(final int currentCity, final int nextCity) {
        pheromoneMatrix[currentCity][nextCity] = (1 - phi) * pheromoneMatrix[currentCity][nextCity] + phi * t0;
    }
}
