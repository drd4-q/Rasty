package expensive.main.baritone.command.defaults;

import expensive.main.baritone.api.IBaritone;
import expensive.main.baritone.api.command.Command;
import expensive.main.baritone.api.command.argument.IArgConsumer;
import expensive.main.baritone.api.command.exception.CommandException;
import expensive.main.baritone.api.command.exception.CommandInvalidStateException;
import expensive.main.baritone.api.pathing.goals.Goal;
import expensive.main.baritone.api.pathing.goals.GoalInverted;
import expensive.main.baritone.api.process.ICustomGoalProcess;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class InvertCommand extends Command {

    public InvertCommand(IBaritone baritone) {
        super(baritone, "invert");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        ICustomGoalProcess customGoalProcess = baritone.getCustomGoalProcess();
        Goal goal;
        if ((goal = customGoalProcess.getGoal()) == null) {
            throw new CommandInvalidStateException("Нет цели");
        }
        if (goal instanceof GoalInverted) {
            goal = ((GoalInverted) goal).origin;
        } else {
            goal = new GoalInverted(goal);
        }
        customGoalProcess.setGoalAndPath(goal);
        logDirect(String.format("Цель: %s", goal.toString()));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Убежать от текущей цели";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Команда invert указывает Baritone двигаться от текущей цели, а не к ней.",
                "",
                "Использование:",
                "> invert - Инвертировать текущую цель."
        );
    }
}