package net.minecraft.tileentity;

import net.minecraft.block.BedBlock;
import net.minecraft.item.DyeColor;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;

public class BedTileEntity extends TileEntity
{
    private DyeColor color;

    public BedTileEntity()
    {
        super(TileEntityType.BED);
    }

    public BedTileEntity(DyeColor colorIn)
    {
        this();
        this.setColor(colorIn);
    }

    /**
     * Retrieves packet to send to the client whenever this Tile Entity is resynced expensive.main.viaversion World.notifyBlockUpdate. For
     * modded TE's, this packet comes back to you clientside in {@link #onDataPacket}
     */
    public SUpdateTileEntityPacket getUpdatePacket()
    {
        return new SUpdateTileEntityPacket(this.pos, 11, this.getUpdateTag());
    }

    public DyeColor getColor()
    {
        if (this.color == null)
        {
            this.color = ((BedBlock)this.getBlockState().getBlock()).getColor();
        }

        return this.color;
    }

    public void setColor(DyeColor color)
    {
        this.color = color;
    }
}
