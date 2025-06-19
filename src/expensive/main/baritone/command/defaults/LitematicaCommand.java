package expensive.main.baritone.command.defaults;

import expensive.main.baritone.api.IBaritone;
import expensive.main.baritone.api.command.Command;
import expensive.main.baritone.api.command.argument.IArgConsumer;
import expensive.main.baritone.api.command.exception.CommandException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class LitematicaCommand extends Command {

    public LitematicaCommand(IBaritone baritone) {
        super(baritone, "litematica");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        int schematic = 0;
        if (args.hasAny()) {
            args.requireMax(1);
            if (args.is(Integer.class)) {
                schematic = args.getAs(Integer.class) - 1;
            }
        }
        try {
            baritone.getBuilderProcess().buildOpenLitematic(schematic);
        } catch (IndexOutOfBoundsException e) {
            logDirect("Пожалуйста, укажите действительный индекс.");
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Строит загруженную схему";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Строит схему, открытую в Litematica.",
                "",
                "Использование:",
                "> litematica",
                "> litematica <#>"
        );
    }
}