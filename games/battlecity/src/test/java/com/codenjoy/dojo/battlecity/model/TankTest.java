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

import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TankTest {

    private static final int AMMO_COUNT = 6;

    private int ticksPerBullets;
    private Battlecity game;
    private int size;


    @Before
    public void init() {
        size = 7;
        ticksPerBullets = 1;
    }

    @Test
    public void ammoCountAfterTwoShot() {
        givenGameWithTankAt(1, 1);
        Tank tank = game.getTanks().get(0);

        assertTrue(tank.getAmmunition().enoughAmmo());
        tank.act();
        game.tick();
        tank.act();
        game.tick();

        assertEquals(4, tank.getAmmunition().getAmmoCount());
    }

    @Test
    public void ammoIsEmpty(){
        givenGameWithTankAt(1, 1);
        Tank tank = game.getTanks().get(0);
        tank.getAmmunition().setAmmoCount(0);
        assertFalse(tank.getAmmunition().enoughAmmo());

        tank.act();
        game.tick();

        assertEquals(Collections.emptyList(),tank.getBullets());


    }


    public Tank tank(int x, int y, Direction direction) {
        Dice dice = getDice(x, y);
        return new Tank(x, y, direction, dice, ticksPerBullets, null);
    }

    private static Dice getDice(int x, int y) {
        Dice dice = mock(Dice.class);
        when(dice.next(anyInt())).thenReturn(x, y);
        return dice;
    }

    private Player initPlayer(Battlecity game, Tank tank) {
        Player player = mock(Player.class);
        when(player.getTank()).thenReturn(tank);
        tank.setField(game);
        game.newGame(player);
        return player;
    }

    public void givenGameWithTankAt(int x, int y) {
        givenGameWithTankAt(x, y, Direction.UP);
    }

    private void givenGame(Tank tank, Construction... constructions) {
        game = new Battlecity(null, null, null);
        initPlayer(game, tank);
    }

    public void givenGameWithTankAt(int x, int y, Direction direction) {
        givenGame(tank(x, y, direction), new Construction[]{});
    }
}
