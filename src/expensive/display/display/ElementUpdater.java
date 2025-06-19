package expensive.display.display;

import expensive.events.EventUpdate;
import expensive.util.client.main.IMinecraft;

public interface ElementUpdater extends IMinecraft {

    void update(EventUpdate e);
}
