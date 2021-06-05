import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Represents a window which contains information about a specific borough
 */
public class BoroughView {

    private Stage stage;
    private Scene boroughPage;
    private String name;
    private ArrayList<AirbnbListing> properties;

    /**
     * Default constructor for a borough view window.
     * @param name the name of the borough to represent
     * @throws IOException method throws an IOException
     */
    public BoroughView(String name) throws IOException {
        this.name = name; // name of borough
        boroughPage = new Scene(FXMLLoader.load(getClass().getResource("borough_page.fxml")), 550, 650);

        // set up stage
        stage = new Stage();
        stage.setTitle(name);
        stage.setScene(boroughPage);
        stage.show();

        // get borough properties
        properties = AirbnbDataLoader.loadBoroughRange(name);

        // set up the sort drop down
        ChoiceBox<String> sort = (ChoiceBox<String>) boroughPage.lookup("#sort");
        ObservableList<String> sortChoices = FXCollections.observableArrayList(
                "Reviews", "Price", "Alphabetical"
        );
        sort.setItems(sortChoices); // add choices
        sort.setOnAction((MouseEvent) -> addPropertyListings(sort.getValue())); // update on sort change

        // add property listings
        addPropertyListings("Reviews");

        // reset scroll position to top
        ScrollPane scroll = (ScrollPane) boroughPage.lookup("#scroll");
        scroll.setVvalue(0);
    }

    /**
     * This method adds each listing from the properties list
     * to a viewable grid in the page.
     */
    private void addPropertyListings(String sort) {
        GridPane list = (GridPane) boroughPage.lookup("#property_list");
        list.getChildren().clear();
        sortProperties(sort);

        int row = 0;
        for (AirbnbListing p : properties) {
            Button identifier = new Button(); // Invisible button which holds the
            identifier.setText(p.getId());    // ID of the listed property
            identifier.setMaxHeight(99999);
            identifier.setMaxWidth(99999);
            identifier.setOpacity(0);
            identifier.setOnMouseClicked(evt -> {
                Button identifier1 = (Button)  evt.getSource();
                try {
                    new PropertyView(identifier1.getText());
                } catch (IOException e) {
                    System.out.println("Something went wrong...");
                    e.printStackTrace();
                }
            });

            Text name = new Text(); // Create text nodes
            Text price = new Text();
            Text reviews = new Text();
            Text min = new Text();

            name.setText(p.getHost_name()); // Put data into text nodes
            price.setText("GBP" + p.getPrice());
            reviews.setText("" + p.getNumberOfReviews());
            min.setText("" + p.getMinimumNights());

            Text[] texts = {name, price, reviews, min};
            for (Text text : texts) {text.setFont(Font.font("Calibri Light", 15));}

            list.add(name, 0, row); // Add to row
            list.add(price, 1, row);
            list.add(reviews, 2, row);
            list.add(min, 3, row);
            list.add(identifier, 0, row, 4, 1);

            row++;
        }
    }

    /**
     * Sorts the properties list by number of reviews, price, or alphabetically by host name
     * @param sort the type of sorting. Can be "Reviews", "Price" or "Alphabetical"
     */
    private void sortProperties(String sort) {
        switch (sort) {
            case "Reviews":
                properties.sort(Comparator.comparingInt(AirbnbListing::getNumberOfReviews));
                Collections.reverse(properties);
                break;
            case "Price":
                properties.sort(Comparator.comparingInt(AirbnbListing::getPrice));
                break;
            case "Alphabetical":
                properties.sort(Comparator.comparing(AirbnbListing::getHost_name));
        }
    }

}
