package expensive.main.baritone.command.defaults;

import expensive.main.baritone.api.IBaritone;
import expensive.main.baritone.api.command.Command;
import expensive.main.baritone.api.command.argument.IArgConsumer;
import expensive.main.baritone.api.command.exception.CommandException;
import expensive.main.baritone.api.command.exception.CommandInvalidStateException;
import expensive.main.baritone.api.pathing.calc.IPathingControlManager;
import expensive.main.baritone.api.process.IBaritoneProcess;
import expensive.main.baritone.api.process.PathingCommand;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ProcCommand extends Command {

    public ProcCommand(IBaritone baritone) {
        super(baritone, "proc");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        IPathingControlManager pathingControlManager = baritone.getPathingControlManager();
        IBaritoneProcess process = pathingControlManager.mostRecentInControl().orElse(null);
        if (process == null) {
            throw new CommandInvalidStateException("Нет процесса в управлении");
        }
        logDirect(String.format(
                "Класс: %s\n" +
                        "Приоритет: %f\n" +
                        "Временный: %b\n" +
                        "Отображаемое имя: %s\n" +
                        "Последняя команда: %s",
                process.getClass().getTypeName(),
                process.priority(),
                process.isTemporary(),
                process.displayName(),
                pathingControlManager
                        .mostRecentCommand()
                        .map(PathingCommand::toString)
                        .orElse("Нет")
        ));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Просмотр информации о состоянии процесса";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Команда proc предоставляет различную информацию о процессе, который в данный момент управляет Baritone.",
                "",
                "Не ожидается, что вы поймете это, если вы не знакомы с тем, как работает Baritone.",
                "",
                "Использование:",
                "> proc - Просмотр информации о процессе, если она есть"
        );
    }
}