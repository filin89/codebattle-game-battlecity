package com.codenjoy.dojo.battlecity.model;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 - 2018 Codenjoy
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.codenjoy.dojo.services.settings.Parameter;

public class FiniteAmmo implements Ammo {

    private Parameter<Integer> initialAmmo;
    private int availableAmmo;

    public FiniteAmmo(Parameter<Integer> initialAmmo) {
        this.initialAmmo = initialAmmo;
        availableAmmo = initialAmmo.getValue();
    }

    @Override
    public boolean hasAvailableAmmo() {
        return availableAmmo > 0;
    }

    @Override
    public void onFire() {
        if (availableAmmo > 0) {
            availableAmmo--;
        }
    }

    @Override
    public void addAmmo(int ammoToAdd) {
        availableAmmo += ammoToAdd;
    }

    @Override
    public void refreshAmmo() {
        availableAmmo = initialAmmo.getValue();
    }
}
