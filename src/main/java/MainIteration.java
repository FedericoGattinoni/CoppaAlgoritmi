import Algorithms.ACS;
import Algorithms.Algorithm;
import Algorithms.NearestNeighbour;
import FileReader.City;
import FileReader.TSPReader;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainIteration {

    public static void main(String[] args) {
        final ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
        service.schedule(() -> {
            System.out.println("Fine programma");
            System.out.println("-----------------");
            System.exit(0);
        }, 3, TimeUnit.MINUTES);


        if( args.length < 1 ) {
            System.out.println("Numero parametri non corretti: java -jar CoppaAlgoritmi.jar tspFile -cl candidate list elements (-s seed)");
            System.exit(-1);
        }

        TSPReader tspReader = TSPReader.getInstance();
        String fileName = args[0]; //"./ALGO_cup_2018_problems/u1060.tsp"
        System.out.println(fileName);
        ArrayList<City> cities = tspReader.read(fileName);
        int[][] distanceMatrix = Main.generateDistanceMatrix(cities);

        int candidateListElements = Integer.parseInt(args[1]);
        Main.createCandidateList(cities, distanceMatrix, candidateListElements);

        boolean withSeed;
        if( args[2].equals("YES")) {
            withSeed = true;
            System.out.println("With random parameters");
        }else {
            withSeed = false;
            System.out.println("Without random parameters");
        }

        Random random = new Random();

        long seed = -1;
        if( withSeed ) {
            seed  = System.currentTimeMillis();
            random.setSeed(seed);
        }

        Algorithm nn = new NearestNeighbour(distanceMatrix, random);
        ArrayList<Integer> tour = nn.run();
        int nnei = nn.getTourLength();

        double phi;
        double q;
        int numAnts;

        if( withSeed ) {
            phi = new BigDecimal(0.1 + (0.2 - 0.1) * random.nextDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            q = new BigDecimal(0.85 + (0.95 - 0.85) * random.nextDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            numAnts = random.nextInt(2) + 1;

        }else{
            phi = 0.1;
            q = 0.95;
            numAnts = 2;
        }

        Algorithm acs = new ACS(distanceMatrix, nnei, random, tour, tspReader.getBestSize(), cities, phi, q, numAnts);
        tour = acs.run();

        if (acs.getTourLength() == tspReader.getBestSize()) {
            System.out.println("best size !! length: " + acs.getTourLength());
            if( withSeed )
                System.out.println("Seed: "+seed);
            System.exit(0);
        }

        if( withSeed )
            System.out.println("phi: "+phi+" q0: "+q+" numAnts: "+numAnts);

        System.out.println("Tour length: " + acs.getTourLength());
        if( withSeed )
            System.out.println("Seed: "+seed);
        }
}
