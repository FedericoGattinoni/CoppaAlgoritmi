package FileReader;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TSPReader {
    private static TSPReader instance;
    private static int bestSize;

    private TSPReader() {
        bestSize = -1;
    }

    public static TSPReader getInstance() {
        if (instance == null) {
            instance = new TSPReader();
        }
        return instance;
    }

    public static ArrayList<City> read(String fileName) {
        ArrayList<City> cities = new ArrayList<>();
        int numberLine = 0;

        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(fileName);
            br = new BufferedReader(fr);

            String line;

            while (!(line = br.readLine()).equals("EOF")) {
                if (numberLine < 7) {
                    if (numberLine == 5) {

                        Pattern pattern = Pattern.compile("BEST_KNOWN : (.*)");
                        Matcher m = pattern.matcher(line);
                        if (m.find()) {
                            bestSize = Integer.parseInt(m.group(1));
                        }
                    }
                    numberLine++;
                    continue;
                }

                String[] parts = line.split(" ");
                City city = new City(Integer.parseInt(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
                cities.add(city);

                numberLine++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            try {
                if (br != null) {
                    br.close();
                }

                if (fr != null) {
                    fr.close();
                }
            } catch (IOException ex) {
            }
        }

        return cities;
    }

    public static boolean write(final String fileName, final ArrayList<Integer> route, final int distance_length) {
        try {

            String fileOutputName = fileName+".opt.tour";
            String fileOutputPath = "./OptimalTour/"+fileOutputName;

            PrintStream ps = new PrintStream(new FileOutputStream(fileOutputPath));

            ps.println("NAME : " + fileOutputName);
            ps.println("COMMENT : Optimum tour for " + fileName + " (" + distance_length + ")");
            ps.println("TYPE : TOUR");
            ps.println("DIMENSION : " + route.size());
            ps.println("TOUR_SECTION");

            for (int i = 0; i < route.size(); i++) {
                ps.println(route.get(i)+1);
            }
            ps.println("EOF");

            System.out.println("Scrittura su file eseguita");
            ps.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static int getBestSize() {
        return bestSize;
    }
}
