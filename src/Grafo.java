import java.util.*;

public class Grafo {
    private DataBase airportDb;
    private HashMap<String, HashMap<String, Double>> airportFork;
    private Vector<String> airportIataVec;
    private Vector<String> citiesVec;
    private Vector<String> statesVec;

    public Vector<String> getStatesVec() {
        return statesVec;
    }

    public Vector<String> getCitiesVec() {
        return citiesVec;
    }

    public Vector<String> getAirportIataVec() {
        return airportIataVec;
    }

    public Grafo() {
        connectToDb();
        initializeAirportIataVec();
        initializeCitiesVec();
        initializeStatesVec();
        initializeAirportFork();
    }

    public void connectToDb() {
        //Conectando ao MySQL
        airportDb = new DataBase();
        airportDb.connectToMysql();
    }

    public void initializeAirportIataVec() {
        airportIataVec = airportDb.getCollumn("iata");
    }

    public void initializeCitiesVec() {
        citiesVec = airportDb.getCollumn("cidade");
    }

    public void initializeStatesVec() {
        statesVec = airportDb.getCollumn("estado");
    }

    public void initializeAirportFork() {
        Vector<String> airportLatitudeVec = airportDb.getCollumn("latitude");
        Vector<String> airportLongitudeVec = airportDb.getCollumn("longitude");

        airportFork = new HashMap<>();

        for (int airportOrig = 0; airportOrig < airportIataVec.size(); airportOrig++) {
            HashMap<String, Double> aux = new HashMap<>();
            for (int airportDest = 0; airportDest < airportIataVec.size(); airportDest++) {
                aux.put(airportIataVec.get(airportDest),
                        calculateDistance(Double.parseDouble(airportLatitudeVec.get(airportOrig)), Double.parseDouble(airportLongitudeVec.get(airportOrig)),
                                Double.parseDouble(airportLatitudeVec.get(airportDest)), Double.parseDouble(airportLongitudeVec.get(airportDest))));

            }
            HashMap<String, Double> auxCopy = new HashMap<>(aux);
            airportFork.put(airportIataVec.get(airportOrig), auxCopy);
            aux.clear();
        }
    }

    public Double calculateDistance(Double latOrig, Double longOrig, Double latDest, Double longDest) {
        double radius = 6378.1;
        latOrig = Math.toRadians(latOrig);
        longOrig = Math.toRadians(longOrig);
        latDest = Math.toRadians(latDest);
        longDest = Math.toRadians(longDest);

        double latDif = latDest - latOrig;
        double longDif = longDest - longOrig;

        double AlfaSin = Math.sqrt(Math.pow(Math.sin(latDif / 2.0), 2) + Math.cos(latOrig) * Math.pow(Math.sin(longDif / 2), 2) * Math.cos(latDest));

        return 2 * Math.asin(AlfaSin) * radius;
    }

    public HashMap<String, List<String>> initializeShortestPath(String orig) {
        HashMap<String, List<String>> shortestPath = new HashMap<>();
        for (String airport : airportIataVec) {
            shortestPath.put(airport, Arrays.asList(orig));
        }
        return shortestPath;
    }

    public HashMap<String, Double> initializeDistanceMap() {
        HashMap<String, Double> distanceMap = new HashMap<>();
        for (String airport : airportIataVec) {
            distanceMap.put(airport, Double.MAX_VALUE);
        }
        return distanceMap;
    }

    public String dijkstra(String orig, String dest) {
        HashMap<String, List<String>> shortestPath = initializeShortestPath(orig);

        PriorityQueue<AbstractMap.SimpleEntry<Double, String>> dijQueue = new PriorityQueue<>((a, b) -> {
            return Double.compare(a.getKey(), b.getKey());
        });

        dijQueue.add(new AbstractMap.SimpleEntry<Double, String>(0.0, orig));

        HashMap<String, Double> distanceMap = initializeDistanceMap();

        distanceMap.put(orig, 0.0);

        while (!dijQueue.isEmpty()) {
            AbstractMap.SimpleEntry<Double, String> simpleDist = dijQueue.remove();
            if (simpleDist.getKey() < -distanceMap.get(simpleDist.getValue()))
                continue;
            for (String currentAp : airportIataVec) {
                HashMap<String, Double> nextAp = airportFork.get(simpleDist.getValue());
                Double dist = nextAp.get(currentAp);
                if (currentAp.equals(dest) && simpleDist.getValue().equals(orig))
                    continue; // ignorar a ligação direta entre orig e dest

                if (distanceMap.get(currentAp) > distanceMap.get(simpleDist.getValue()) + dist) {

                    List<String> auxPath = shortestPath.get(simpleDist.getValue());

                    if (!auxPath.contains(simpleDist.getValue())) auxPath.add(simpleDist.getValue());

                    shortestPath.put(currentAp, new ArrayList<>(auxPath));

                    distanceMap.put(currentAp, distanceMap.get(simpleDist.getValue()) + dist);
                    dijQueue.add(new AbstractMap.SimpleEntry<Double, String>(-distanceMap.get(currentAp), currentAp));
                }
            }
        }

        String path = "";
        for (String airport : shortestPath.get(dest))
            path += airport + " -> ";
        path += dest;

        airportDb.writeData("airportpaths", path);

        return path;
    }
}

