package expensive.main.baritone.command.defaults;

import expensive.main.baritone.api.IBaritone;
import expensive.main.baritone.api.command.Command;
import expensive.main.baritone.api.command.argument.IArgConsumer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class CommandAlias extends Command {

    private final String shortDesc;
    public final String target;

    public CommandAlias(IBaritone baritone, List<String> names, String shortDesc, String target) {
        super(baritone, names.toArray(new String[0]));
        this.shortDesc = shortDesc;
        this.target = target;
    }

    public CommandAlias(IBaritone baritone, String name, String shortDesc, String target) {
        super(baritone, name);
        this.shortDesc = shortDesc;
        this.target = target;
    }

    @Override
    public void execute(String label, IArgConsumer args) {
        this.baritone.getCommandManager().execute(String.format("%s %s", target, args.rawRest()));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return this.baritone.getCommandManager().tabComplete(String.format("%s %s", target, args.rawRest()));
    }

    @Override
    public String getShortDesc() {
        return shortDesc;
    }

    @Override
    public List<String> getLongDesc() {
        return Collections.singletonList(String.format("Эта команда является псевдонимом для: %s ...", target));
    }
}