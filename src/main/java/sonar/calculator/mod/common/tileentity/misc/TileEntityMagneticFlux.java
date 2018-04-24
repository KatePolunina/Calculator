package sonar.calculator.mod.common.tileentity.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import sonar.calculator.mod.client.gui.misc.GuiMagneticFlux;
import sonar.calculator.mod.common.containers.ContainerMagneticFlux;
import sonar.core.api.SonarAPI;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.common.tileentity.TileEntityInventory;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;
import sonar.core.inventory.SonarInventory;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.IGuiTile;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class TileEntityMagneticFlux extends TileEntityInventory implements ISidedInventory, IByteBufTile, IGuiTile {

	public boolean whitelisted, exact;
	public Random rand = new Random();
    public float rotate;
	public boolean disabled;

	public TileEntityMagneticFlux() {
		super.inv = new SonarInventory(this, 8);
		syncList.addPart(inv);
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

    @Override
	public void update() {
		super.update();
		if (this.world.isBlockIndirectlyGettingPowered(pos) > 0) {
			disabled = true;
			return;
		}
		disabled = false;
		if (this.world.isRemote) {
			if (!(rotate >= 1)) {
				rotate += (float) 1 / 100;
			} else {
				rotate = 0;
			}
		}
		this.magnetizeItems();
	}

    @Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type.isType(SyncType.DEFAULT_SYNC, SyncType.SAVE)) {
			this.whitelisted = nbt.getBoolean("blacklisted");
			this.exact = nbt.getBoolean("exact");
		}
	}

    @Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type.isType(SyncType.DEFAULT_SYNC, SyncType.SAVE)) {
			nbt.setBoolean("blacklisted", whitelisted);
			nbt.setBoolean("exact", exact);
		}
		return nbt;
	}

	public void magnetizeItems() {
		int range = 10;
		AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range);
		List<EntityItem> items = this.world.getEntitiesWithinAABB(EntityItem.class, aabb, null);
		for (EntityItem entity : items) {
            if (validItemStack(entity.getItem())) {
				double x = pos.getX() + 0.5D - entity.posX;
				double y = pos.getY() + 0.2D - entity.posY;
				double z = pos.getZ() + 0.5D - entity.posZ;

				double distance = Math.sqrt(x * x + y * y + z * z);
				if (distance < 1.5) {
                    ItemStack itemstack = addToInventory(entity);
					if (itemstack.isEmpty() || itemstack.getCount() <= 0) {
						entity.setDead();
					} else {
                        entity.setItem(itemstack);
					}
				} else {
					double speed = entity.isBurning() ? 5.2 : 0.1;
					entity.motionX += x / distance * speed;
					entity.motionY += y * speed;
					if (y > 0) {
						entity.motionY = 0.10;
					}
					entity.motionZ += z / distance * speed;
				}
			}
		}
	}

	public boolean validItemStack(ItemStack stack) {
		for (ItemStack slot : slots()) {
			if (!slot.isEmpty()) {
				boolean matches = matchingStack(slot, stack);
				if (!this.whitelisted && matches) {
					return false;
				} else if (whitelisted && matches) {
					return true;
				}
			}
		}
		return !this.whitelisted;
	}

	public boolean matchingStack(ItemStack stack, ItemStack stack2) {
		if (exact) {
			int[] stackDict = OreDictionary.getOreIDs(stack2);
			int[] storedDict = OreDictionary.getOreIDs(stack);
            for (int aStackDict : stackDict) {
                for (int aStoredDict : storedDict) {
                    if (aStackDict == aStoredDict) {
						return true;
					}
				}
			}
		}
		return stack.getItem() == stack2.getItem() && (exact || stack.getItemDamage() == stack2.getItemDamage()) && (exact || ItemStack.areItemStackTagsEqual(stack, stack2));
	}

	public ItemStack addToInventory(EntityItem item) {
		if (!this.world.isRemote) {
			EntityItem entity = (EntityItem) this.world.getEntityByID(item.getEntityId());
			if (entity == null) {
				return null;
			}
            ItemStack itemstack = entity.getItem();
            int i = itemstack.getCount();
            TileEntity target = SonarHelper.getAdjacentTileEntity(this, EnumFacing.DOWN);
            if (target != null)
                itemstack = SonarAPI.getItemHelper().getStackToAdd(itemstack.getCount(), new StoredItemStack(itemstack), SonarAPI.getItemHelper().addItems(target, new StoredItemStack(itemstack), EnumFacing.getFront(1), ActionType.PERFORM, null)).getFullStack();
            return itemstack;
		}
        return item.getItem();
	}

	@Nonnull
    @Override
	public int[] getSlotsForFace(@Nonnull EnumFacing side) {
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int slot, @Nonnull ItemStack item, @Nonnull EnumFacing side) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, @Nonnull ItemStack item, @Nonnull EnumFacing side) {
		return false;
	}

    @Override
	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip, IBlockState state) {
		if (!disabled) {
			String active = FontHelper.translate("locator.state") + " : " + FontHelper.translate("state.on");
			currenttip.add(active);
		} else {
			String idle = FontHelper.translate("locator.state") + " : " + FontHelper.translate("state.off");
			currenttip.add(idle);
		}

		return currenttip;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			this.whitelisted = !whitelisted;
			buf.writeBoolean(whitelisted);
			break;
		case 1:
			this.exact = !exact;
			buf.writeBoolean(exact);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			whitelisted = buf.readBoolean();
			break;
		case 1:
			exact = buf.readBoolean();
			break;
		}
	}

	@Override
	public Object getGuiContainer(EntityPlayer player) {
		return new ContainerMagneticFlux(player.inventory, this);
	}

	@Override
	public Object getGuiScreen(EntityPlayer player) {
		return new GuiMagneticFlux(player.inventory, this);
	}
}
