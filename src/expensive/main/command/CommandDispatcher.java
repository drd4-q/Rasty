package expensive.main.command;

import expensive.main.command.impl.DispatchResult;

public interface CommandDispatcher {
    DispatchResult dispatch(String command);
}
