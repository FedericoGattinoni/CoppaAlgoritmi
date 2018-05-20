package Algorithms;

import java.util.ArrayList;
import java.util.Collections;

public class TwoOpt implements Algorithm {
    private ArrayList<Integer> tour;
    private int[][] distanceMatrix;
    private int tourLength;
    private int ncities;

    public TwoOpt(final ArrayList<Integer> tour, final int[][] distanceMatrix, final int tourLength) {
        this.tour = tour;
        this.distanceMatrix = distanceMatrix;
        this.tourLength = tourLength;
        ncities = distanceMatrix.length;
    }

    @Override
    public int getTourLength() {
        return tourLength;
    }

    @Override
    public ArrayList<Integer> run() {
        int bestGain = -1;
        int best_i = -1, best_j = -1;
        int gain;

        while (bestGain < 0) {
            bestGain = 0;
            for (int i = 0; i < ncities; i++) {
                for (int j = (i + 2); j < ncities; j++) {
                    gain = computeGain(i, j);

                    if (gain < bestGain) {
                        bestGain = gain;
                        best_i = i;
                        best_j = j;
                        //break;
                    }
                }
            }

            if (bestGain < 0) {
                //System.out.println("best gain i: " + best_i + " j: " + best_j + " => best gain: " + bestGain);
                exchange(best_i, best_j);
            }
        }
        setTourLength();
        return tour;
    }

    private int computeGain(final int i, final int j) {
        int next_i = (i + 1) % ncities;
        int next_j = (j + 1) % ncities;

        int removed = distanceMatrix[tour.get(i)][tour.get(next_i)] + distanceMatrix[tour.get(j)][tour.get(next_j)];

        int added = distanceMatrix[tour.get(i)][tour.get(j)] + distanceMatrix[tour.get(next_i)][tour.get(next_j)];

        return added - removed;
    }

    private void exchange(int best_i, int best_j) {
        int next_i = (best_i + 1) % tour.size();

        int a = next_i;
        int b = best_j;

        while (a < b && a != b) {
            Collections.swap(tour, a, b);
            a++;
            b--;
        }
    }

    private void setTourLength() {
        tourLength = 0;
        int next = 1;
        for (int i = 0; i < tour.size(); i++) {
            tourLength += distanceMatrix[tour.get(i)][tour.get(next)];
            next = (next + 1) % tour.size();
        }
    }
}