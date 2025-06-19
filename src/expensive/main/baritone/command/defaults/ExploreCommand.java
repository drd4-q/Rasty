package expensive.main.baritone.command.defaults;

import expensive.main.baritone.api.IBaritone;
import expensive.main.baritone.api.command.Command;
import expensive.main.baritone.api.command.argument.IArgConsumer;
import expensive.main.baritone.api.command.datatypes.RelativeGoalXZ;
import expensive.main.baritone.api.command.exception.CommandException;
import expensive.main.baritone.api.pathing.goals.GoalXZ;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ExploreCommand extends Command {

    public ExploreCommand(IBaritone baritone) {
        super(baritone, "explore");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        if (args.hasAny()) {
            args.requireExactly(2);
        } else {
            args.requireMax(0);
        }
        GoalXZ goal = args.hasAny()
                ? args.getDatatypePost(RelativeGoalXZ.INSTANCE, ctx.playerFeet())
                : new GoalXZ(ctx.playerFeet());
        baritone.getExploreProcess().explore(goal.getX(), goal.getZ());
        logDirect(String.format("Исследую из %s", goal.toString()));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        if (args.hasAtMost(2)) {
            return args.tabCompleteDatatype(RelativeGoalXZ.INSTANCE);
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Исследовать местность";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Указывает Baritone исследовать местность случайным образом. Если вы ранее использовали explorefilter, он будет применен.",
                "",
                "Использование:",
                "> explore - Исследовать с текущей позиции.",
                "> explore <x> <z> - Исследовать с указанной позиции X и Z."
        );
    }
}