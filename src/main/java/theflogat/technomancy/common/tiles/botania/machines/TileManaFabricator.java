package theflogat.technomancy.common.tiles.botania.machines;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import theflogat.technomancy.common.blocks.base.TMBlocks;
import theflogat.technomancy.common.tiles.base.IWrenchable;
import theflogat.technomancy.common.tiles.base.TileMachineBase;
import theflogat.technomancy.lib.handlers.Rate;
import theflogat.technomancy.util.helpers.WorldHelper;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.mana.IManaPool;

public class TileManaFabricator extends TileMachineBase implements IManaPool, IWrenchable {
	
	public int maxMana = 100000;
	public int mana;
	public int facing;
	public static int cost = Rate.manaFabCost;

	public TileManaFabricator() {
		super(Rate.manaFabCost * 2);
	}
	
	@Override
	public void updateEntity() {
		if(getEnergyStored()>=cost && mana+100<=maxMana) {
			mana += 100;
			extractEnergy(cost, false);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	@Override
	public void writeSyncData(NBTTagCompound compound) {
		super.writeSyncData(compound);
		compound.setInteger("Mana", mana);
		compound.setInteger("Facing", facing);
	}
	
	@Override
	public void readSyncData(NBTTagCompound compound) {
		super.readSyncData(compound);
		mana = compound.getInteger("Mana");
		facing = compound.getInteger("Facing");
	}
	
	@Override
	public boolean isFull() {
		return mana >= maxMana;
	}

	@Override
	public void recieveMana(int mana) {
		this.mana += mana;		
	}

	@Override
	public boolean canRecieveManaFromBursts() {
		return false;
	}

	@Override
	public int getCurrentMana() {
		return mana;
	}

	@Override
	public boolean isOutputtingPower() {
		return false;
	}
	
	public void renderHUD(Minecraft mc, ScaledResolution res) {
		int color = 0x660000FF;
		BotaniaAPI.internalHandler.drawSimpleManaHUD(color, mana, maxMana, TMBlocks.manaFabricator.getLocalizedName(), res);
	}
	
	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return from.ordinal() == facing;
	}

	@Override
	public boolean onWrenched(boolean sneaking) {
		for (int i = facing + 1; i < facing + 6; i++) {
			TileEntity tile = WorldHelper.getAdjacentTileEntity(this, (byte) (i % 6));
			if (WorldHelper.isEnergyHandlerFromOppFacing(tile, (byte) (i % 6))) {
				if(!worldObj.isRemote) {
					facing = (byte) (i % 6);
					worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord));
					worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
				}
				return true;
			}
		}
		return false;
	}

}
