package com.codenjoy.dojo.battlecity.model;

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


import com.codenjoy.dojo.battlecity.model.events.YouKilledTankEvent;
import com.codenjoy.dojo.battlecity.model.events.YourTankWasKilledEvent;
import com.codenjoy.dojo.battlecity.model.levels.Level;
import com.codenjoy.dojo.battlecity.model.levels.LevelInfo;
import com.codenjoy.dojo.battlecity.model.levels.LevelRegistry;
import com.codenjoy.dojo.battlecity.model.modes.BattlecityGameMode;
import com.codenjoy.dojo.battlecity.model.modes.GameModeRegistry;
import com.codenjoy.dojo.battlecity.services.Scores;
import com.codenjoy.dojo.services.Dice;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Joystick;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.services.Tickable;
import com.codenjoy.dojo.services.printer.BoardReader;

import java.util.LinkedList;
import java.util.List;

public class Battlecity implements Tickable, ITanks, Field {

    private Dice dice;
    private LinkedList<Tank> aiTanks;
    private int aiCount;
    private int size;
    private List<Construction> constructions;
    private List<Border> borders;
    private List<HedgeHog> hedgeHogs;
    private List<WormHole> wormHoles;
    private List<Player> players = new LinkedList<>();
    private TankFactory aiTankFactory;
    private GameSettings settings;
    private BattlecityGameMode gameMode;
    private GameController gameController;
    private GameModeRegistry modeRegistry;
    private LevelRegistry levelRegistry;
    private Level level;
    private HedgeHogController hedgeHogController;

    public Battlecity(TankFactory aiTankFactory,
                      GameSettings settings,
                      LevelRegistry levelRegistry) {

        this.levelRegistry = levelRegistry;
        this.aiTankFactory = aiTankFactory;
        this.settings = settings;
        this.gameController = new BattleCityGameController();

        setDice(new RandomDice()); // TODO вынести это чудо за пределы конструктора
    }

    public void startOrRestartGame() {
        loadLevel(settings.getMap().getValue());
        loadGameMode( settings.getGameMode().getValue());
    }

    private void loadLevel(String mapName) {
        LevelInfo levelInfo = levelRegistry.getLevelByName(mapName);
        level = new Level(levelInfo.getMap(), aiTankFactory);

        aiCount = level.getTanks().size();
        this.size = level.size();
        this.aiTanks = new LinkedList<>();
        this.constructions = new LinkedList<>(level.getConstructions());
        this.borders = new LinkedList<>(level.getBorders());
        this.wormHoles = new LinkedList<>(level.getWormHoles());
        this.hedgeHogs = new LinkedList<>(level.getHedgeHogs());
        hedgeHogController = new HedgeHogController(this, settings, hedgeHogs);
    }

    @Override
    public void tick() {
        checkRequireReloadLevel();

        removeDeadTanks();

        gameMode.beforeTick();

        processGameElements();

        gameMode.afterTick();
    }

    private void reloadLevelAndMode() {
        clearGamesObjects();

        startOrRestartGame();
    }

    private void loadGameMode(String gameModeName) {
        gameMode = modeRegistry.getGameModeByName(gameModeName);
    }

    private void checkRequireReloadLevel() {
        if (settings.getGameMode().changed()) {
            settings.getGameMode().changesReacted();
            reloadLevelAndMode();
        } else if (settings.getMap().changed()) {
            settings.getMap().changesReacted();
            reloadLevelAndMode();
        }
    }

    private void clearGamesObjects() {
        getBullets().forEach(Bullet::onDestroy);
        getBullets().clear();

        getTanks().forEach(t -> t.kill(null));
        getTanks().clear();

        players.clear();
    }

