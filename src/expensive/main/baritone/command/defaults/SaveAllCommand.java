package expensive.main.baritone.command.defaults;

import expensive.main.baritone.api.IBaritone;
import expensive.main.baritone.api.command.Command;
import expensive.main.baritone.api.command.argument.IArgConsumer;
import expensive.main.baritone.api.command.exception.CommandException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SaveAllCommand extends Command {

    public SaveAllCommand(IBaritone baritone) {
        super(baritone, "saveall");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        ctx.worldData().getCachedWorld().save();
        logDirect("Сохранено");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Сохраняет кэш Baritone для этого мира";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Команда saveall сохраняет кэш мира Baritone.",
                "",
                "Использование:",
                "> saveall"
        );
    }
}