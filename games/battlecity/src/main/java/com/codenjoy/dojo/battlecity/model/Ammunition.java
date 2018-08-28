package com.codenjoy.dojo.battlecity.model;

public class Ammunition {

    private static final int AMMO_BONUS = 5; //While the game did not enter bonus ammo

    private int ammoCount/* = 3*/;

    public Ammunition(int ammoCount) {
        this.ammoCount = ammoCount;
    }

    public void replenishAmmo(){
        ammoCount += AMMO_BONUS;
    }

    public void ammoAfterShotDecrement(){
        ammoCount--;
    }

    public int getAmmoCount() {
        return ammoCount;
    }

    public void setAmmoCount(int ammoCount) {
        this.ammoCount = ammoCount;
    }

    public boolean enoughAmmo(){
       return ammoCount > 0; 
    }

//    public Tank getOwner() {
//        return owner;
//    }
}
