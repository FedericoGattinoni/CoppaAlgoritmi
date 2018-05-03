import Algorithms.ACS;
import Algorithms.Algorithm;
import FileReader.City;
import FileReader.TSPReader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;

public class TuttiGliAlgoritmiACiclo {
    public static void main(String[] args) {
        //-> Itero su tutti i file ciclicamente
        String[] filesName = {
                "fl1577.tsp",
                "pcb442.tsp",
                "pr439.tsp",
                "rat783.tsp",
                "u1060.tsp"
        };


        int[] tourLengths = new int[filesName.length];
        long[] seeds = new long[filesName.length];
        int[] bestSize = new int[filesName.length];
        ArrayList<int[][]> distanceMatrix = new ArrayList<>();
        ArrayList<City> cities[] = new ArrayList[filesName.length];

        TSPReader tspReader = TSPReader.getInstance();


        for (int j = 0; j < filesName.length; j++) {
            cities[j] = tspReader.read(filesName[j]);
            distanceMatrix.add(generateDistanceMatrix(cities[j]));
            bestSize[j] = tspReader.getBestSize();
            tourLengths[j] = Integer.MAX_VALUE;
        }

        int i = 0;
        try {
            PrintStream ps = new PrintStream(new FileOutputStream("nightSeed.txt", true));
            ps.println("--------------------------------------");
            while (true) {
                long seed = System.currentTimeMillis();
                Random random = new Random(seed);

                Algorithms.Algorithm nn = new Algorithms.NearestNeighbour(distanceMatrix.get(i), random);
                ArrayList<Integer> tour = nn.run();
                int nnei = nn.getTourLength();

                double q = 0.7 + (0.95 - 0.7) * random.nextDouble();
                double alpha = 0.7 + (0.90 - 0.7) * random.nextDouble();
                double phi = alpha;
                int numAnts = 1 + random.nextInt((3 - 1) + 1);

                Algorithm acs = new ACS(distanceMatrix.get(i), nnei, random, tour, tspReader.getBestSize(), cities[i], alpha, q, numAnts);
                tour = acs.run();

                if (((ACS) acs).getTourLength() < tourLengths[i]) {
                    seeds[i] = seed;
                    tourLengths[i] = ((ACS) acs).getTourLength();
                    System.out.println(filesName[i] + " best seed: " + seeds[i] + " tour length: " + tourLengths[i] + " phi: " + phi + " alpha: " + alpha + " q: " + q + " numero formiche: " + numAnts);
                    ps.println(filesName[i] + " seed: " + seeds[i] + " tour length: " + tourLengths[i] + " phi: " + phi + " alpha: " + alpha + " q: " + q + " numero formiche: " + numAnts);
                }

                if (tourLengths[i] == bestSize[i]) {
                    System.out.println("Best size for " + filesName[i]);
                    continue;
                }

                i = (i + 1) % filesName.length;
            }
        } catch (IOException ex) {
            System.out.println("Error diring file reading");
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
