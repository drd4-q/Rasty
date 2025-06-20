package net.minecraft.network.play.client;

import java.io.IOException;

import expensive.main.Expensive;
import expensive.events.InventoryCloseEvent;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;

public class CCloseWindowPacket implements IPacket<IServerPlayNetHandler>
{
    private int windowId;

    public CCloseWindowPacket()
    {
    }

    public CCloseWindowPacket(int windowIdIn)
    {
        this.windowId = windowIdIn;

    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(IServerPlayNetHandler handler)
    {
        handler.processCloseWindow(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buf) throws IOException
    {
        this.windowId = buf.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.windowId);
    }
}
