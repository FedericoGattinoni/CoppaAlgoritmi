package Algorithms;

import java.util.ArrayList;
import java.util.Collections;

public class Random {
    private int tourLength;

    Random(final int tourLength) {
        this.tourLength = tourLength;
    }


    ArrayList<Integer> runAlgorithm() {
        ArrayList<Integer> tour = new ArrayList<>();

        for (int i = 0; i < tourLength; i++) {
            tour.add(i);
        }

        Collections.shuffle(tour);
        return tour;
    }
}
