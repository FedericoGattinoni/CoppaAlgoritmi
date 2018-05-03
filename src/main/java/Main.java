import Algorithms.ACS;
import Algorithms.Algorithm;
import Algorithms.NearestNeighbour;
import Algorithms.TwoOpt;
import FileReader.City;
import FileReader.TSPReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(() -> {
            System.out.println("Fine programma");
            System.exit(0);
        }, 3, TimeUnit.MINUTES);

        TSPReader tspReader = TSPReader.getInstance();
        String fileName = "ch130.tsp";
        ArrayList<FileReader.City> cities = tspReader.read(fileName);
        int[][] distanceMatrix = generateDistanceMatrix(cities);

        Random random = new Random(1525369657014l);

        Algorithm nn = new NearestNeighbour(distanceMatrix, random);
        ArrayList<Integer> tour = nn.run();
        int nnei = nn.getTourLength();

        double phi = new BigDecimal(0.1 + (0.2 - 0.1) * random.nextDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        double q = new BigDecimal(0.85 + (0.95 - 0.85) * random.nextDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        int numAnts = random.nextInt(2) + 1;

        Algorithm acs = new ACS(distanceMatrix, nnei, random, tour, tspReader.getBestSize(), cities, phi, q, numAnts);
        tour = acs.run();

        if (acs.getTourLength() == tspReader.getBestSize()) {
            System.out.println("best size !! length: " + acs.getTourLength());
        }
        System.out.println("Tour length: " + acs.getTourLength());

        System.exit(0);

        /*
        if (tspReader.write("ch130.opt.tour", "ch130", tour, acs.getTourLength())) {
            System.exit(0);
        }*/
    }

    private static boolean checkIntegrity(ArrayList<Integer> tour) {
        ArrayList<Integer> present = new ArrayList<>();

        for (int i = 0; i < tour.size(); i++) {
            if (present.contains(tour.get(i))) {
                System.out.println("città già presente: " + tour.get(i));
                return false;
            }
            present.add(tour.get(i));
        }
        return true;
    }

    private static int tourLength(ArrayList<Integer> tour, final int[][] distanceMatrix) {
        int tourLength = 0;
        int next = 1;
        for (int i = 0; i < tour.size(); i++) {
            tourLength += distanceMatrix[tour.get(i)][tour.get(next)];
            next = (next + 1) % tour.size();
        }

        return tourLength;
    }

    private static int[][] createDistanceMatrix() {
        int[][] distanceMatrix = new int[5][5];

        distanceMatrix[0][0] = 100;
        distanceMatrix[0][1] = 13;
        distanceMatrix[0][2] = 8;
        distanceMatrix[0][3] = 10;
        distanceMatrix[0][4] = 14;

        distanceMatrix[1][0] = 13;
        distanceMatrix[1][1] = 100;
        distanceMatrix[1][2] = 4;
        distanceMatrix[1][3] = 7;
        distanceMatrix[1][4] = 5;

        distanceMatrix[2][0] = 8;
        distanceMatrix[2][1] = 4;
        distanceMatrix[2][2] = 100;
        distanceMatrix[2][3] = 6;
        distanceMatrix[2][4] = 4;

        distanceMatrix[3][0] = 10;
        distanceMatrix[3][1] = 7;
        distanceMatrix[3][2] = 9;
        distanceMatrix[3][3] = 100;
        distanceMatrix[3][4] = 2;

        distanceMatrix[4][0] = 14;
        distanceMatrix[4][1] = 5;
        distanceMatrix[4][2] = 4;
        distanceMatrix[4][3] = 2;
        distanceMatrix[4][4] = 100
        ;
        return distanceMatrix;
    }

    private static void printTour(ArrayList<Integer> tour) {
        System.out.println("\n------------Tour------------");
        for (int i = 0; i < tour.size(); i++) {
            //System.out.print(tour.get(i) + " -> ");
            System.out.print(tour.get(i) + "(" + i + ") -> ");
        }
        System.out.println("\n----------------------------");
    }

    private static void printMatrix(final int[][] distance) {
        System.out.println("\n------------Distance Martix------------\n");
        for (int i = 0; i < distance.length; i++) {
            for (int j = 0; j < distance.length; j++) {
                System.out.print(distance[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static int[][] generateDistanceMatrix(ArrayList<City> cities) {
        int citiesNumber = cities.size();
        int distanceMatrix[][] = new int[citiesNumber][citiesNumber];

        for (int i = 0; i < citiesNumber; i++) {
            for (int j = i; j < citiesNumber; j++) {
                if (i == j) {
                    distanceMatrix[i][j] = Integer.MAX_VALUE;
                    distanceMatrix[j][i] = Integer.MAX_VALUE;
                    continue;
                }

                double distance = Math.sqrt(Math.pow(cities.get(i).getLat() - cities.get(j).getLat(), 2) + Math.pow(cities.get(i).getLon() - cities.get(j).getLon(), 2));
                distanceMatrix[i][j] = (int) (distance + 0.5);
                distanceMatrix[j][i] = (int) (distance + 0.5);
            }
        }
        return distanceMatrix;
    }
}
