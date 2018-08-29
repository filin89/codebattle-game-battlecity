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

import com.codenjoy.dojo.battlecity.model.modes.BattlecityGameModes;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;

import java.util.Arrays;

public class GameSettingsImpl implements GameSettings {

    private Parameter<Integer> initialPlayerAmmoCount;
    private Parameter<Integer> initialAIAmmoCount;
    private Parameter<String> gameModeName;
    private Parameter<String> map;
    private Parameter<Integer> maxHedgeHogsOnMap;
    private Parameter<Integer> ticksToUpdateHedgehogs;
    private Parameter<Integer> maxHedgehogLifetime;
    private Parameter<Integer> minHedgehogLifetime;



    public GameSettingsImpl(Settings settings) {
        initialPlayerAmmoCount = settings.addEditBox("Initial Player Ammo Count").type(Integer.class).def(10);
        initialAIAmmoCount = settings.addEditBox("Initial AI Ammo Count").type(Integer.class).def(5000);
        gameModeName = settings.addSelect("Game Mode",
                Arrays.asList(BattlecityGameModes.CLASSIC.getName(),
                        BattlecityGameModes.PLAYERS_VERSUS_AI.getName(),
                        BattlecityGameModes.PLAYERS_ONLY.getName()))
                .type(String.class).def(BattlecityGameModes.CLASSIC.getName());

        ticksToUpdateHedgehogs = settings.addEditBox("Ticks to update Hedgehogs").type(Integer.class).def(20);
        maxHedgeHogsOnMap = settings.addEditBox("Maximum Hedgehogs on the map ").type(Integer.class).def(20);
        maxHedgehogLifetime = settings.addEditBox("Maximum Hedgehogs lifetime").type(Integer.class).def(30);
        minHedgehogLifetime = settings.addEditBox("Minimum Hedgehogs lifetime").type(Integer.class).def(15);


        map = settings.addEditBox("Map").type(String.class).def("default");
    }

    @Override
    public Parameter<Integer> getInitialPlayerAmmoCount() {
        return initialPlayerAmmoCount;
    }

    @Override
    public Parameter<Integer> getInitialAIAmmoCount() {
        return initialAIAmmoCount;
    }

    @Override
    public Parameter<String> getGameMode() {
        return gameModeName;
    }
    @Override
    public Parameter<String> getMap() {
        return map;
    }

    @Override
    public Parameter<Integer> getMaxHedgeHogsOnMap() {
        return maxHedgeHogsOnMap;
    }

    @Override
    public Parameter<Integer> getTicksToUpdateHedgehogs() {
        return ticksToUpdateHedgehogs;
    }

    @Override
    public Parameter<Integer> getMaxHedgehogLifetime() {
        return maxHedgehogLifetime;
    }

    @Override
    public Parameter<Integer> getMinHedgehogLifetime() {
        return minHedgehogLifetime;
    }
}
