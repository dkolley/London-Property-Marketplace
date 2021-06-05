import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Statistics {
    private static ArrayList<AirbnbListing> list = new ArrayList<>();
    private static List<String[]> watchlist;
    private static final ArrayList<String> stats = new ArrayList<>();

    /**
     * Generates all statistics and adds them to the stats array list
     */
    public static void generateStatistics() {
        stats.clear();
        list = AirbnbDataLoader.loadRange();
        watchlist = WatchlistDataLoader.load();
        stats.add("Average number of reviews per property\n\n" + getAverageReviewsPerProperty());
        stats.add("Total number of available properties\n\n" + getTotalProperties());
        stats.add("Number of non private properties\n\n" + getNonPrivateProperties());
        stats.add("The most expensive borough\n\n" + getMostExpensiveBorough());
        stats.add("The cheapest borough\n\n" + getCheapestBorough());
        stats.add("Highest number of reviews received\n\n" + getMostRatedProperty());
        stats.add("Average price of properties in watchlist\n\nGBP" + getAverageWatchlistPrice());
        stats.add("Most frequent borough in watch list\n\n" + getMostFrequentWatchlistBorough());
    }

    /**
     * Properly changes the displayed stat when a mouse event is received from
     * a button in the stat page
     * @param node the node (button) that was pressed
     */
    public static void changeDisplayedStat(Node node) {
        Scene scene = node.getScene();
        Text text = (Text) scene.lookup("#stat" + node.getId().charAt(0));
        char dir = node.getId().charAt(1); // represents direction to move (0 = right, 1 = left)

        if (dir == '0') {
            stats.add(stats.size(), text.getText());
            text.setText(stats.get(0));
            stats.remove(0);
        }
        else {
            stats.add(0, text.getText());
            text.setText(stats.get(stats.size()-1));
            stats.remove(stats.size()-1);
        }
    }

    /**
     * Return the first element in the stats list. Should not be used after the page
     * has been set up
     * @return the stat string
     */
    public static String getNextStat() {
        String stat = stats.get(0);
        stats.remove(0);
        return stat;
    }

    /**
     *
     * @return the number of non private properties in the current price range
     */
    private static int getNonPrivateProperties() {
        int count = 0;
        for (AirbnbListing listing : list) {
            if (!listing.getRoom_type().equals("Private room")) {
                count++;
            }
        }
        return count;
    }

    /**
     *
     * @return average number of reviews per property
     */
    private static int getAverageReviewsPerProperty() {
        int count = 0;
        for (AirbnbListing listing : list) {
            count += listing.getNumberOfReviews();
        }
        return count / list.size();
    }

    private static int getTotalProperties() {
        return list.size();
    }

    /**
     *
     * @return borough with the most expensive property
     */
    private static String getMostExpensiveBorough() {
        int highestPrice = 0; // highest price = price * min number of nights
        String borough = "";
        for (AirbnbListing listing : list) {
            int listingPrice = listing.getPrice() * listing.getMinimumNights();
            if (listingPrice > highestPrice) {
                highestPrice = listingPrice;
                borough = listing.getNeighbourhood();
            }
        }
        return borough;
    }

    /**
     *
     * @return borough with the cheapest property
     */
    private static String getCheapestBorough() {
        int lowestPrice = list.get(0).getPrice() * list.get(0).getMinimumNights(); // use first property as initial price
        String borough = list.get(0).getNeighbourhood();
        for (AirbnbListing listing : list) {
            int listingPrice = listing.getPrice() * listing.getMinimumNights();
            if (listingPrice < lowestPrice) {
                lowestPrice = listingPrice;
                borough = listing.getNeighbourhood();
            }
        }
        return borough;
    }

    /**
     *
     * @return the number of reviews from the most reviewed property
     */
    private static int getMostRatedProperty() {
        int rating = 0;
        for (AirbnbListing listing : list) {
            if(listing.getNumberOfReviews() > rating) {
                rating = listing.getNumberOfReviews();
            }
        }
        return rating;
    }

    private static int getAverageWatchlistPrice() {
        if (watchlist.size() == 0) {return 0;}

        int count = 0;
        for (String[] listing : watchlist) {
            count += Integer.parseInt(listing[2]);
        }
        return count / watchlist.size();
    }

    private static String getMostFrequentWatchlistBorough() {
        if (watchlist.size() == 0) {return "N/A";}

        HashMap<String, Integer> boroughFreq = new HashMap<>();
        int highestFreq = 0;
        String borough = "";

        for (String[] listing : watchlist) {
            String id = listing[0]; // get id of property, then use to find airbnb listing object
            AirbnbListing property = AirbnbDataLoader.loadID(id);
            // get the current count for the property borough, or 0 if not found, plus 1
            int val = boroughFreq.getOrDefault(property.getNeighbourhood(), 0) + 1;
            boroughFreq.put(property.getNeighbourhood(), val);
            if (val > highestFreq) {
                highestFreq = val;
                borough = property.getNeighbourhood();
            }
        }

        return borough;
    }
}
