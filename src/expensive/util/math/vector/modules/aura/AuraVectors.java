package expensive.util.math.vector.modules.aura;

import expensive.util.client.main.IMinecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.util.math.MathHelper.clamp;

public class AuraVectors implements IMinecraft {

    public static double getDistanceEyePos(Entity target) {
        Vector3d closestHitboxPoint = getClosestVec(target);
        return mc.player.getEyePosition(1.0f).distanceTo(closestHitboxPoint);
    }

    public static Vector3d getVector(LivingEntity target) {
        double wHalf = target.getWidth() / 2;
        double yExpand = clamp(target.getPosYEye() - target.getPosY(), 0, target.getHeight());
        double xExpand = clamp(mc.player.getPosX() - target.getPosX(), -wHalf, wHalf);
        double zExpand = clamp(mc.player.getPosZ() - target.getPosZ(), -wHalf, wHalf);

        return new Vector3d(
                target.getPosX() - mc.player.getPosX() + xExpand,
                target.getPosY() - mc.player.getPosYEye() + yExpand,
                target.getPosZ() - mc.player.getPosZ() + zExpand
        );
    }


    public static Vector3d getSpookyVector(LivingEntity target) {
        double yExpand = MathHelper.clamp(target.getPosYEye() - target.getPosY(), 0, target.getHeight());
        double xExpand = MathHelper.clamp(mc.player.getPosX() - target.getPosX(), -0, 0);
        double zExpand = MathHelper.clamp(mc.player.getPosZ() - target.getPosZ(), -0, 0);

        return new Vector3d(
                target.getPosX() - mc.player.getPosX() + xExpand,
                target.getPosY() - mc.player.getPosYEye() + yExpand,
                target.getPosZ() - mc.player.getPosZ() + zExpand
        );
    }

    public static Vector3d getBestVec3d(final Vector3d pos, final AxisAlignedBB axisAlignedBB) {
        double lastDistance = Double.MAX_VALUE;
        Vector3d bestVec = null;

        final double xWidth = axisAlignedBB.maxX - axisAlignedBB.minX;
        final double zWidth = axisAlignedBB.maxZ - axisAlignedBB.minZ;
        final double height = axisAlignedBB.maxY - axisAlignedBB.minY;

        for (float x = 0F; x < 1F; x += 0.1F) {
            for (float y = 0F; y < 1F; y += 0.1F) {
                for (float z = 0F; z < 1F; z += 0.1F) {
                    final Vector3d hitVec = new Vector3d(
                            axisAlignedBB.minX + xWidth * x,
                            axisAlignedBB.minY + height * y,
                            axisAlignedBB.minZ + zWidth * z
                    );

                    final double distance = pos.distanceTo(hitVec);

                    if (isHitBoxNotVisible(hitVec) && distance < lastDistance) {
                        bestVec = hitVec;
                        lastDistance = distance;
                    }
                }
            }
        }

        return bestVec;
    }

    public static Vector3d getClosestVec(Entity entity) {
        Vector3d eyePosVec = mc.player.getEyePosition(1.0F);
        AxisAlignedBB boundingBox = entity.getBoundingBox();
        return new Vector3d(
                clamp(eyePosVec.getX(), boundingBox.minX, boundingBox.maxX),
                clamp(eyePosVec.getY(), boundingBox.minY, boundingBox.maxY),
                clamp(eyePosVec.getZ(), boundingBox.minZ, boundingBox.maxZ)
        );
    }

    public static Vector3d randomHitBox(AxisAlignedBB axisAlignedBB) {
        double randomX = ThreadLocalRandom.current().nextDouble(axisAlignedBB.minX, axisAlignedBB.maxX);
        double randomY = ThreadLocalRandom.current().nextDouble(axisAlignedBB.minY, axisAlignedBB.maxY);
        double randomZ = ThreadLocalRandom.current().nextDouble(axisAlignedBB.minZ, axisAlignedBB.maxZ);
        return new Vector3d(randomX, randomY, randomZ);
    }


