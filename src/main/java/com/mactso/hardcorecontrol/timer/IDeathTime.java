package com.mactso.hardcorecontrol.timer;

import java.time.LocalDateTime;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IDeathTime
{
	public void setDeathTime(LocalDateTime ldt);
	public LocalDateTime getDeadTime();
}
