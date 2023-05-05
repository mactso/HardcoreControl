package com.mactso.hardcorecontrol.timer;

import java.time.LocalDateTime;

public class DeathTimeStorage implements IDeathTime {

	Object object;
	private LocalDateTime deadTimeStored;

	public DeathTimeStorage(Object object) {
		this.object = object;
	}

	@Override
	public LocalDateTime getDeadTime() {

		return deadTimeStored;
	}

	@Override
	public void setDeathTime(LocalDateTime ldt) {
		deadTimeStored = ldt;
	}

}
