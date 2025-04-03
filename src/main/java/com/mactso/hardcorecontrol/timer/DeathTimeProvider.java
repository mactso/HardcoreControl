package com.mactso.hardcorecontrol.timer;

import java.time.LocalDateTime;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

public class DeathTimeProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag>
{
	IDeathTime storage;

	public DeathTimeProvider (ServerPlayer serverPlayerEntity) {
		
		storage = new DeathTimeStorage(serverPlayerEntity);
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		
		if (cap == CapabilityDeathTime.DEATH_TIME)
			return (LazyOptional<T>) LazyOptional.of(() -> storage);
		return LazyOptional.empty();
		
	}
	

@Override
public CompoundTag serializeNBT(Provider registryAccess) {

	CompoundTag ret = new CompoundTag();
		String s = "";
		LocalDateTime ldt = storage.getDeadTime();
		if (ldt != null) {
			s = ldt.toString();
		}
		ret.putString("deathTimeStored", s);
		return ret;
		
	}

	@Override
	public void deserializeNBT(Provider registryAccess, CompoundTag nbt) {

		LocalDateTime ldt = null;
		String s = nbt.getString("deathTimeStored");
		if (!s.isEmpty()) {
			ldt = LocalDateTime.parse(nbt.getString("deathTimeStored"));
		}
		storage.setDeathTime(ldt);
		
	}
}
