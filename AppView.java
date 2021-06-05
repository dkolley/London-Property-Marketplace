import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application class. Controls the window view.
 */
public class AppView extends Application{

    private Stage stage;
    private Scene welcomePage; // the welcome page - default page
    private Scene mapPage; // page with map of boroughs
    private Scene statsPage;
    private Scene watchlistPage;

    @Override
    public void start(Stage stage) throws IOException {
        // Get required scenes
        welcomePage = new Scene(FXMLLoader.load(getClass().getResource("welcome_page.fxml")), 800, 600);
        mapPage = new Scene(FXMLLoader.load(getClass().getResource("map_page.fxml")), 800, 600);
        statsPage = new Scene(FXMLLoader.load(getClass().getResource("statistics_page.fxml")), 800, 600);
        watchlistPage = new Scene(FXMLLoader.load(getClass().getResource("watchlist_page.fxml")), 800, 600);

        // Set up window
        this.stage = stage;
        stage.setResizable(false);
        stage.setTitle("Airbnb London");
        stage.setScene(welcomePage);
        stage.show();

        // Set up the welcome page, other pages will be set up once a valid range is selected
        // Set up price range drop-downs
        ComboBox<String> min = (ComboBox<String>) welcomePage.lookup("#min_range");
        ComboBox<String> max = (ComboBox<String>) welcomePage.lookup("#max_range");

        ObservableList<String> ranges = FXCollections.observableArrayList(
                "GBP0", "GBP500", "GBP1000", "GBP1500", "GBP2000", "GBP2500", "GBP3000", "GBP3500",
                "GBP4000", "GBP4500", "GBP5000", "GBP5500", "GBP6000", "GBP6500", "GBP7000"
        );
        min.setItems(ranges);
        max.setItems(ranges);

        // Bind text and background nodes to the width/height of the
        // parent pane node
        ImageView bg = (ImageView) welcomePage.lookup("#welcome_bg");
        VBox text = (VBox) welcomePage.lookup("#welcome_text");
        Pane parent = (Pane) bg.getParent();

        bg.fitWidthProperty().bind(parent.widthProperty());
        bg.fitHeightProperty().bind(parent.heightProperty());
        text.prefWidthProperty().bind(parent.widthProperty());

        // Set up events handling for nodes
        min.setOnAction((MouseEvent) -> unlockOtherPages()); // Range drop-downs
        max.setOnAction((MouseEvent) -> unlockOtherPages());

        Button left = (Button) welcomePage.lookup("#nav_left"); // Nav buttons
        Button right = (Button) welcomePage.lookup("#nav_right");
        left.setOnAction((MouseEvent) -> {
            stage.setScene(watchlistPage);
            setUpWatchlistPage();
        });
        right.setOnAction((MouseEvent) -> {
            stage.setScene(mapPage);
            setUpMapPage();
        });

    }

    /**
     * Checks if remaining pages can be unlocked
     */
    private void unlockOtherPages() {
        ComboBox<String> minBox = (ComboBox<String>) welcomePage.lookup("#min_range");
        ComboBox<String> maxBox = (ComboBox<String>) welcomePage.lookup("#max_range");

        // Check if unlock is available
        if (minBox.valueProperty().getValue() == null || maxBox.valueProperty().getValue() == null ) {
            setNavButtonsDisable(true);
            return;
        }
        int min = Integer.parseInt(minBox.valueProperty().getValue().substring(3));
        int max = Integer.parseInt(maxBox.valueProperty().getValue().substring(3));
        if (min >= max) {
            setNavButtonsDisable(true);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Price Range");
            alert.setHeaderText("Please try again.");
            alert.setContentText("The minimum price cannot be higher than the maximum.");
            alert.showAndWait();
            return;
        }

        AirbnbDataLoader.setMin(min);
        AirbnbDataLoader.setMax(max);
        setNavButtonsDisable(false);
    }

    /**
     * Sets whether the bottom nav buttons are disabled or not
     * @param state false if disabled, true if working
     */
    private void setNavButtonsDisable(boolean state) {
        Button left = (Button) welcomePage.lookup("#nav_left");
        Button right = (Button) welcomePage.lookup("#nav_right");
        left.setDisable(state);
        right.setDisable(state);
    }

