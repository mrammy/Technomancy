package theflogat.technomancy.common.tiles.technom;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import theflogat.technomancy.api.renderers.ModelCatalystSpecial;
import theflogat.technomancy.api.rituals.IRitualEffectHandler;
import theflogat.technomancy.api.rituals.Ritual;
import theflogat.technomancy.api.rituals.RitualRegistry;
import theflogat.technomancy.common.tiles.base.TileTechnomancy;
import theflogat.technomancy.util.Java;

public class TileCatalyst extends TileTechnomancy {

	public int remCount = -1;
	public ModelCatalystSpecial specialRender = null;
	public ResourceLocation textLoc = null;
	public IRitualEffectHandler handler = null;

	@Override
	public void updateEntity() {
		if(worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
			activateRitual();

		if(handler!=null)
			handler.applyEffect(this);

		if(remCount!=-1){
			if(remCount==0){
				worldObj.setBlockToAir(xCoord, yCoord, zCoord);
			}else{
				remCount--;
			}
		}
	}

	public void activateRitual() {
		for(Ritual r : RitualRegistry.getRituals()){
			if(r!=null && r.isCoreComplete(worldObj, xCoord, yCoord, zCoord) && r.isFrameComplete(worldObj, xCoord, yCoord, zCoord)){
				if(r.canApplyEffect(worldObj, xCoord, yCoord, zCoord))
					r.applyEffect(worldObj, xCoord, yCoord, zCoord);
			}
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound comp) {
		remCount = comp.getInteger("remcount");
		if(comp.hasKey("textloc"))
				textLoc = new ResourceLocation(comp.getString("textloc"));
		try{
			specialRender = (ModelCatalystSpecial) Java.getInstanceFromNBT(comp, "specialrender");
			handler = (IRitualEffectHandler) Java.getInstanceFromNBT(comp, "handler");
		}catch(Exception e){e.printStackTrace();}
	}

	@Override
	public void writeCustomNBT(NBTTagCompound comp) {
		comp.setInteger("remcount", remCount);
		Java.saveClassToNBT(comp, "specialrender", specialRender);
		if(textLoc!=null)
			comp.setString("textloc", textLoc.getResourcePath());
		Java.saveClassToNBT(comp, "handler", handler);
	}
}