package sonar.calculator.mod.common.tileentity.machines;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.CalculatorConfig;
import sonar.calculator.mod.client.gui.machines.GuiAtomicMultiplier;
import sonar.calculator.mod.common.containers.ContainerAtomicMultiplier;
import sonar.calculator.mod.utils.AtomicMultiplierBlacklist;
import sonar.core.api.energy.EnergyMode;
import sonar.core.api.machines.IProcessMachine;
import sonar.core.common.tileentity.TileEntityEnergyInventory;
import sonar.core.energy.DischargeValues;
import sonar.core.helpers.FontHelper;
import sonar.core.inventory.SonarInventory;
import sonar.core.network.sync.SyncTagType;
import sonar.core.utils.IGuiTile;

import javax.annotation.Nonnull;
import java.util.List;

public class TileEntityAtomicMultiplier extends TileEntityEnergyInventory implements ISidedInventory, IProcessMachine, IGuiTile {

	public SyncTagType.INT cookTime = new SyncTagType.INT(0);
	public SyncTagType.INT active = new SyncTagType.INT(1);

	private static final int[] input = new int[] { 0 };
	private static final int[] circuits = new int[] { 1, 2, 3, 4, 5, 6, 7 };
	private static final int[] output = new int[] { 8 };

	public TileEntityAtomicMultiplier() {
		super.storage.setCapacity(CalculatorConfig.ATOMIC_MULTIPLIER_STORAGE);
		super.storage.setMaxTransfer(CalculatorConfig.ATOMIC_MULTIPLIER_TRANSFER_RATE);
		super.inv = new SonarInventory(this, 10);
		super.energyMode = EnergyMode.RECIEVE;
		super.CHARGING_RATE = CalculatorConfig.ATOMIC_MULTIPLIER_TRANSFER_RATE;
		syncList.addParts(cookTime, active, inv);
	}

	@Override
	public void update() {
		super.update();
		discharge(9);
		if (this.cookTime.getObject() > 0) {
			this.active.setObject(1);
			this.cookTime.increaseBy(1);
			int energy = CalculatorConfig.ATOMIC_MULTIPLIER_USAGE / CalculatorConfig.ATOMIC_MULTIPLIER_SPEED;
			this.storage.modifyEnergyStored(-energy);
		}
		if (this.canCook()) {
			if (!this.world.isRemote) {
				if (cookTime.getObject() == 0) {
					this.cookTime.increaseBy(1);
				}
			}
			if (this.cookTime.getObject() >= CalculatorConfig.ATOMIC_MULTIPLIER_SPEED) {

				this.cookTime.setObject(0);
				this.cookItem();
				this.active.setObject(0);

				int energy = CalculatorConfig.ATOMIC_MULTIPLIER_USAGE / CalculatorConfig.ATOMIC_MULTIPLIER_SPEED;
				this.storage.modifyEnergyStored(-energy);
				markBlockForUpdate();
			}
		} else {
			if (this.cookTime.getObject() != 0 || this.active.getObject() != 0) {
				this.cookTime.setObject(0);
				this.active.setObject(0);
				markBlockForUpdate();
			}
		}

		this.markDirty();
	}

	public boolean canCook() {
		if (this.storage.getEnergyStored() == 0) {
			return false;
		}
		for (int i = 0; i < 8; i++) {
			if (slots().get(i).isEmpty()) {
				return false;
			}
		}
		if (!isAllowed(slots().get(0))) {
			return false;
		}
		ItemStack output = slots().get(8);
		if (!output.isEmpty()) {
			if (output.getCount() + 4 > 64) {
				return false;
			}
			if (!slots().get(0).isItemEqual(output)) {
				return false;
			}
		}

		if (cookTime.getObject() == 0) {
			if (this.storage.getEnergyStored() < CalculatorConfig.ATOMIC_MULTIPLIER_USAGE) {
				return false;
			}
		}
		if (!(slots().get(0).getMaxStackSize() >= 4)) {
			return false;
		}

		for (int i = 1; i < 8; i++) {
			if (slots().get(i).getItem() != Calculator.circuitBoard) {
				return false;
			}
		}

		if (cookTime.getObject() >= CalculatorConfig.ATOMIC_MULTIPLIER_SPEED) {
			return true;
		}
		return true;
	}

	public static boolean isAllowed(ItemStack stack) {
		return AtomicMultiplierBlacklist.blacklist().isAllowed(stack.getItem());
	}

	private void cookItem() {
		ItemStack itemstack = new ItemStack(slots().get(0).getItem(), 4, slots().get(0).getItemDamage());
		ItemStack output = slots().get(8);
		if (output.isEmpty()) {
			slots().set(8, itemstack);
		} else if (output.isItemEqual(itemstack)) {
			output.grow(4);
		}

		for (int i = 0; i < 8; i++) {
			slots().get(i).shrink(1);
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
		if (0 < slot && slot < 8) {
            return stack.getItem() == Calculator.circuitBoard;
		} else if (slot == 0) {
            return stack.getMaxStackSize() >= 4;
		} else return slot == 9 && DischargeValues.getValueOf(stack) > 0;
    }

	@Nonnull
    @Override
	public int[] getSlotsForFace(@Nonnull EnumFacing side) {
		return EnumFacing.DOWN == side ? output : EnumFacing.UP == side ? input : EnumFacing.VALUES[2] == side ? circuits : EnumFacing.VALUES[3] == side ? circuits : EnumFacing.VALUES[4] == side ? circuits : EnumFacing.VALUES[5] == side ? circuits : input;
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
		return this.isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack stack, @Nonnull EnumFacing direction) {
        return slot == 8;
	}

    @Override
	public boolean receiveClientEvent(int action, int param) {
		if (action == 1) {
			markBlockForUpdate();
		}
		return true;
	}

    @Override
	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip, IBlockState state) {
		super.getWailaInfo(currenttip, state);
		if (cookTime.getObject() > 0) {
			String active = FontHelper.translate("locator.state") + ": " + FontHelper.translate("locator.active");
			currenttip.add(active);
		} else {
			String idle = FontHelper.translate("locator.state") + ": " + FontHelper.translate("locator.idle");
			currenttip.add(idle);
		}
		return currenttip;
	}

	@Override
	public int getCurrentProcessTime() {
		return cookTime.getObject();
	}

	@Override
	public int getProcessTime() {
		return CalculatorConfig.ATOMIC_MULTIPLIER_SPEED;
	}

	@Override
	public double getEnergyUsage() {
        return CalculatorConfig.ATOMIC_MULTIPLIER_USAGE / CalculatorConfig.ATOMIC_MULTIPLIER_SPEED;
	}

	@Override
	public Object getGuiContainer(EntityPlayer player) {
		return new ContainerAtomicMultiplier(player.inventory, this);
	}

	@Override
	public Object getGuiScreen(EntityPlayer player) {
		return new GuiAtomicMultiplier(player.inventory, this);
	}

	@Override
	public int getBaseProcessTime() {
		return CalculatorConfig.ATOMIC_MULTIPLIER_SPEED;
	}
}
