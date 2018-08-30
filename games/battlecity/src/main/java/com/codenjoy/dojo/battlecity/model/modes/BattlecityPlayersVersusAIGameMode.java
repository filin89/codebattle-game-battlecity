package com.codenjoy.dojo.battlecity.model.modes;

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

import com.codenjoy.dojo.battlecity.model.GameController;
import com.codenjoy.dojo.battlecity.model.Tank;
import com.codenjoy.dojo.battlecity.model.events.YouKilledTankEvent;
import com.codenjoy.dojo.battlecity.model.events.YourTankWasKilledEvent;
import com.codenjoy.dojo.battlecity.model.scores.ScoresCalculator;
import com.codenjoy.dojo.battlecity.services.Scores;

public class BattlecityPlayersVersusAIGameMode extends DefaultBattlecityGameMode {

    private ScoresCalculator scoresCalculator;

    BattlecityPlayersVersusAIGameMode(GameController gameController) {
        super(gameController);
        scoresCalculator = new PlayersVersusAIScoresCalculator();
    }

    @Override
    public void startGame() {
        controller.createAITanks();
    }

    @Override
    public void beforeTick() {
        controller.newAI();
    }

    @Override
    public void onScoresEvent(Object event, Scores scores) {
        scoresCalculator.changeScore(event, scores);
    }

    private static class PlayersVersusAIScoresCalculator implements ScoresCalculator {

        @Override
        public void changeScore(Object event, Scores scores) {
            if (event instanceof YouKilledTankEvent) {
                YouKilledTankEvent e = (YouKilledTankEvent) event;

                if (Tank.Type.Player.equals(e.getKilledTankType())) {
                    scores.decrementScore(scores.getKillAliasTankScore());
                } else {
                    scores.incrementScore(scores.getKillOtherTankScore());
                }
            } else if (event instanceof YourTankWasKilledEvent) {
                scores.decrementScore(scores.getKillYourTankPenalty());
            }
        }
    }
}
