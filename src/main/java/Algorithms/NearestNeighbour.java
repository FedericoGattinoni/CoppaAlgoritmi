package Algorithms;

import java.util.ArrayList;
import java.util.Random;

public class NearestNeighbour implements Algorithm {
    private Random random;
    private int tourLength = 0;
    private int[][] distanceMatrix;


    public NearestNeighbour(final int[][] distanceMatrix, final Random random) {
        this.distanceMatrix = distanceMatrix;
        this.random = random;
    }

    @Override
    public int getTourLength() {
        return tourLength;
    }

    @Override
    public ArrayList<Integer> run() {
        //System.out.println("--- Nearest Neighbour ---");
        ArrayList<Integer> tour = new ArrayList<>();

        int current = random.nextInt(distanceMatrix.length);
        int minimum = Integer.MAX_VALUE;
        int index = -1;

        tour.add(current);

        while (tour.size() < distanceMatrix.length) {
            for (int i = 0; i < distanceMatrix.length; i++) {
                if (distanceMatrix[current][i] < minimum && !tour.contains(i)) {
                    index = i;
                    minimum = distanceMatrix[current][i];
                }
            }
            tour.add(index);
            tourLength += distanceMatrix[current][index];
            current = index;
            minimum = Integer.MAX_VALUE;
        }

        tourLength += distanceMatrix[current][tour.get(0)];

        return tour;
    }
}
