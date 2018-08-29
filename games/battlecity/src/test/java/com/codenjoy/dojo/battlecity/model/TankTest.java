package com.codenjoy.dojo.battlecity.model;

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

        tank.act();
        game.tick();

        assertFalse(tank.getAmmunition().enoughAmmo());
        assertEquals(Collections.emptyList(),tank.getBullets());


    }


    public Tank tank(int x, int y, Direction direction) {
        Dice dice = getDice(x, y);
        return new Tank(x, y, direction, dice, ticksPerBullets,AMMO_COUNT);
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
        game = new Battlecity(size, Arrays.asList(constructions));
        initPlayer(game, tank);
    }

    public void givenGameWithTankAt(int x, int y, Direction direction) {
        givenGame(tank(x, y, direction), new Construction[]{});
    }


}