    /**
     * Sets up the map page
     */
    private void setUpMapPage() {
        // Set up event handling
        Button left = (Button) mapPage.lookup("#nav_left"); // Nav buttons
        Button right = (Button) mapPage.lookup("#nav_right");
        left.setOnAction((MouseEvent) -> stage.setScene(welcomePage));
        right.setOnAction((MouseEvent) -> {
            stage.setScene(statsPage);
            setUpStatPage();
        });

        // store a list of properties within the price range
        ArrayList<AirbnbListing> fullList = AirbnbDataLoader.loadRange();
        for (int i=1; i<34; i++) {
            ImageView ind = (ImageView) mapPage.lookup("#ind" + i);

            // copy the original list and filter by borough name.
            // this avoids having to call loadBoroughRange (i.e. loading
            // all properties) for every borough
            ArrayList<AirbnbListing> list = new ArrayList<>(fullList);
            list.removeIf(n -> (!n.getNeighbourhood().equals(ind.getAccessibleText())));

            int count = list.size();
            if (count < 500) {
                ind.setImage(new Image(new File("img/green.png").toURI().toString()));
            }
            else if (count < 2000) {
                ind.setImage(new Image(new File("img/amber.png").toURI().toString()));
            }
            else {
                ind.setImage(new Image(new File("img/red.png").toURI().toString()));
            }
        }
    }

    /**
     * Sets up the statistics page
     */
    private void setUpStatPage() {
        // Generate statistics
        Statistics.generateStatistics();

        // Set up event handling
        Button left = (Button) statsPage.lookup("#nav_left"); // Nav buttons
        Button right = (Button) statsPage.lookup("#nav_right");
        left.setOnAction((MouseEvent) -> {
            stage.setScene(mapPage);
            setUpMapPage();
        });
        right.setOnAction((MouseEvent) -> {
            stage.setScene(watchlistPage);
            setUpWatchlistPage();
        });

        Text stat1 = (Text) statsPage.lookup("#stat1");
        Text stat2 = (Text) statsPage.lookup("#stat2");
        Text stat3 = (Text) statsPage.lookup("#stat3");
        Text stat4 = (Text) statsPage.lookup("#stat4");

        stat1.setText(Statistics.getNextStat());
        stat2.setText(Statistics.getNextStat());
        stat3.setText(Statistics.getNextStat());
        stat4.setText(Statistics.getNextStat());
    }

    /**
     * Sets up the watchlist page
     */
    private void setUpWatchlistPage() {
        // Set up event handling
        Button left = (Button) watchlistPage.lookup("#nav_left"); // Nav buttons
        Button right = (Button) watchlistPage.lookup("#nav_right");
        left.setOnAction((MouseEvent) -> {
            stage.setScene(statsPage);
            setUpStatPage();
        });
        right.setOnAction((MouseEvent) -> stage.setScene(welcomePage));

        // reset scroll position to top
        ScrollPane scroll = (ScrollPane) watchlistPage.lookup("#scroll");
        scroll.setVvalue(0);

        List<String[]> properties = WatchlistDataLoader.load();
        GridPane list = (GridPane) watchlistPage.lookup("#property_list");
        list.getChildren().clear();

        int row = 0;
        for (String[] p : properties) {
            Button identifier = new Button(); // Invisible button which holds the
            identifier.setText(p[0]);    // ID of the listed property
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

            Button remove = new Button(); // Button to remove the listing
            remove.setText("Remove");
            remove.setFont(Font.font("Calibri Light", 14));
            remove.setOnAction((MouseEvent) -> {
                WatchlistDataLoader.removeFromWatchlist(p[0]);
                setUpWatchlistPage();
            });

            Text name = new Text(); // Create text nodes
            Text price = new Text();
            Text reviews = new Text();
            Text min = new Text();

            name.setText(p[1]); // Put data into text nodes
            price.setText("GBP" + p[2]);
            reviews.setText(p[3]);
            min.setText(p[4]);

            Text[] texts = {name, price, reviews, min};
            for (Text text : texts) {text.setFont(Font.font("Calibri Light", 15));}

            list.add(name, 0, row); // Add to row
            list.add(price, 1, row);
            list.add(reviews, 2, row);
            list.add(min, 3, row);
            list.add(identifier, 0, row, 4, 1);
            list.add(remove, 4, row);

            row++;
        }
    }
}
