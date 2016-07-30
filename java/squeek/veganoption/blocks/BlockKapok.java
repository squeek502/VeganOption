package squeek.veganoption.blocks;

import net.minecraft.block.BlockColored;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class BlockKapok extends BlockColored
{
    public BlockKapok(Material materialIn) {
        super(materialIn);
        setSoundType(SoundType.CLOTH);
    }
}
