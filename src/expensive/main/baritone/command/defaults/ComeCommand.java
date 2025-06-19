package expensive.main.baritone.command.defaults;

import expensive.main.baritone.api.IBaritone;
import expensive.main.baritone.api.command.Command;
import expensive.main.baritone.api.command.argument.IArgConsumer;
import expensive.main.baritone.api.command.exception.CommandException;
import expensive.main.baritone.api.command.exception.CommandInvalidStateException;
import expensive.main.baritone.api.pathing.goals.GoalBlock;
import net.minecraft.entity.Entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ComeCommand extends Command {

    public ComeCommand(IBaritone baritone) {
        super(baritone, "come");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        Entity entity = mc.getRenderViewEntity();
        if (entity == null) {
            throw new CommandInvalidStateException("сущность обзора рендера равна null");
        }
        baritone.getCustomGoalProcess().setGoalAndPath(new GoalBlock(entity.getPosition()));
        logDirect("Иду");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Начать двигаться к вашей камере";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Команда come указывает Baritone двигаться к вашей камере.",
                "",
                "Это может быть полезно в модифицированных клиентах, где свободная камера не перемещает позицию игрока.",
                "",
                "Использование:",
                "> come"
        );
    }
}