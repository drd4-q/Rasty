package expensive.main.baritone.command.defaults;

import expensive.main.baritone.api.IBaritone;
import expensive.main.baritone.api.command.Command;
import expensive.main.baritone.api.command.argument.IArgConsumer;
import expensive.main.baritone.api.command.datatypes.BlockById;
import expensive.main.baritone.api.command.datatypes.ForBlockOptionalMeta;
import expensive.main.baritone.api.command.exception.CommandException;
import expensive.main.baritone.api.utils.BlockOptionalMeta;
import expensive.main.baritone.cache.WorldScanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class MineCommand extends Command {

    public MineCommand(IBaritone baritone) {
        super(baritone, "mine");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        int quantity = args.getAsOrDefault(Integer.class, 0);
        args.requireMin(1);
        List<BlockOptionalMeta> boms = new ArrayList<>();
        while (args.hasAny()) {
            boms.add(args.getDatatypeFor(ForBlockOptionalMeta.INSTANCE));
        }
        WorldScanner.INSTANCE.repack(ctx);
        logDirect(String.format("Добыча %s", boms.toString()));
        baritone.getMineProcess().mine(quantity, boms.toArray(new BlockOptionalMeta[0]));
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return args.tabCompleteDatatype(BlockById.INSTANCE);
    }

    @Override
    public String getShortDesc() {
        return "Добыть некоторые блоки";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Команда mine позволяет указать Baritone искать и добывать отдельные блоки.",
                "",
                "Указанные блоки могут быть рудами или любыми другими блоками.",
                "",
                "Также см. настройки legitMine (см. #set l legitMine).",
                "",
                "Использование:",
                "> mine diamond_ore - Добывает все алмазы, которые может найти."
        );
    }
}