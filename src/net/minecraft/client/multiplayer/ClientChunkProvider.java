package net.minecraft.client.multiplayer;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;

import expensive.main.baritone.utils.accessor.IChunkArray;
import expensive.main.baritone.utils.accessor.IClientChunkProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.SectionPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.lighting.WorldLightManager;
import net.optifine.ChunkDataOF;
import net.optifine.ChunkOF;
import net.optifine.reflect.Reflector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientChunkProvider extends AbstractChunkProvider implements IClientChunkProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final Chunk empty;
    private final WorldLightManager lightManager;
    private volatile ChunkArray array;
    private final ClientWorld world;

    public ClientChunkProvider(ClientWorld clientWorldIn, int viewDistance)
    {
        this.world = clientWorldIn;
        this.empty = new EmptyChunk(clientWorldIn, new ChunkPos(0, 0));
        this.lightManager = new WorldLightManager(this, true, clientWorldIn.getDimensionType().hasSkyLight());
        this.array = new ChunkArray(adjustViewDistance(viewDistance));
    }

    public WorldLightManager getLightManager()
    {
        return this.lightManager;
    }

    private static boolean isValid(@Nullable Chunk chunkIn, int x, int z)
    {
        if (chunkIn == null)
        {
            return false;
        }
        else
        {
            ChunkPos chunkpos = chunkIn.getPos();
            return chunkpos.x == x && chunkpos.z == z;
        }
    }

    /**
     * Unload chunk from ChunkProviderClient's hashmap. Called in response to a Packet50PreChunk with its mode field set
     * to false
     */
    public void unloadChunk(int x, int z)
    {
        if (this.array.inView(x, z))
        {
            int i = this.array.getIndex(x, z);
            Chunk chunk = this.array.get(i);

            if (isValid(chunk, x, z))
            {
                if (Reflector.ChunkEvent_Unload_Constructor.exists())
                {
                    Reflector.postForgeBusEvent(Reflector.ChunkEvent_Unload_Constructor, chunk);
                }

                chunk.setLoaded(false);
                this.array.unload(i, chunk, (Chunk)null);
            }
        }
    }

    @Nullable
    public Chunk getChunk(int chunkX, int chunkZ, ChunkStatus requiredStatus, boolean load)
    {
        if (this.array.inView(chunkX, chunkZ))
        {
            Chunk chunk = this.array.get(this.array.getIndex(chunkX, chunkZ));

            if (isValid(chunk, chunkX, chunkZ))
            {
                return chunk;
            }
        }

        return load ? this.empty : null;
    }

    public IBlockReader getWorld()
    {
        return this.world;
    }

    @Nullable
    public Chunk loadChunk(int chunkX, int chunkZ, @Nullable BiomeContainer biomeContainerIn, PacketBuffer packetIn, CompoundNBT nbtTagIn, int sizeIn, boolean p_228313_7_)
    {
        if (!this.array.inView(chunkX, chunkZ))
        {
            LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", chunkX, chunkZ);
            return null;
        }
        else
        {
            int i = this.array.getIndex(chunkX, chunkZ);
            Chunk chunk = this.array.chunks.get(i);

            if (!p_228313_7_ && isValid(chunk, chunkX, chunkZ))
            {
                boolean flag = false;

                if (chunk instanceof ChunkOF)
                {
                    ChunkOF chunkof = (ChunkOF)chunk;
                    Object object = packetIn.getCustomData("ChunkDataOF");

                    if (object instanceof ChunkDataOF)
                    {
                        ChunkDataOF chunkdataof = (ChunkDataOF)object;
                        chunkof.setChunkDataOF(chunkdataof);
                        ChunkSection.THREAD_CHUNK_DATA_OF.set(chunkdataof);
                        flag = true;
                    }
                }

                chunk.read(biomeContainerIn, packetIn, nbtTagIn, sizeIn);

                if (flag)
                {
                    ChunkSection.THREAD_CHUNK_DATA_OF.set((ChunkDataOF)null);
                }
            }
            else
            {
                if (biomeContainerIn == null)
                {
                    LOGGER.warn("Ignoring chunk since we don't have complete data: {}, {}", chunkX, chunkZ);
                    return null;
                }

                if (chunk != null)
                {
                    chunk.setLoaded(false);
                }

                chunk = new ChunkOF(this.world, new ChunkPos(chunkX, chunkZ), biomeContainerIn);
                chunk.read(biomeContainerIn, packetIn, nbtTagIn, sizeIn);
                this.array.replace(i, chunk);
            }

            ChunkSection[] achunksection = chunk.getSections();
            WorldLightManager worldlightmanager = this.getLightManager();
            worldlightmanager.enableLightSources(new ChunkPos(chunkX, chunkZ), true);

            for (int j = 0; j < achunksection.length; ++j)
            {
                ChunkSection chunksection = achunksection[j];
                worldlightmanager.updateSectionStatus(SectionPos.of(chunkX, j, chunkZ), ChunkSection.isEmpty(chunksection));
            }

            this.world.onChunkLoaded(chunkX, chunkZ);

            if (Reflector.ChunkEvent_Load_Constructor.exists())
            {
                Reflector.postForgeBusEvent(Reflector.ChunkEvent_Load_Constructor, chunk);
            }

            chunk.setLoaded(true);
            return chunk;
        }
    }

    public void tick(BooleanSupplier hasTimeLeft)
    {
    }

    public void setCenter(int x, int z)
    {
        this.array.centerX = x;
        this.array.centerZ = z;
    }

    public void setViewDistance(int viewDistance)
    {
        int i = this.array.viewDistance;
        int j = adjustViewDistance(viewDistance);

        if (i != j)
        {
            ChunkArray clientchunkprovider$chunkarray = new ChunkArray(j);
            clientchunkprovider$chunkarray.centerX = this.array.centerX;
            clientchunkprovider$chunkarray.centerZ = this.array.centerZ;

            for (int k = 0; k < this.array.chunks.length(); ++k)
            {
                Chunk chunk = this.array.chunks.get(k);

                if (chunk != null)
                {
                    ChunkPos chunkpos = chunk.getPos();

                    if (clientchunkprovider$chunkarray.inView(chunkpos.x, chunkpos.z))
                    {
                        clientchunkprovider$chunkarray.replace(clientchunkprovider$chunkarray.getIndex(chunkpos.x, chunkpos.z), chunk);
                    }
                }
            }

            this.array = clientchunkprovider$chunkarray;
        }
    }

    private static int adjustViewDistance(int p_217254_0_)
    {
        return Math.max(2, p_217254_0_) + 3;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString()
    {
        return "Client Chunk Cache: " + this.array.chunks.length() + ", " + this.getLoadedChunksCount();
    }

    public int getLoadedChunksCount()
    {
        return this.array.loaded;
    }

    public void markLightChanged(LightType type, SectionPos pos)
    {
        Minecraft.getInstance().worldRenderer.markForRerender(pos.getSectionX(), pos.getSectionY(), pos.getSectionZ());
    }

    public boolean canTick(BlockPos pos)
    {
        return this.chunkExists(pos.getX() >> 4, pos.getZ() >> 4);
    }

    public boolean isChunkLoaded(ChunkPos pos)
    {
        return this.chunkExists(pos.x, pos.z);
    }

    public boolean isChunkLoaded(Entity entityIn)
    {
        return this.chunkExists(MathHelper.floor(entityIn.getPosX()) >> 4, MathHelper.floor(entityIn.getPosZ()) >> 4);
    }

    @Override
    public ClientChunkProvider createThreadSafeCopy() {
        IChunkArray arr = extractReferenceArray();
        ClientChunkProvider result = new ClientChunkProvider(world, arr.viewDistance() - 3); // -3 because its adds 3 for no reason lmao
        IChunkArray copyArr = ((IClientChunkProvider) result).extractReferenceArray();
        copyArr.copyFrom(arr);
        if (copyArr.viewDistance() != arr.viewDistance()) {
            throw new IllegalStateException(copyArr.viewDistance() + " " + arr.viewDistance());
        }
        return result;
    }

    @Override
    public IChunkArray extractReferenceArray() {
        return array;
    }
    final class ChunkArray implements IChunkArray
    {
        private final AtomicReferenceArray<Chunk> chunks;
        private final int viewDistance;
        private final int sideLength;
        private volatile int centerX;
        private volatile int centerZ;
        private int loaded;

        private ChunkArray(int viewDistanceIn)
        {
            this.viewDistance = viewDistanceIn;
            this.sideLength = viewDistanceIn * 2 + 1;
            this.chunks = new AtomicReferenceArray<>(this.sideLength * this.sideLength);
        }

        private int getIndex(int x, int z)
        {
            return Math.floorMod(z, this.sideLength) * this.sideLength + Math.floorMod(x, this.sideLength);
        }

        protected void replace(int chunkIndex, @Nullable Chunk chunkIn)
        {
            Chunk chunk = this.chunks.getAndSet(chunkIndex, chunkIn);

            if (chunk != null)
            {
                --this.loaded;
                ClientChunkProvider.this.world.onChunkUnloaded(chunk);
            }

            if (chunkIn != null)
            {
                ++this.loaded;
            }
        }

        protected Chunk unload(int chunkIndex, Chunk chunkIn, @Nullable Chunk replaceWith)
        {
            if (this.chunks.compareAndSet(chunkIndex, chunkIn, replaceWith) && replaceWith == null)
            {
                --this.loaded;
            }

            ClientChunkProvider.this.world.onChunkUnloaded(chunkIn);
            return chunkIn;
        }

        private boolean inView(int x, int z)
        {
            return Math.abs(x - this.centerX) <= this.viewDistance && Math.abs(z - this.centerZ) <= this.viewDistance;
        }

        @Nullable
        protected Chunk get(int chunkIndex)
        {
            return this.chunks.get(chunkIndex);
        }

        @Override
        public void copyFrom(IChunkArray other) {
            centerX = other.centerX();
            centerZ = other.centerZ();

            AtomicReferenceArray<Chunk> copyingFrom = other.getChunks();
            for (int k = 0; k < copyingFrom.length(); ++k) {
                Chunk chunk = copyingFrom.get(k);
                if (chunk != null) {
                    ChunkPos chunkpos = chunk.getPos();
                    if (inView(chunkpos.x, chunkpos.z)) {
                        int index = getIndex(chunkpos.x, chunkpos.z);
                        if (chunks.get(index) != null) {
                            throw new IllegalStateException("Doing this would mutate the client's REAL loaded chunks?!");
                        }
                        replace(index, chunk);
                    }
                }
            }
        }
        @Override
        public int centerX() {
            return centerX;
        }

        @Override
        public int centerZ() {
            return centerZ;
        }

        @Override
        public int viewDistance() {
            return viewDistance;
        }

        @Override
        public AtomicReferenceArray<Chunk> getChunks() {
            return chunks;
        }
    }
}
