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


import com.codenjoy.dojo.services.LengthToXY;
import com.codenjoy.dojo.services.settings.Parameter;

import java.util.List;
import java.util.Random;

public class HedgeHogController {

    private Field fieldController;
    private final Parameter<Integer> maxHedgeHogsOnMap;
    private final Parameter<Integer> ticksToUpdateHedgehogs;
    private final Parameter<Integer> maxHedgehogLifetime;
    private final Parameter<Integer> minHedgehogLifetime;
    private Random random = new Random();
    private int tick;
    private boolean startGame = true;
    private List<HedgeHog> hedgeHogs;


    public HedgeHogController(Field fieldController, GameSettings gameSettings, List<HedgeHog> hedgeHogs) {
        this.fieldController = fieldController;
        // settings from admin's page
        ticksToUpdateHedgehogs = gameSettings.getTicksToUpdateHedgehogs();
        maxHedgeHogsOnMap = gameSettings.getMaxHedgeHogsOnMap();
        maxHedgehogLifetime = gameSettings.getMaxHedgehogLifetime();
        minHedgehogLifetime = gameSettings.getMinHedgehogLifetime();
        this.hedgeHogs = hedgeHogs;
    }

    public void refreshHedgeHogs() {
        tick();
        removeDeadHedgeHogs();
        createNewHedgeHogs();
    }

    private void createNewHedgeHogs() {
        LengthToXY xy = new LengthToXY(fieldController.size());
        final int numberOfHedgehogsForCreation =
                maxHedgeHogsOnMap.getValue() - fieldController.getHedgeHogs().size();
        int currentHedgeHogCount = 0;
        int lifeCount = 0;
        boolean fieldOccupied;
        int coverage = fieldController.size() * fieldController.size();

        if (tick >= ticksToUpdateHedgehogs.getValue() || startGame) {
            while (currentHedgeHogCount < numberOfHedgehogsForCreation) {

                int index = random.nextInt(coverage);
                lifeCount = minHedgehogLifetime.getValue() + random.nextInt(maxHedgehogLifetime.getValue()); // время жизни для вновь созданных ежей
                fieldOccupied = fieldController.isFieldOccupied(xy.getXY(index).getX(), xy.getXY(index).getY());
                if (!fieldOccupied) {
                    hedgeHogs.add(new HedgeHog(xy.getXY(index), lifeCount));
                    currentHedgeHogCount++;
                }
            }
            tick = 0;
            startGame = false;
        } else {
            tick++;
        }

    }

    private void removeDeadHedgeHogs() {
        hedgeHogs.removeIf(h -> !h.isAlive());
    }

    private void tick() {
        hedgeHogs.forEach(HedgeHog::tick);
    }


}