    private void processGameElements() {
        for (Tank tank : getTanks()) {
            tank.tick();
        }

        for (Bullet bullet : getBullets()) {
            if (bullet.destroyed()) {
                bullet.onDestroy();
            }
        }

        for (Tank tank : getTanks()) {
            if (tank.isAlive()) {
                tank.move();

                List<Bullet> bullets = getBullets();
                int index = bullets.indexOf(tank);
                if (index != -1) {
                    Bullet bullet = bullets.get(index);
                    affect(bullet);
                }
            }
        }
        for (Bullet bullet : getBullets()) {
            bullet.move();
        }

        for (Construction construction : constructions) {
            if (!getTanks().contains(construction) && !getBullets().contains(construction)) {
                construction.tick();
            }
        }

        hedgeHogController.refreshHedgeHogs();
    }

    private void removeDeadTanks() {
        for (Tank tank : getTanks()) {
            if (!tank.isAlive()) {
                aiTanks.remove(tank);
            }
        }
        for (Player player : players.toArray(new Player[0])) {
            if (!player.getTank().isAlive()) {
                players.remove(player);
            }
        }
    }

    @Override
    public Joystick getJoystick() {
       return players.get(0).getTank();
    }

    void addAI(Tank tank) {
        if (tank != null) {
            tank.setField(this);
        }
        tank.setField(this);
        aiTanks.add(tank);
    }

    @Override
    public void affect(Bullet bullet) {
        if (borders.contains(bullet)) {
            bullet.onDestroy();
            return;
        }

        if (getTanks().contains(bullet)) {
            int index = getTanks().indexOf(bullet);
            Tank tank = getTanks().get(index);
            if (tank == bullet.getOwner()) {
                return;
            }

            triggerEventForTankKill(bullet, tank);

            tank.kill(bullet);
            bullet.onDestroy();  // TODO заимплементить взрыв
            return;
        }

        for (Bullet bullet2 : getBullets().toArray(new Bullet[0])) {
            if (bullet != bullet2 && bullet.equals(bullet2)) {
                bullet.boom();
                bullet2.boom();
                return;
            }
        }

        if (constructions.contains(bullet)) {
            Construction construction = getConstructionAt(bullet);

            if (!construction.destroyed()) {
                construction.destroyFrom(bullet.getDirection());
                bullet.onDestroy();  // TODO заимплементить взрыв
            }

            return;
        }
    }

    private Construction getConstructionAt(Bullet bullet) {
        int index = constructions.indexOf(bullet);
        return constructions.get(index);
    }

    private void triggerEventForTankKill(Bullet killedBullet, Tank diedTank) {
        Player died = null;
        Tank.Type killedTankType = Tank.Type.AI;

        if (!aiTanks.contains(diedTank)) {
            died = getPlayer(diedTank);
            killedTankType = Tank.Type.Player;
        }

        Tank killerTank = killedBullet.getOwner();
        Player killer = null;
        if (!aiTanks.contains(killerTank)) {
            killer = getPlayer(killerTank);
        }

        if (killer != null) {
            killer.event(new YouKilledTankEvent(killedTankType));
        }
        if (died != null) {
            died.event(new YourTankWasKilledEvent());
        }
    }

    private Player getPlayer(Tank tank) {
        for (Player player : players) {
            if (player.getTank().equals(tank)) {

                return player;
            }
        }

        throw new RuntimeException("Танк игрока не найден!");
    }

    @Override
    public boolean isBarrier(int x, int y) {
        for (Construction construction : constructions) {
            if (construction.itsMe(x, y) && !construction.destroyed()) {
                return true;
            }
        }
        for (Point border : borders) {
            if (border.itsMe(x, y)) {
                return true;
            }
        }
        for (Tank tank : getTanks()) {   //  TODO проверить как один танк не может проходить мимо другого танка игрока (не AI)
            if (tank.itsMe(x, y)) {
                return true;
            }
        }
        for (Point hedgehog : hedgeHogs) {
            if (hedgehog.itsMe(x, y)) {
                return true;
            }
        }
        return outOfField(x, y);
    }

