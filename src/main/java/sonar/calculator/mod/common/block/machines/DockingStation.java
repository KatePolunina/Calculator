package sonar.calculator.mod.common.block.machines;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.common.tileentity.machines.TileEntityDockingStation;
import sonar.calculator.mod.utils.helpers.CalculatorHelper;
import sonar.core.api.utils.BlockInteraction;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.common.block.SonarMachineBlock;
import sonar.core.common.block.SonarMaterials;
import sonar.core.helpers.FontHelper;
import sonar.core.upgrades.MachineUpgrade;
import sonar.core.utils.IGuiTile;

import javax.annotation.Nonnull;
import java.util.List;

public class DockingStation extends SonarMachineBlock {

	public DockingStation() {
		super(SonarMaterials.machine, true, true);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.95F, 1.0F);
	}

	@Override
	public boolean operateBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, BlockInteraction interact) {
		if (player != null) {
			if (interact.type == BlockInteractionType.RIGHT) {
				player.getHeldItemMainhand();
				if (player.getHeldItemMainhand().getItem() instanceof MachineUpgrade) {
					return false;
				}
				if (!insertCalculator(player, world, pos)) {
					if (!world.isRemote) {
						TileEntity target = world.getTileEntity(pos);
						if (target instanceof TileEntityDockingStation) {
							TileEntityDockingStation station = (TileEntityDockingStation) target;
							if (TileEntityDockingStation.getInputStackSize(station.calcStack) != 0) {
								player.openGui(Calculator.instance, IGuiTile.ID, world, pos.getX(), pos.getY(), pos.getZ());
							} else {
								FontHelper.sendMessage(FontHelper.translate("docking.noCalculator"), world, player);
							}
						}
					}
				}
			}
		}
		return true;
	}

	public boolean insertCalculator(EntityPlayer player, World world, BlockPos pos) {
		player.getHeldItemMainhand();
		if (TileEntityDockingStation.getInputStackSize(player.getHeldItemMainhand()) > 0) {
			TileEntity target = world.getTileEntity(pos);
			if (target instanceof TileEntityDockingStation) {
				TileEntityDockingStation station = (TileEntityDockingStation) target;
				if (station.calcStack.isEmpty()) {
					station.calcStack = player.getHeldItemMainhand().copy();
					player.getHeldItemMainhand().shrink(1);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(@Nonnull World var1, int var2) {
		return new TileEntityDockingStation();
	}

	@Override
	public void addSpecialToolTip(ItemStack stack, World player, List<String> list, NBTTagCompound tag) {
		CalculatorHelper.addEnergytoToolTip(stack, player, list);
	}
	@Override
	public boolean hasSpecialRenderer() {
		return true;
	}

	@Nonnull
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.INVISIBLE;
	}
}
