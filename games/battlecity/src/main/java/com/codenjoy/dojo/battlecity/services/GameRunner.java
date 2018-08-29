package com.codenjoy.dojo.battlecity.services;

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


import com.codenjoy.dojo.battlecity.client.ai.ApofigSolver;
import com.codenjoy.dojo.battlecity.model.AITankFactory;
import com.codenjoy.dojo.battlecity.model.Battlecity;
import com.codenjoy.dojo.battlecity.model.Elements;
import com.codenjoy.dojo.battlecity.model.GameSettings;
import com.codenjoy.dojo.battlecity.model.GameSettingsImpl;
import com.codenjoy.dojo.battlecity.model.PlayerTankFactory;
import com.codenjoy.dojo.battlecity.model.Single;
import com.codenjoy.dojo.battlecity.model.levels.ResourcesLevelRegistryImpl;
import com.codenjoy.dojo.battlecity.model.TankFactory;
import com.codenjoy.dojo.battlecity.model.modes.GameModeRegistry;
import com.codenjoy.dojo.battlecity.model.modes.StaticGameModeRegistryImpl;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.AbstractGameType;
import com.codenjoy.dojo.services.EventListener;
import com.codenjoy.dojo.services.Game;
import com.codenjoy.dojo.services.GameType;
import com.codenjoy.dojo.services.PlayerScores;
import com.codenjoy.dojo.services.RandomDice;
import com.codenjoy.dojo.services.hero.GameMode;
import com.codenjoy.dojo.services.printer.PrinterFactory;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;

import static com.codenjoy.dojo.services.settings.SimpleParameter.v;

public class GameRunner extends AbstractGameType implements GameType {

    public final static boolean SINGLE = GameMode.SINGLE_MODE;

    private static final String MAPS_PREFIX = "battlecity/maps/";
    private static final String MAP_FILES_EXTENSION = ".map";
    private static final String BATTLECITY_GAME_NAME = "battlecity";

    private Battlecity battleCityGame;
    private TankFactory aiTankFactory;
    private TankFactory playerTankFactory;
    private GameSettings gameSettings;

    public GameRunner() {
        gameSettings = getGameSettings(settings);

        RandomDice dice = new RandomDice();
        aiTankFactory = new AITankFactory(dice, gameSettings);
        playerTankFactory = new PlayerTankFactory(dice, gameSettings);
    }

    private GameSettings getGameSettings(Settings settings) {
        return new GameSettingsImpl(settings);
    }

    private Battlecity newBattleCityGame() {
        Battlecity battlecity = new Battlecity(
                aiTankFactory,
                gameSettings,
                new ResourcesLevelRegistryImpl(MAPS_PREFIX, MAP_FILES_EXTENSION));

        GameModeRegistry modeRegistry = new StaticGameModeRegistryImpl(battlecity.getGameController());
        battlecity.setModeRegistry(modeRegistry);

        battlecity.startOrRestartGame();

        return battlecity;
    }

    @Override
    public PlayerScores getPlayerScores(Object score) {
        return new Scores((Integer) score, settings) {
            @Override
            public void event(Object event) {
                battleCityGame.onScoresEvent(event, this);
            }
        };
    }

    @Override
    public Game newGame(EventListener listener, PrinterFactory factory, String save, String playerName) {
        if (!SINGLE || battleCityGame == null) {
            battleCityGame = newBattleCityGame();
        }
        Game game = new Single(battleCityGame, listener, factory, playerTankFactory);
        game.newGame();

        return game;
    }

    @Override
    public Parameter<Integer> getBoardSize() {
        return v(battleCityGame.size());
    }

    @Override
    public String name() {
        return BATTLECITY_GAME_NAME;
    }

    @Override
    public Enum[] getPlots() {
        return Elements.values();
    }

    @Override
    public boolean isSingleBoard() {
        return SINGLE;
    }

    @Override
    public boolean newAI(String aiName) {
        ApofigSolver.start(aiName, WebSocketRunner.Host.REMOTE_LOCAL);
        return true;
    }

    @Override
    public Object getGame() {
        return null;
    }
}
