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

package expensive.main.baritone.api.event.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.vector.Matrix4f;

/**
 * @author Brady
 * @since 8/5/2018
 */
public final class RenderEvent {

    /**
     * The current render partial ticks
     */
    private final float partialTicks;

    private final Matrix4f projectionMatrix;
    private final MatrixStack modelViewStack;

    public RenderEvent(float partialTicks, MatrixStack modelViewStack, Matrix4f projectionMatrix) {
        this.partialTicks = partialTicks;
        this.modelViewStack = modelViewStack;
        this.projectionMatrix = projectionMatrix;
    }

    /**
     * @return The current render partial ticks
     */
    public final float getPartialTicks() {
        return this.partialTicks;
    }

    public MatrixStack getModelViewStack() {
        return this.modelViewStack;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
}
