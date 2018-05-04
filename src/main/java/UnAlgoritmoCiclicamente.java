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

public class UnAlgoritmoCiclicamente {
    public static void main(String[] args) {
        //-> Itero su un file per un tot di tempo
        int tourLength = Integer.MAX_VALUE;

        TSPReader tspReader = TSPReader.getInstance();
        String fileName = "pr439.tsp";
        ArrayList<City> cities = tspReader.read(fileName);
        int bestSize = tspReader.getBestSize();
        System.out.println(fileName + " -> best size = " + bestSize);

        int[][] distanceMatrix = generateDistanceMatrix(cities);
        try {
            PrintStream ps = new PrintStream(new FileOutputStream("seed_" + fileName + ".txt", true));
            ps.println("--------------------------------------");
            while (true) {
                long seed = System.currentTimeMillis();
                Random random = new Random(seed);

                Algorithm nn = new NearestNeighbour(distanceMatrix, random);
                ArrayList<Integer> tour = nn.run();
                int nnei = nn.getTourLength();

                double phi = new BigDecimal(0.1 + (0.2 - 0.1) * random.nextDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                double q = new BigDecimal(0.85 + (0.95 - 0.85) * random.nextDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                int numAnts = random.nextInt(2) + 1;

                Algorithm acs = new ACS(distanceMatrix, nnei, random, tour, tspReader.getBestSize(), cities, phi, q, numAnts);
                tour = acs.run();

                if ((acs.getTourLength() < tourLength)) {
                    tourLength = acs.getTourLength();
                    System.out.println("seed: " + seed + " tour length: " + tourLength + " phi: " + phi + " alpha: " + phi + " q: " + q + " numero formiche: " + numAnts);
                    ps.println("seed: " + seed + " tour length: " + tourLength + " phi: " + phi + " alpha: " + phi + " q: " + q + " numero formiche: " + numAnts);
                }


                if (tourLength == bestSize) {
                    System.out.println("Best size !!");
                    System.exit(0);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
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
