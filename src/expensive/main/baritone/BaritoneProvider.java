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

package expensive.main.baritone;

import expensive.main.baritone.api.IBaritone;
import expensive.main.baritone.api.IBaritoneProvider;
import expensive.main.baritone.api.cache.IWorldScanner;
import expensive.main.baritone.api.command.ICommandSystem;
import expensive.main.baritone.api.schematic.ISchematicSystem;
import expensive.main.baritone.cache.WorldScanner;
import expensive.main.baritone.command.CommandSystem;
import expensive.main.baritone.command.ExampleBaritoneControl;
import expensive.main.baritone.utils.schematic.SchematicSystem;

import java.util.Collections;
import java.util.List;

/**
 * @author Brady
 * @since 9/29/2018
 */
public final class BaritoneProvider implements IBaritoneProvider {

    private final Baritone primary;
    private final List<IBaritone> all;

    {
        this.primary = new Baritone();
        this.all = Collections.singletonList(this.primary);

        // Setup chat control, just for the primary instance
        new ExampleBaritoneControl(this.primary);
    }

    @Override
    public IBaritone getPrimaryBaritone() {
        return primary;
    }

    @Override
    public List<IBaritone> getAllBaritones() {
        return all;
    }

    @Override
    public IWorldScanner getWorldScanner() {
        return WorldScanner.INSTANCE;
    }

    @Override
    public ICommandSystem getCommandSystem() {
        return CommandSystem.INSTANCE;
    }

    @Override
    public ISchematicSystem getSchematicSystem() {
        return SchematicSystem.INSTANCE;
    }
}
