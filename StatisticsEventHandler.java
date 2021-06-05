import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;

/**
 * This class is used to to handle button presses in the statistics page
 */
public class StatisticsEventHandler implements EventHandler<Event> {
    @Override
    public void handle(Event evt) {
        Node node = (Node) evt.getSource();
        Statistics.changeDisplayedStat(node);
    }
}
