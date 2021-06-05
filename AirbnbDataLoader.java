import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import com.opencsv.CSVReader;
import java.net.URISyntaxException;

public class AirbnbDataLoader {
    private static int min; // min and max price range
    private static int max;
 
    /** 
     * Return an ArrayList containing the rows in the AirBnB London data set csv file.
     */
    public static ArrayList<AirbnbListing> load() {
        System.out.print("Begin loading Airbnb london dataset...");
        ArrayList<AirbnbListing> listings = new ArrayList<>();
        try{
            URL url = AirbnbDataLoader.class.getResource("airbnb-london.csv");
            CSVReader reader = new CSVReader(new FileReader(new File(url.toURI()).getAbsolutePath()));
            String [] line;
            //skip the first row (column headers)
            reader.readNext();
            while ((line = reader.readNext()) != null) {listings.add(createListing(line));}
        } catch(IOException | URISyntaxException e){
            System.out.println("Failure! Something went wrong");
            e.printStackTrace();
        }
        System.out.println("Success! Number of loaded records: " + listings.size());
        return listings;
    }

    /**
     * Fetches an airbnb listing based on its ID
     * @param requestedID the ID of the property to return
     * @return AirbnbListing object or null if ID is not found
     */
    public static AirbnbListing loadID(String requestedID) {
        try {
            URL url = AirbnbDataLoader.class.getResource("airbnb-london.csv");
            CSVReader reader = new CSVReader(new FileReader(new File(url.toURI()).getAbsolutePath()));
            for(String[] line : reader.readAll()) {
                if (line[0].equals(requestedID)) {return createListing(line);}
            }
        } catch (IOException | URISyntaxException e){
            System.out.println("Failure! Something went wrong");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns a filtered list of properties in a borough based on the price range
     * @param name the name of the borough
     * @return filtered list of AirbnbListings
     */
    public static ArrayList<AirbnbListing> loadBoroughRange(String name) {
        ArrayList<AirbnbListing> list = load();
        list.removeIf(n -> (!n.getNeighbourhood().equals(name))); // filter to borough
        list.removeIf(n -> (n.getPrice() < min)); // filter to range
        list.removeIf(n -> (n.getPrice() > max));
        return list;
    }

    /**
     * Returns a filtered list of properties within the price range
     * @return filtered list of AirbnbListings
     */
    public static ArrayList<AirbnbListing> loadRange() {
        ArrayList<AirbnbListing> list = load();
        list.removeIf(n -> (n.getPrice() < min)); // filter to range
        list.removeIf(n -> (n.getPrice() > max));
        return list;
    }

    public static void setMin(int min) {
        AirbnbDataLoader.min = min;
    }

    public static void setMax(int max) {
        AirbnbDataLoader.max = max;
    }

    /**
     * Converts a string array from a csv reader to an airbnb listing
     * @param line the string array which contains the listing data
     * @return the created AirbnbListing object
     */
    private static AirbnbListing createListing(String[] line) {
        String id = line[0];
        String name = line[1];
        String host_id = line[2];
        String host_name = line[3];
        String neighbourhood = line[4];
        double latitude = convertDouble(line[5]);
        double longitude = convertDouble(line[6]);
        String room_type = line[7];
        int price = convertInt(line[8]);
        int minimumNights = convertInt(line[9]);
        int numberOfReviews = convertInt(line[10]);
        String lastReview = line[11];
        double reviewsPerMonth = convertDouble(line[12]);
        int calculatedHostListingsCount = convertInt(line[13]);
        int availability365 = convertInt(line[14]);

        AirbnbListing listing = new AirbnbListing(id, name, host_id,
                host_name, neighbourhood, latitude, longitude, room_type,
                price, minimumNights, numberOfReviews, lastReview,
                reviewsPerMonth, calculatedHostListingsCount, availability365
        );
        return listing;
    }

    /**
     *
     * @param doubleString the string to be converted to Double type
     * @return the Double value of the string, or -1.0 if the string is 
     * either empty or just whitespace
     */
    private static Double convertDouble(String doubleString){
        if(doubleString != null && !doubleString.trim().equals("")){
            return Double.parseDouble(doubleString);
        }
        return -1.0;
    }

    /**
     *
     * @param intString the string to be converted to Integer type
     * @return the Integer value of the string, or -1 if the string is 
     * either empty or just whitespace
     */
    private static Integer convertInt(String intString){
        if(intString != null && !intString.trim().equals("")){
            return Integer.parseInt(intString);
        }
        return -1;
    }

}
