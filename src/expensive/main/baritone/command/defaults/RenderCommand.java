package expensive.main.baritone.command.defaults;

import expensive.main.baritone.api.IBaritone;
import expensive.main.baritone.api.command.Command;
import expensive.main.baritone.api.command.argument.IArgConsumer;
import expensive.main.baritone.api.command.exception.CommandException;
import expensive.main.baritone.api.utils.BetterBlockPos;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class RenderCommand extends Command {

    public RenderCommand(IBaritone baritone) {
        super(baritone, "render");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        BetterBlockPos origin = ctx.playerFeet();
        int renderDistance = (mc.gameSettings.renderDistanceChunks + 1) * 16;
        mc.worldRenderer.markBlockRangeForRenderUpdate(
                origin.x - renderDistance,
                0,
                origin.z - renderDistance,
                origin.x + renderDistance,
                255,
                origin.z + renderDistance
        );
        logDirect("Готово");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Исправить глючные чанки";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Команда render исправляет глючную отрисовку чанков без необходимости их полной перезагрузки.",
                "",
                "Использование:",
                "> render"
        );
    }
}