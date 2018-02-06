package com.ugleh.redstoneproximitysensor.addons;

import org.bukkit.entity.Entity;

import com.ugleh.redstoneproximitysensor.utils.RPS;

public abstract class AddonTemplate {
	public abstract boolean checkTrigger(RPS rps, Entity e);
	public abstract void buttonPressed(Boolean on, RPS affectedRPS);
	public abstract void rpsCreated(RPS affectedRPS);
	public abstract void rpsRemoved(RPS affectedRPS);
	
}
