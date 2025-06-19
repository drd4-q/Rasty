package expensive.display.display;

import expensive.events.EventDisplay;
import expensive.util.client.main.IMinecraft;

public interface ElementRenderer extends IMinecraft {
    void render(EventDisplay eventDisplay);
}
