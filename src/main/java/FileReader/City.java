package FileReader;

import java.util.ArrayList;

public class City {
    private int id;
    private double lat;
    private double lon;
    ArrayList<Integer> candidateList;

    public City(final int id, final double lat, final double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.candidateList = new ArrayList<>();
    }

    public City(int id) {
        this.id = id;
        this.candidateList = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public ArrayList<Integer> getCandidateList() {
        return candidateList;
    }

    public void setCandidateList(ArrayList<Integer> candidateList){
        this.candidateList = candidateList;
    }
}
