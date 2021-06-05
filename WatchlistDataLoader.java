import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class allows the watchlist file to be accessed and modified
 */
public class WatchlistDataLoader {

    /**
     * Insert a new entry into the watchlist
     * @param p the airbnb listing to add
     */
    public static void addToWatchlist(AirbnbListing p) {
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(new File("watchlist.csv").getAbsolutePath(), true));
            String[] data = {p.getId(), p.getHost_name(), "" + p.getPrice(), "" + p.getNumberOfReviews(), "" + p.getMinimumNights()};
            writer.writeNext(data, false); // add property data from listing
            writer.close();
        }
        catch (IOException e) {
            System.out.println("Couldn't load watchlist file...");
            e.printStackTrace();
        }
    }

    /**
     * Remove a new entry into the watchlist
     * @param id id of the property to remove
     */
    public static void removeFromWatchlist(String id) {
        try {
            CSVReader reader = new CSVReader(new FileReader(new File("watchlist.csv").getAbsolutePath()));
            List<String[]> listings = reader.readAll();
            listings.removeIf(listing -> (listing[0].equals(id)));
            reader.close();

            CSVWriter writer = new CSVWriter(new FileWriter(new File("watchlist.csv").getAbsolutePath()));
            writer.writeAll(listings, false);
            writer.close();
        }
        catch (IOException e) {
            System.out.println("Couldn't load watchlist file...");
            e.printStackTrace();
        }
    }

    public static boolean isInWatchlist(String id) {
        try {
            CSVReader reader = new CSVReader(new FileReader(new File("watchlist.csv").getAbsolutePath()));
            List<String[]> listings = reader.readAll();
            return listings.stream().anyMatch(listing -> listing[0].equals(id));
        }
        catch (IOException e) {
            System.out.println("Couldn't load watchlist file...");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * retrieve a list of string arrays, each containing one property listing from the watchlist
     * @return
     */
    public static List<String[]> load() {
        List<String[]> listings = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(new FileReader(new File("watchlist.csv").getAbsolutePath()));
            listings = reader.readAll();
            listings.remove(0); // remove headers
        }
        catch (IOException e) {
            System.out.println("Couldn't load watchlist file...");
            e.printStackTrace();
        }
        return listings;
    }
}
