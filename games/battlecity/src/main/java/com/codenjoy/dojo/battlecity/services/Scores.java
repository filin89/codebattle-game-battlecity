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


import com.codenjoy.dojo.services.PlayerScores;
import com.codenjoy.dojo.services.settings.Parameter;
import com.codenjoy.dojo.services.settings.Settings;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class Scores implements PlayerScores {

    private final Parameter<Integer> killYourTankPenalty;
    private final Parameter<Integer> killOtherTankScore;
    private final Parameter<Integer> killAliasTankScore;

    private AtomicInteger score;

    public Scores(int startScore, Settings settings) {
        this.score = new AtomicInteger(startScore);

        killYourTankPenalty = settings.addEditBox("Kill your tank penalty").type(Integer.class).def(50);
        killOtherTankScore = settings.addEditBox("Kill other tank score").type(Integer.class).def(100);
        killAliasTankScore = settings.addEditBox("Kill alias tank score").type(Integer.class).def(60);
    }

    @Override
    public int clear() {
        score.set(0);
        return score.get();
    }

    @Override
    public Integer getScore() {
        return score.get();
    }

    public void incrementScore(int addScore) {
        score.addAndGet(addScore);
    }

    public void decrementScore(int subtractScore) {
        if (score.addAndGet(-subtractScore) < 0) {
            score.set(0);
        }
    }

    public int getKillYourTankPenalty() {
        return killYourTankPenalty.getValue();
    }

    public int getKillOtherTankScore() {
        return killOtherTankScore.getValue();
    }

    public int getKillAliasTankScore() {
        return killAliasTankScore.getValue();
    }
}
