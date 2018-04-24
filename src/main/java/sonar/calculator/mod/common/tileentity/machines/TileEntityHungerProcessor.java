package sonar.calculator.mod.common.tileentity.machines;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.calculator.mod.api.machines.ProcessType;
import sonar.calculator.mod.api.nutrition.IHungerProcessor;
import sonar.calculator.mod.api.nutrition.IHungerStore;
import sonar.calculator.mod.client.gui.machines.GuiHungerProcessor;
import sonar.calculator.mod.common.containers.ContainerHungerProcessor;
import sonar.core.common.tileentity.TileEntitySidedInventory;
import sonar.core.helpers.FontHelper;
import sonar.core.inventory.SonarInventory;
import sonar.core.network.sync.SyncTagType;
import sonar.core.utils.IGuiTile;

import java.util.List;

public class TileEntityHungerProcessor extends TileEntitySidedInventory implements IGuiTile, IHungerProcessor {

	public SyncTagType.INT storedpoints = new SyncTagType.INT(0);
	public final int speed = 4;

	public TileEntityHungerProcessor() {
		super.input = new int[] { 0 };
		super.output = new int[] { 1 };
		super.inv = new SonarInventory(this, 2);
		syncList.addParts(storedpoints, inv);
	}

	@Override
	public void update() {
		super.update();
		if (!this.world.isRemote)
			food(slots().get(0));
		charge(slots().get(1));

		this.markDirty();
	}

	public void charge(ItemStack stack) {
        if (!stack.isEmpty() && this.storedpoints.getObject() != 0) {
			if (stack.getItem() instanceof IHungerStore) {
				IHungerStore module = (IHungerStore) stack.getItem();
				int hunger = module.getHungerPoints(stack);
				int max = module.getMaxHungerPoints(stack);
				if (!(hunger >= max) || max == -1) {
					if (storedpoints.getObject() >= speed) {
						if (max == -1 || max >= hunger + speed) {
							module.transferHunger(speed, stack, ProcessType.ADD);
							storedpoints.increaseBy(-speed);
						} else {
							module.transferHunger(max - hunger, stack, ProcessType.ADD);
							storedpoints.increaseBy(-(max - hunger));
						}
					} else if (storedpoints.getObject() <= speed) {
						if (max == -1 | max >= hunger + speed) {
							module.transferHunger(speed, stack, ProcessType.ADD);
							storedpoints.setObject(0);
						} else {
							module.transferHunger(max - hunger, stack, ProcessType.ADD);
							storedpoints.increaseBy(-(max - hunger));
						}
					}
				}
			}
		}
	}

	private void food(ItemStack stack) {
        if (!stack.isEmpty()) {
			if (stack.getItem() instanceof ItemFood) {
				ItemFood food = (ItemFood) stack.getItem();
				storedpoints.increaseBy(food.getHealAmount(stack));
				this.slots().get(0).shrink(1);
			}
			if (stack.getItem() instanceof IHungerStore) {

				IHungerStore module = (IHungerStore) stack.getItem();
				int hunger = module.getHungerPoints(stack);

				if (hunger != 0) {
					if (hunger >= speed) {
						module.transferHunger(speed, stack, ProcessType.REMOVE);
						storedpoints.increaseBy(speed);
					} else {
						module.transferHunger(hunger, stack, ProcessType.REMOVE);
						storedpoints.increaseBy(hunger);
					}
				}
			}
		}
	}
	
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing side) {
		return this.isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing side) {
		if (slot == 1) {
			if (this.storedpoints.getObject() == 0) {
				return true;
			}
            return this.storedpoints.getObject() == 0;
		}
		return true;
	}

    @Override
	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip, IBlockState state) {
		currenttip.add(FontHelper.translate("points.hunger") + ": " + storedpoints);
		return currenttip;
	}

	public int getHungerPoints() {
		return storedpoints.getObject();
	}

	@Override
	public Object getGuiContainer(EntityPlayer player) {
		return new ContainerHungerProcessor(player.inventory, this);
	}

	@Override
	public Object getGuiScreen(EntityPlayer player) {
		return new GuiHungerProcessor(player.inventory, this);
	}
}
