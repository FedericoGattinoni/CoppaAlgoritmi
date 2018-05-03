package Algorithms;

import java.util.ArrayList;

public class Ant {
    private int id;
    private ArrayList<Integer> tour;
    private int[][] distanceMatrix;
    private ArrayList<Integer> notVisitedCities;

    private int ncities;

    private int tourLength;

    Ant(final int id, final int ncities, final int[][] distanceMatrix) {
        this.id = id;
        tour = new ArrayList<>();
        notVisitedCities = new ArrayList<>();
        this.distanceMatrix = distanceMatrix;
        this.ncities = ncities;

        for (int i = 0; i < ncities; i++) {
            notVisitedCities.add(i);
        }
    }

    public void addCity(int city) {
        if (notVisitedCities.contains(city)) {
            notVisitedCities.remove(notVisitedCities.indexOf(city));
        }

        tour.add(city);
    }

    public ArrayList<Integer> getTour() {
        return tour;
    }

    public ArrayList<Integer> getNotVisitedCities() {
        return notVisitedCities;
    }

    public int getLastCityVisited() {
        int tourSize = tour.size();
        return tour.get(tourSize - 1);
    }

    public int getTourLength() {
        tourLength = 0;
        int next = 1;
        for (int i = 0; i < tour.size(); i++) {
            tourLength += distanceMatrix[tour.get(i)][tour.get(next)];
            next = (next + 1) % tour.size();
        }

        return tourLength;
    }
}
