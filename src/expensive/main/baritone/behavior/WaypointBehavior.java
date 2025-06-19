/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package expensive.main.baritone.behavior;

import expensive.main.baritone.Baritone;
import expensive.main.baritone.api.cache.IWaypoint;
import expensive.main.baritone.api.cache.Waypoint;
import expensive.main.baritone.api.event.events.BlockInteractEvent;
import expensive.main.baritone.api.utils.BetterBlockPos;
import expensive.main.baritone.api.utils.Helper;
import expensive.main.baritone.utils.BlockStateInterface;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Set;

import static expensive.main.baritone.api.command.IBaritoneChatControl.FORCE_COMMAND_PREFIX;

public class WaypointBehavior extends Behavior {


    public WaypointBehavior(Baritone baritone) {
        super(baritone);
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        if (!Baritone.settings().doBedWaypoints.value)
            return;
        if (event.getType() == BlockInteractEvent.Type.USE) {
            BetterBlockPos pos = BetterBlockPos.from(event.getPos());
            BlockState state = BlockStateInterface.get(ctx, pos);
            if (state.getBlock() instanceof BedBlock) {
                if (state.get(BedBlock.PART) == BedPart.FOOT) {
                    pos = pos.offset(state.get(BedBlock.HORIZONTAL_FACING));
                }
                Set<IWaypoint> waypoints = baritone.getWorldProvider().getCurrentWorld().getWaypoints().getByTag(IWaypoint.Tag.BED);
                boolean exists = waypoints.stream().map(IWaypoint::getLocation).filter(pos::equals).findFirst().isPresent();
                if (!exists) {
                    baritone.getWorldProvider().getCurrentWorld().getWaypoints().addWaypoint(new Waypoint("bed", Waypoint.Tag.BED, pos));
                }
            }
        }
    }

    @Override
    public void onPlayerDeath() {
        if (!Baritone.settings().doDeathWaypoints.value)
            return;
        Waypoint deathWaypoint = new Waypoint("death", Waypoint.Tag.DEATH, ctx.playerFeet());
        baritone.getWorldProvider().getCurrentWorld().getWaypoints().addWaypoint(deathWaypoint);
        IFormattableTextComponent component = new StringTextComponent("Death position saved.");
        component.setStyle(component.getStyle()
                .setFormatting(TextFormatting.WHITE)
                .setHoverEvent(new HoverEvent(
                        HoverEvent.Action.SHOW_TEXT,
                        new StringTextComponent("Click to goto death")
                ))
                .setClickEvent(new ClickEvent(
                        ClickEvent.Action.RUN_COMMAND,
                        String.format(
                                "%s%s goto %s @ %d",
                                FORCE_COMMAND_PREFIX,
                                "wp",
                                deathWaypoint.getTag().getName(),
                                deathWaypoint.getCreationTimestamp()
                        )
                )));
        Helper.HELPER.logDirect(component);
    }

}
