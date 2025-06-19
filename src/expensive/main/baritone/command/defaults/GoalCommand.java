package expensive.main.baritone.command.defaults;

import expensive.main.baritone.api.IBaritone;
import expensive.main.baritone.api.command.Command;
import expensive.main.baritone.api.command.argument.IArgConsumer;
import expensive.main.baritone.api.command.datatypes.RelativeCoordinate;
import expensive.main.baritone.api.command.datatypes.RelativeGoal;
import expensive.main.baritone.api.command.exception.CommandException;
import expensive.main.baritone.api.command.helpers.TabCompleteHelper;
import expensive.main.baritone.api.pathing.goals.Goal;
import expensive.main.baritone.api.process.ICustomGoalProcess;
import expensive.main.baritone.api.utils.BetterBlockPos;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class GoalCommand extends Command {

    public GoalCommand(IBaritone baritone) {
        super(baritone, "goal");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        ICustomGoalProcess goalProcess = baritone.getCustomGoalProcess();
        if (args.hasAny() && Arrays.asList("reset", "clear", "none").contains(args.peekString())) {
            args.requireMax(1);
            if (goalProcess.getGoal() != null) {
                goalProcess.setGoal(null);
                logDirect("Цель очищена");
            } else {
                logDirect("Не было цели для очистки");
            }
        } else {
            args.requireMax(3);
            BetterBlockPos origin = baritone.getPlayerContext().playerFeet();
            Goal goal = args.getDatatypePost(RelativeGoal.INSTANCE, origin);
            goalProcess.setGoal(goal);
            logDirect(String.format("Цель: %s", goal.toString()));
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        TabCompleteHelper helper = new TabCompleteHelper();
        if (args.hasExactlyOne()) {
            helper.append("reset", "clear", "none", "~");
        } else {
            if (args.hasAtMost(3)) {
                while (args.has(2)) {
                    if (args.peekDatatypeOrNull(RelativeCoordinate.INSTANCE) == null) {
                        break;
                    }
                    args.get();
                    if (!args.has(2)) {
                        helper.append("~");
                    }
                }
            }
        }
        return helper.filterPrefix(args.getString()).stream();
    }

    @Override
    public String getShortDesc() {
        return "Установить или очистить цель";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Команда goal позволяет установить или очистить цель Baritone.",
                "",
                "Где ожидается координата, вы можете использовать ~, как в обычных командах Minecraft. Или просто использовать обычные числа.",
                "",
                "Использование:",
                "> goal - Установить цель на вашу текущую позицию",
                "> goal <reset/clear/none> - Стереть цель",
                "> goal <y> - Установить цель на уровень Y",
                "> goal <x> <z> - Установить цель на позицию X,Z",
                "> goal <x> <y> <z> - Установить цель на позицию X,Y,Z"
        );
    }
}