    @Override
    public boolean isFieldOccupied(int x, int y) {
        boolean isBarrier = isBarrier(x, y);

        if (!isBarrier) {
            for (Point bullet : getBullets()) {
                if (bullet.itsMe(x, y)) {
                    return true;
                }
            }

            for (Point hole : getWormHoles()) {
                if (hole.itsMe(x, y)) {
                    return true;
                }
            }

            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean isAmmoBonus(int x, int y) {
        return false; //реализовать проверку на бонусные патроны
    }

    @Override
    public boolean isWormHole(int x, int y) {
        return wormHoles.stream()
                .anyMatch(wormHole -> wormHole.itsMe(x, y));
    }

    @Override
    public WormHole getWormHole(int x, int y) {
        return wormHoles.stream()
                .filter(wormHole -> wormHole.itsMe(x, y))
                .findAny()
                .orElse(null);
    }

    @Override
    public boolean outOfField(int x, int y) { // TODO заменить все есть в point
        return x < 0 || y < 0 || y > size - 1 || x > size - 1;
    }

    @Override
    public List<Bullet> getBullets() {
        List<Bullet> result = new LinkedList<>();
        for (Tank tank : getTanks()) {
            for (Bullet bullet : tank.getBullets()) {
                result.add(bullet);
            }
        }
        return result;
    }

    @Override
    public List<Tank> getTanks() {
        LinkedList<Tank> result = new LinkedList<>(aiTanks);
        for (Player player : players) {
//            if (player.getTank().isAlive()) { // TODO разремарить с тестом
                result.add(player.getTank());
//            }
        }
        return result;
    }

    @Override
    public void remove(Player player) {   // TODO test me
        players.remove(player);
    }

    @Override
    public void newGame(Player player) {  // TODO test me
        if (!players.contains(player)) {
            players.add(player);
        }
        player.newHero(this);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public BoardReader reader() {
        return new BoardReader() {
            private int size = Battlecity.this.size;

            @Override
            public int size() {
                return size;
            }

            @Override
            public Iterable<? extends Point> elements() {
                List<Point> result = new LinkedList<>();
                result.addAll(Battlecity.this.getBorders());
                result.addAll(Battlecity.this.getTanks());
                result.addAll(Battlecity.this.getConstructions());
                result.addAll(Battlecity.this.getWormHoles());
                result.addAll(Battlecity.this.getHedgeHogs());
                result.addAll(Battlecity.this.getBullets());
                return result;
            }
        };
    }

    @Override
    public List<Construction> getConstructions() {
        List<Construction> result = new LinkedList<>();
        for (Construction construction : constructions) {
            if (!construction.destroyed()) {
                result.add(construction);
            }
        }
        return result;
    }

    @Override
    public List<Border> getBorders() {
        return borders;
    }

    @Override
    public List<WormHole> getWormHoles() {
        return wormHoles;
    }

    @Override
    public List<HedgeHog> getHedgeHogs() {
        return hedgeHogs;
    }

    public void setDice(Dice dice) {
        this.dice = dice;
    }

    public void onScoresEvent(Object event, Scores scores) {
        gameMode.onScoresEvent(event, scores);
    }

    public void setModeRegistry(GameModeRegistry modeRegistry) {
        this.modeRegistry = modeRegistry;
    }

    class BattleCityGameController implements GameController {
        @Override
        public void createAITanks() {
            for (Tank tank : aiTanks) {
                addAI(tank);
            }
        }

        @Override
        public void newAI() {
            for (int count = aiTanks.size(); count < aiCount; count++) {
                int y = size - 2;
                int x = 0;
                int c = 0;
                do {
                    x = dice.next(size);
                    c++;
                } while (isBarrier(x, y) & c < size);

                if (!isBarrier(x, y)) {
                    addAI(aiTankFactory.createTank(
                            TankParams.newAITankParams(x, y, Direction.DOWN)));
                }
            }
        }
    }

    public GameController getGameController() {
        return gameController;
    }


}
