package toniqx.vailware.main.bypass;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public class BlockData {
    public static BlockPos position;
    public static EnumFacing face;
    public Vec3 hitVec;

    public BlockData(BlockPos position, EnumFacing face) {
        this.position = position;
        this.face = face;
        this.hitVec = getHitVec();
    }

    public static EnumFacing getFacing() {
        return face;
    }

    public static BlockPos getPosition() {
        return position;
    }

    private Vec3 getHitVec() {
        final Vec3i directionVec = face.getDirectionVec();
        double x = directionVec.getX() * 0.5D;
        double z = directionVec.getZ() * 0.5D;

        if (face.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE) {
            x = -x;
            z = -z;
        }

        final Vec3 hitVec = new Vec3(position).addVector(x + z, directionVec.getY() * 0.5D, x + z);

        final Vec3 src = Minecraft.getMinecraft().thePlayer.getPositionEyes(1.0F);
        final MovingObjectPosition obj = Minecraft.getMinecraft().theWorld.rayTraceBlocks(src,
                hitVec,
                false,
                false,
                true);

        if (obj == null || obj.hitVec == null || obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK)
            return null;

        switch (face.getAxis()) {
            case Z:
                obj.hitVec = new Vec3(obj.hitVec.xCoord, obj.hitVec.yCoord, Math.round(obj.hitVec.zCoord));
                break;
            case X:
                obj.hitVec = new Vec3(Math.round(obj.hitVec.xCoord), obj.hitVec.yCoord, obj.hitVec.zCoord);
                break;
		default:
			break;
        }

        if (face != EnumFacing.DOWN && face != EnumFacing.UP) {
            final IBlockState blockState = Minecraft.getMinecraft().theWorld.getBlockState(obj.getBlockPos());
            final Block blockAtPos = blockState.getBlock();

            double blockFaceOffset;

            if (blockAtPos instanceof BlockSlab && !((BlockSlab) blockAtPos).isDouble()) {
                final BlockSlab.EnumBlockHalf half = blockState.getValue(BlockSlab.HALF);

                blockFaceOffset = org.apache.commons.lang3.RandomUtils.nextDouble(0.1, 0.4);

                if (half == BlockSlab.EnumBlockHalf.TOP) {
                    blockFaceOffset += 0.5;
                }
            } else {
                blockFaceOffset = org.apache.commons.lang3.RandomUtils.nextDouble(0.1, 0.9);
            }

            obj.hitVec = obj.hitVec.addVector(0.0D, -blockFaceOffset, 0.0D);
        }

        return obj.hitVec;
    }
}