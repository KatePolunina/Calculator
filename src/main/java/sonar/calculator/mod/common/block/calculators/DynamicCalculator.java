package sonar.calculator.mod.common.block.calculators;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.common.tileentity.misc.TileEntityCalculator;
import sonar.core.api.utils.BlockInteraction;
import sonar.core.common.block.SonarMachineBlock;
import sonar.core.common.block.SonarMaterials;
import sonar.core.helpers.FontHelper;
import sonar.core.utils.IGuiTile;

import javax.annotation.Nonnull;

public class DynamicCalculator extends SonarMachineBlock {
	public DynamicCalculator() {
		super(SonarMaterials.machine, true, true);
	}

    @Override
	public boolean operateBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, BlockInteraction interact) {
		player.openGui(Calculator.instance, IGuiTile.ID, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	@SideOnly(Side.CLIENT)
	public String localBlockName(String string) {
		if (string.equals("stable")) {
			return FontHelper.translate("tile.StableStone.name");
		}
		if (string.equals("glass")) {
			return FontHelper.translate("tile.StableGlass.name");
		} else {
			return FontHelper.translate("tile.Air.name");
		}
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World world, int i) {
		return new TileEntityCalculator.Dynamic();
	}

	@Override
	public boolean dropStandard(IBlockAccess world, BlockPos pos) {
		return true;
	}
}
