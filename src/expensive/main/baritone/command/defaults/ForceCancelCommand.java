package expensive.main.baritone.command.defaults;

import expensive.main.baritone.api.IBaritone;
import expensive.main.baritone.api.behavior.IPathingBehavior;
import expensive.main.baritone.api.command.Command;
import expensive.main.baritone.api.command.argument.IArgConsumer;
import expensive.main.baritone.api.command.exception.CommandException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ForceCancelCommand extends Command {

    public ForceCancelCommand(IBaritone baritone) {
        super(baritone, "forcecancel");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        IPathingBehavior pathingBehavior = baritone.getPathingBehavior();
        pathingBehavior.cancelEverything();
        pathingBehavior.forceCancel();
        logDirect("ок, принудительно отменено");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Принудительная отмена";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Как cancel, но более принудительно.",
                "",
                "Использование:",
                "> forcecancel"
        );
    }
}