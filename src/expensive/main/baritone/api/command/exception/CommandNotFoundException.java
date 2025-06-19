package expensive.main.baritone.api.command.exception;

import expensive.main.baritone.api.BaritoneAPI;
import expensive.main.baritone.api.command.ICommand;
import expensive.main.baritone.api.command.argument.ICommandArgument;

import java.util.List;

import static expensive.main.baritone.api.utils.Helper.HELPER;

public class CommandNotFoundException extends CommandException {

    public final String command;

    public CommandNotFoundException(String command) {
        super(String.format("Command not found: %s", command));
        this.command = command;
    }

    @Override
    public void handle(ICommand command, List<ICommandArgument> args) {
        String prefix = BaritoneAPI.getSettings().prefix.value;
        if (args != null && !args.isEmpty() && args.get(0).getValue().startsWith(prefix)) {
            HELPER.logDirect(getMessage());
        }
    }
}