import java.util.Scanner;
import java.util.Vector;

public class Menu {
    private Grafo mainFork;
    private Vector<String> airportVec;

    public Menu() {
        mainFork = new Grafo();
        Vector<String> airportVec = mainFork.getAirportIataVec();
    }

    public void initialMessage() {
        System.out.println("List of Airports:");
        airportVec = mainFork.getAirportIataVec();
        Vector<String> citiesVec = mainFork.getCitiesVec();
        Vector<String> statesVec = mainFork.getStatesVec();
        for (int i = 0; i < airportVec.size(); i++) {
            System.out.print(airportVec.get(i));
            System.out.print(" - ");
            System.out.print(citiesVec.get(i));
            System.out.print(" - ");
            System.out.println(statesVec.get(i));
        }
    }

    public void terminal() {
        String ans = "";
        String orig = "";
        String dest = "";
        Scanner userInput = new Scanner(System.in);

        while (!ans.equals("n")) {
            initialMessage();

            System.out.println("Insert your origin airport: ");
            orig = userInput.nextLine();

            if (airportVec.contains(orig.toUpperCase())) {
                System.out.println("Insert your destination airport: ");
                dest = userInput.nextLine();
                if (airportVec.contains(dest.toUpperCase())) {
                    String path = mainFork.dijkstra(orig.toUpperCase(), dest.toUpperCase());
                    System.out.println(path);
                } else
                    System.out.println("The airport " + dest + " does not exist in our database.");

            } else {
                System.out.println("The airport " + orig + " does not exist in our database.");
            }

            do {
                System.out.println("Do you want to continue? (y/n)");
                ans = userInput.nextLine();
            } while (!ans.equals("n") && !ans.equals("y"));
        }
        System.out.println("Bye, bye!");
    }

}
