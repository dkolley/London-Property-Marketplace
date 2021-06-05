import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Represents a single Airbnb property listing (or AirbnbListing object)
 */
public class PropertyView {
    private AirbnbListing propertyData;
    private Scene propertyView;

    /**
     * Default constructor for a property view window
     * @param id the property id to represent
     * @throws IOException method throws an IOException
     */
    public PropertyView(String id) throws IOException {
        propertyData = AirbnbDataLoader.loadID(id);
        propertyView = new Scene(FXMLLoader.load(getClass().getResource("property_page.fxml")), 550, 650);

        // set up stage
        Stage stage = new Stage();
        stage.setTitle("Property " + propertyData.getId());
        stage.setScene(propertyView);
        stage.show();

        // set up property page
        Text desc = (Text) propertyView.lookup("#desc");
        Text hname = (Text) propertyView.lookup("#hname");
        Text borough = (Text) propertyView.lookup("#borough");
        Text price = (Text) propertyView.lookup("#price");
        Text type = (Text) propertyView.lookup("#type");
        Text reviews = (Text) propertyView.lookup("#reviews");
        Text lastReview = (Text) propertyView.lookup("#availability");
        Text lat = (Text) propertyView.lookup("#lat");
        Text lon = (Text) propertyView.lookup("#long");
        Button watchlist = (Button) propertyView.lookup("#watchlist");
        desc.setText("Description: " + propertyData.getName());
        hname.setText("Host name: " + propertyData.getHost_name());
        borough.setText("Neighbourhood: " + propertyData.getNeighbourhood());
        price.setText("Price: GBP" + propertyData.getPrice());
        type.setText("Room type: " + propertyData.getRoom_type());
        reviews.setText("Number of reviews: " + propertyData.getNumberOfReviews());
        lastReview.setText("Availability 365: " + propertyData.getAvailability365());
        lat.setText("Latitude: " + propertyData.getLatitude());
        lon.setText("Longitude: " + propertyData.getLongitude());
        if (!WatchlistDataLoader.isInWatchlist(propertyData.getId())) {
            watchlist.setOnMouseClicked(evt -> {
                Button add = (Button)  evt.getSource();
                add.setDisable(true);
                WatchlistDataLoader.addToWatchlist(propertyData);
            });
        }
        else {
            watchlist.setDisable(true);
        }
    }
}
