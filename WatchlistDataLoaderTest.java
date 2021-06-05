import static org.junit.Assert.*;
import org.junit.Test;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * The test class WatchlistDataLoaderTest.
 *
 * @author  (your name)
 * @version (a version number or a date)
 */
public class WatchlistDataLoaderTest
{
    private ArrayList<AirbnbListing> propertyData;
    private List<String[]> watchlistData;
    private Random rand;
    
    /**
     * Default constructor for test class WatchlistDataLoaderTest
     */
    public WatchlistDataLoaderTest()
    {
        // get contents of watchlist/properties for later testing
        propertyData = AirbnbDataLoader.load();
        watchlistData = WatchlistDataLoader.load();
        rand = new Random();
    }
    
    @Test
    public void testLoad() {
        assertNotNull(WatchlistDataLoader.load());
    }
    
    @Test
    public void testIsInWatchlist() {
        if (watchlistData.size() != 0) {
            String id = watchlistData.get(rand.nextInt(watchlistData.size()))[0];
            boolean testForTrue = WatchlistDataLoader.isInWatchlist(id);
            assertTrue(testForTrue);
        }
    }
    
    @Test
    public void testNotInWatchlist() {
        boolean testForFalse = WatchlistDataLoader.isInWatchlist("abc"); // test for impossible id
        assertFalse(testForFalse);
    }
    
    @Test
    public void testAddToWatchlist() {
        AirbnbListing p = propertyData.get(rand.nextInt(propertyData.size()));
        // check property is not already in watch list
        while(WatchlistDataLoader.isInWatchlist(p.getId())) {
            p = propertyData.get(rand.nextInt(propertyData.size()));
        }
        WatchlistDataLoader.addToWatchlist(p);
        assertTrue(WatchlistDataLoader.isInWatchlist(p.getId()));
        WatchlistDataLoader.removeFromWatchlist(p.getId()); // remove test property
    }
    
    @Test
    public void testRemoveFromWatchList() {
        if (watchlistData.size() != 0) {
            String id = watchlistData.get(rand.nextInt(watchlistData.size()))[0];
            WatchlistDataLoader.removeFromWatchlist(id);
            assertFalse(WatchlistDataLoader.isInWatchlist(id));
            WatchlistDataLoader.addToWatchlist(AirbnbDataLoader.loadID(id));
        }
    }
    
}
