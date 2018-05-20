import Algorithms.ACS;
import Algorithms.Algorithm;
import Algorithms.NearestNeighbour;
import Algorithms.TwoOpt;
import FileReader.City;
import FileReader.TSPReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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


        if( args.length < 1 ) {
            System.out.println("Numero parametri non corretti");
            System.exit(-1);
        }

        TSPReader tspReader = TSPReader.getInstance();
        String filePath = args[0];
        String[] slashSplit = filePath.split("/");
        String temp = slashSplit[slashSplit.length-1];
        String[] pointSplit = temp.split("\\.");

        String fileName = pointSplit[0];
        System.out.println("------"+fileName+"-------");

        long seed;
        Random random;
        if(args.length == 2) {
            seed = Long.parseLong(args[1]);
            random = new Random(seed);
        }
        else
            random = new Random();

        ArrayList<FileReader.City> cities = tspReader.read(filePath);
        int[][] distanceMatrix = generateDistanceMatrix(cities);

        Algorithm nn = new NearestNeighbour(distanceMatrix, random);
        ArrayList<Integer> tour = nn.run();
        int nnei = nn.getTourLength();

        Algorithm acs;

        if( args.length == 2 ) {
            double phi = new BigDecimal(0.1 + (0.2 - 0.1) * random.nextDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            double q = new BigDecimal(0.85 + (0.95 - 0.85) * random.nextDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            int numAnts = random.nextInt(2) + 1;

            acs = new ACS(distanceMatrix, nnei, random, tour, tspReader.getBestSize(), cities, phi, q, numAnts);
        }else{
            acs = new ACS(distanceMatrix, nnei, random, tour, tspReader.getBestSize(), cities, 0.1, 0.95, 2);
        }
        tour = acs.run();

        System.out.println("Tour length: " + acs.getTourLength());

        //write on file only if the tour is the best tour possible
        if( acs.getTourLength() == tspReader.getBestSize() ) {
            File file = new File("./OptimalTour/" + fileName + ".opt.tour");
            if (!file.exists()) {
                if (tspReader.write(fileName, tour, acs.getTourLength())) {
                    System.exit(0);
                }
            } else {
                System.exit(0);
            }
        }
    }

    public static int[][] createDistanceMatrix() {
        int[][] distanceMatrix = new int[5][5];

        distanceMatrix[0][0] = 10000;
        distanceMatrix[0][1] = 13;
        distanceMatrix[0][2] = 8;
        distanceMatrix[0][3] = 10;
        distanceMatrix[0][4] = 14;

        distanceMatrix[1][0] = 13;
        distanceMatrix[1][1] = 10000;
        distanceMatrix[1][2] = 4;
        distanceMatrix[1][3] = 7;
        distanceMatrix[1][4] = 5;

        distanceMatrix[2][0] = 8;
        distanceMatrix[2][1] = 4;
        distanceMatrix[2][2] = 10000;
        distanceMatrix[2][3] = 6;
        distanceMatrix[2][4] = 4;

        distanceMatrix[3][0] = 10;
        distanceMatrix[3][1] = 7;
        distanceMatrix[3][2] = 9;
        distanceMatrix[3][3] = 10000;
        distanceMatrix[3][4] = 2;

        distanceMatrix[4][0] = 14;
        distanceMatrix[4][1] = 5;
        distanceMatrix[4][2] = 4;
        distanceMatrix[4][3] = 2;
        distanceMatrix[4][4] = 10000;

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

    public static void printMatrix(final int[][] distance) {
        System.out.println("\n------------Distance Martix------------\n");
        for (int i = 0; i < distance.length; i++) {
            for (int j = 0; j < distance.length; j++) {
                System.out.print(distance[i][j] + " ");
            }
            System.out.println();
        }
    }

    public static int[][] generateDistanceMatrix(ArrayList<City> cities) {
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

    private static void createCandidateList(ArrayList<City> cities, int[][] distanceMatrix, int numElements){
        //per ogni città
        int numCities = distanceMatrix.length;

        ArrayList<Integer> candidateList;

        int maxCities = -1;
        int minValue = Integer.MAX_VALUE;


        for (int i = 0; i < numCities; i++) {
            candidateList = new ArrayList<>();
            for (int k = 0; k < numElements; k++) {

                for (int j = 0; j < numCities; j++) {
                    if( distanceMatrix[i][j] < minValue && !candidateList.contains(j) && i != j ){
                        maxCities = j;
                        minValue = distanceMatrix[i][j];
                    }
                }
                candidateList.add(maxCities);
                maxCities = -1;
                minValue = Integer.MAX_VALUE;
            }
            System.out.println(candidateList);
            cities.get(i).setCandidateList(candidateList);

        }
    }
}
