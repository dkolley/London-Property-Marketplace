import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

import java.io.IOException;

/**
 * The purpose of this class is to check for 'mouse clicked' events on
 * map icons, then open the appropriate BoroughView using the text
 * of the clicked button.
 */
public class BoroughRequestEventHandler implements EventHandler<Event> {
    @Override
    public void handle(Event evt) {
        Button button = (Button) evt.getSource();
        try {
            new BoroughView(button.getText());
        } catch (IOException e) {
            System.out.println("Something went wrong...");
            e.printStackTrace();
        }
    }
}
