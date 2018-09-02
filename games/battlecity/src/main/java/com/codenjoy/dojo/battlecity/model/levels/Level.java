package com.codenjoy.dojo.battlecity.model.levels;

/*-
 * #%L
 * Codenjoy - it's a dojo-like platform from developers to developers.
 * %%
 * Copyright (C) 2016 Codenjoy
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


import com.codenjoy.dojo.battlecity.model.*;
import com.codenjoy.dojo.services.*;
import com.codenjoy.dojo.services.printer.BoardReader;

import java.util.*;

public class Level implements Field {

    private final LengthToXY xy;
    private Random random = new Random();
    List<HedgeHog> result = new LinkedList<>();

    int tick;

    private String map =
            "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ " +
            "☼   ¿         ¿         ¿                 ¿         ¿         ¿   ☼ " +
            "☼                                                                 ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬ ☼ ☼ ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬ ☼ ☼ ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬  ͱ  ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬                         ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬           ͱ ͱ ͱ ͱ ͱ ͱ ͱ ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼                         ╬ ╬ ╬ ͱ ͱ ╬ ╬ ╬   ͱ                     ☼ " +
            "☼                         ╬ ╬ ╬ ͱ ͱ ╬ ╬ ╬   ͱ             ͱ       ☼ " +
            "☼           ╬ ╬ ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬   ͱ  ╬ ╬ ╬ ╬ ╬  ͱ       ☼ " +
            "☼ ☼ ☼       ╬ ╬ ╬ ╬ ╬                       ͱ  ╬ ╬ ╬ ╬ ╬  ͱ   ☼ ☼ ☼ " +
            "☼                                           ͱ ͱ ͱ ͱ ͱ ͱ           ☼ " +
            "☼                         ╬ ╬ ╬     ╬ ╬ ╬                         ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬ ͱ   ╬ ╬ ╬   ͱ ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬ ╬ ╬ ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬ ╬ ╬ ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬ ͱ   ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬ ͱ         ͱ                                 ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬                                             ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬               ╬ ╬ ╬ ╬ ╬ ╬ ╬ ╬               ╬ ╬ ╬     ☼ " +
            "☼     ╬ ╬ ╬               ╬ ╬ ╬ ╬ ╬ ╬ ╬ ╬               ╬ ╬ ╬     ☼ " +
            "☼                         ╬ ╬         ╬ ╬                         ☼ " +
            "☼                         ╬ ╬         ╬ ╬                         ☼ " +
            "☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ";

    public Level() {
        removeSpaces();
        xy = new LengthToXY(size());
    }

    private void removeSpaces() {
        String result = "";
        for (int i = 0; i < map.length(); i += 2) {
            result += map.charAt(i);
        }
        map = result;
    }

    @Override
    public int size() {
        return (int) Math.sqrt(map.length());
    }


    // добавление кирпичей на доску
    @Override
    public List<Construction> getConstructions() {
        List<Construction> result = new LinkedList<Construction>();
        for (int index = 0; index < map.length(); index++) {
            if (map.charAt(index) == Elements.CONSTRUCTION.ch) { // если эл на карте из enum эл-ов конструкции
                result.add(new Construction(xy.getXY(index))); // получим для него координаты по формуле, сформируем из них точку PointImpl
            }
        }
        return result;
    }


    @Override
    public List<Tank> getTanks() {
        List<Tank> result = new LinkedList<Tank>();
        for (int index = 0; index < map.length(); index++) {
            if (map.charAt(index) == Elements.AI_TANK_DOWN.ch) { // все танки изначально размещаются направленными вниз
                Point pt = xy.getXY(index);
                result.add(new AITank(pt.getX(), pt.getY(), new RandomDice(), Direction.DOWN));
            }
        }
        return result;
    }

    @Override
    public List<Border> getBorders() {
        List<Border> result = new LinkedList<Border>();
        for (int index = 0; index < map.length(); index++) {
            if (map.charAt(index) == Elements.BATTLE_WALL.ch) {
                result.add(new Border(xy.getXY(index)));
            }
        }
        return result;
    }

    @Override
    public List<HedgeHog> getHedgeHogs() {
        List<HedgeHog> result = new LinkedList<>();
        for (int index = 0; index < map.length(); index++) {
            if (map.charAt(index) == Elements.HEDGEHOG.ch) {
                result.add(new HedgeHog(xy.getXY(index)));
            }
        }
        return result;
    }


    public List<HedgeHog> getHedgeHogsAuto(List<Point> addedElems) {
        final int numberOfHedgehogs = 40;
        final int coverage = 650;
        int index;
        int counter = 0;
        final int ticksToUpdate = 44;
        boolean pointExists;

        if(tick == ticksToUpdate){
            result = new LinkedList<>();
            while (counter < numberOfHedgehogs) {
                index = 2 + random.nextInt(coverage);
                pointExists = checkExistingPoint(xy.getXY(index), addedElems);
                if (!pointExists/*map.charAt(index) == ' '*/) {
                    result.add(new HedgeHog(xy.getXY(index)));
                    counter++;
                }
            }
            tick=0;
        }else{
            tick++;
        }

        return result;
    }


    public boolean checkExistingPoint(Point point, List<Point> addedElems){
        for(int i=0; i<addedElems.size(); i++){
            boolean exist = addedElems.get(i).itsMe(point);
            if(exist) return exist;
        }
        return false;
    }

    @Override
    public List<Bullet> getBullets() {
        return new LinkedList<Bullet>(); // do nothing
    }

    @Override
    public boolean isBarrier(int x, int y) {
        return false; // do nothing
    }

    @Override
    public boolean outOfField(int x, int y) {
        return false;  // do nothing
    }

    @Override
    public void affect(Bullet bullet) {
        // do nothing
    }

    @Override
    public BoardReader reader() {
        return new BoardReader() {
            @Override
            public int size() {
                return Level.this.size();
            }

            // все элементы в итоге добавляются в список
            @Override
            public Iterable<? extends Point> elements() {
                List<Point> result = new LinkedList<Point>();
                result.addAll(Level.this.getBorders());
                result.addAll(Level.this.getBullets());
                result.addAll(Level.this.getConstructions());
                result.addAll(Level.this.getTanks());
                result.addAll(Level.this.getHedgeHogs()); // сюда надо добавить ежа
                return result;
            }
        };
    }




}
