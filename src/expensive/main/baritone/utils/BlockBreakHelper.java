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

package expensive.main.baritone.utils;

import expensive.main.baritone.api.utils.Helper;
import expensive.main.baritone.api.utils.IPlayerContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

/**
 * @author Brady
 * @since 8/25/2018
 */
public final class BlockBreakHelper implements Helper {

    private final IPlayerContext ctx;
    private boolean didBreakLastTick;

    BlockBreakHelper(IPlayerContext ctx) {
        this.ctx = ctx;
    }

    public void stopBreakingBlock() {
        // The player controller will never be null, but the player can be
        if (ctx.player() != null && didBreakLastTick) {
            if (!ctx.playerController().hasBrokenBlock()) {
                // insane bypass to check breaking succeeded
                ctx.playerController().setHittingBlock(true);
            }
            ctx.playerController().resetBlockRemoving();
            didBreakLastTick = false;
        }
    }

    public void tick(boolean isLeftClick) {
        RayTraceResult trace = ctx.objectMouseOver();
        boolean isBlockTrace = trace != null && trace.getType() == RayTraceResult.Type.BLOCK;

        if (isLeftClick && isBlockTrace) {
            if (!didBreakLastTick) {
                ctx.playerController().syncHeldItem();
                ctx.playerController().clickBlock(((BlockRayTraceResult) trace).getPos(), ((BlockRayTraceResult) trace).getFace());
                ctx.player().swingArm(Hand.MAIN_HAND);
            }

            // Attempt to break the block
            if (ctx.playerController().onPlayerDamageBlock(((BlockRayTraceResult) trace).getPos(), ((BlockRayTraceResult) trace).getFace())) {
                ctx.player().swingArm(Hand.MAIN_HAND);
            }

            ctx.playerController().setHittingBlock(false);

            didBreakLastTick = true;
        } else if (didBreakLastTick) {
            stopBreakingBlock();
            didBreakLastTick = false;
        }
    }
}