    public static Vector3d getBestVector(LivingEntity target) {
        double lastDistance = Double.MAX_VALUE;
        Vector3d bestVec = null;
        Vector3d hitVec = target.getPositionVec();
        AxisAlignedBB axisAlignedBB = target.getBoundingBox();

        Vector3d predictedPos = target.getPositionVec().add(target.getMotion().scale(1));
        predictedPos = predictedPos.subtract(0, 0.5 * 0.05 * 1 * 2, 0);

        double yExpand = clamp(mc.player.getPosYEye() - target.getPosYEye(), target.getHeight() / 2, target.getHeight())
                / (mc.player.isElytraFlying() ? 10 : !mc.gameSettings.keyBindJump.isKeyDown() && mc.player.isOnGround() ?
                target.isSneaking() ? 0.8F : 0.6f : 1F);

        double xWidth = (axisAlignedBB.maxX) - (axisAlignedBB.minX);
        double zWidth = (axisAlignedBB.maxZ) - (axisAlignedBB.minZ);

        for (float x = (float) -xWidth; x < xWidth; x += (float) (xWidth / 5f)) {
            for (float z = (float) -zWidth; z < zWidth; z += (float) (zWidth / 5f)) {
                hitVec = new Vector3d(
                        target.getPosX() + xWidth * x - 0.1f,
                        target.getPosY() + yExpand,
                        target.getPosZ() + zWidth * z - 0.1f);

                final double distance = mc.player.getPositionVec().distanceTo(hitVec);

                if (!isHitBoxNotVisible(hitVec) && rayTraceEntities(target, hitVec) && distance < lastDistance) {
                    bestVec = hitVec;
                    lastDistance = distance;
                }
            }
        }

        Vector3d finalVector = mc.player.getDistance(target) < 0 ? target.getPositionVec().add(0, yExpand, 0) : bestVec != null ? bestVec : hitVec;
        Vector3d returnedVector = mc.player.isElytraFlying() ? predictedPos : finalVector;
        return returnedVector.subtract(mc.player.getEyePosition(1)).normalize();
    }

    public static Vector3d getEdgeVector(LivingEntity target) {
        AxisAlignedBB bb = target.getBoundingBox();
        Vector3d eyePos = mc.player.getEyePosition(1.0f);
        double minDistance = Double.MAX_VALUE;
        Vector3d bestEdge = null;
        double[][] edges = {
                {bb.minX, bb.minY, bb.minZ}, {bb.minX, bb.minY, bb.maxZ},
                {bb.minX, bb.maxY, bb.minZ}, {bb.minX, bb.maxY, bb.maxZ},
                {bb.maxX, bb.minY, bb.minZ}, {bb.maxX, bb.minY, bb.maxZ},
                {bb.maxX, bb.maxY, bb.minZ}, {bb.maxX, bb.maxY, bb.maxZ}
        };
        for (double[] edge : edges) {
            Vector3d edgeVec = new Vector3d(edge[0], edge[1], edge[2]);
            double distance = eyePos.distanceTo(edgeVec);
            if (distance < minDistance) {
                minDistance = distance;
                bestEdge = edgeVec;
            }
        }
        return bestEdge.subtract(eyePos).normalize();
    }


    public static Vector3d getFullyRandomizedVector(LivingEntity target) {
        Vector3d eyePos = mc.player.getEyePosition(1.0f);
        AxisAlignedBB bb = target.getBoundingBox();
        Vector3d randomVec = randomHitBox(bb);
        return randomVec.subtract(eyePos).normalize();
    }

    public static boolean rayTraceEntities(LivingEntity target, Vector3d hitVec) {
        List<Entity> entities = mc.world.getEntitiesInAABBexcluding(mc.player,
                new AxisAlignedBB(
                        Math.min(mc.player.getPosX(), hitVec.x),
                        Math.min(mc.player.getPosY(), hitVec.y),
                        Math.min(mc.player.getPosZ(), hitVec.z),
                        Math.max(mc.player.getPosX(), hitVec.x),
                        Math.max(mc.player.getPosY(), hitVec.y),
                        Math.max(mc.player.getPosZ(), hitVec.z)
                ),
                entity -> entity instanceof LivingEntity && entity != target
        );

        for (Entity entity : entities) {
            Optional<Vector3d> result = entity.getBoundingBox().rayTrace(mc.player.getEyePosition(1F), hitVec);
            if (result.isPresent()) {
                return true;
            }
        }

        return false;
    }

    public static boolean isHitBoxNotVisible(final Vector3d vec3d) {
        final RayTraceContext rayTraceContext = new RayTraceContext(
                mc.player.getEyePosition(1F),
                vec3d,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                mc.player
        );
        final BlockRayTraceResult blockHitResult = mc.world.rayTraceBlocks(rayTraceContext);
        return blockHitResult.getType() == RayTraceResult.Type.MISS;
    }

    public static double getStrictDistance(Entity entity) {
        return getDistanceEyePos(entity);
    }
}