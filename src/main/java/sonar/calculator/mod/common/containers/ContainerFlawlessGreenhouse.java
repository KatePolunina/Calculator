package sonar.calculator.mod.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IPlantable;
import sonar.calculator.mod.common.tileentity.machines.TileEntityFlawlessGreenhouse;
import sonar.core.inventory.ContainerSync;
import sonar.core.inventory.TransferSlotsManager;

import javax.annotation.Nonnull;

public class ContainerFlawlessGreenhouse extends ContainerSync {

	private TileEntityFlawlessGreenhouse entity;
	public static TransferSlotsManager<TileEntityFlawlessGreenhouse> transfer = new TransferSlotsManager() {
		{
			addTransferSlot(TransferSlotsManager.DISCHARGE_SLOT);
			addTransferSlot(new TransferSlots<TileEntityFlawlessGreenhouse>(TransferType.TILE_INV, 9) {
                @Override
				public boolean canInsert(EntityPlayer player, TileEntityFlawlessGreenhouse inv, Slot slot, int pos, int slotID, ItemStack stack) {
					return stack.getItem() instanceof IPlantable;
				}
			});
			addPlayerInventory();
		}
	};

	public ContainerFlawlessGreenhouse(InventoryPlayer inventory, TileEntityFlawlessGreenhouse entity) {
		super(entity);
		this.entity = entity;

		addSlotToContainer(new Slot(entity, 0, 26, 61));
		for (int j = 0; j < 9; j++) {
			addSlotToContainer(new Slot(entity, 1 + j, 8 + j * 18, 88));
		}
		addInventory(inventory, 8, 110);
	}

	@Nonnull
    @Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		return transfer.transferStackInSlot(this, entity, player, slotID);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return entity.isUsableByPlayer(player);
	}
}
