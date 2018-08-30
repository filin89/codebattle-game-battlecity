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

import com.codenjoy.dojo.services.Direction;

public class TankParams {

    private static final int DEFAULT_AI_TICKS_PER_BULLETS = 1;

    private int x;
    private int y;
    private Direction direction;
    private int ticksPerBullets;

    private TankParams() {}

    public static TankParams newTankParams(int x, int y, Direction direction, int ticksPerBullets) {
        TankParams instance = new TankParams();

        instance.x = x;
        instance.y = y;
        instance.direction = direction;
        instance.ticksPerBullets = ticksPerBullets;

        return instance;
    }

    public static TankParams newAITankParams(int x, int y, Direction direction) {
        return newTankParams(x, y, direction, DEFAULT_AI_TICKS_PER_BULLETS);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getTicksPerBullets() {
        return ticksPerBullets;
    }
}
