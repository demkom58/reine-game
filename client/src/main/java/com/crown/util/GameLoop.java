package com.crown.util;

import com.crown.Game;

public interface GameLoop {
    void init();

    void loop(Game game);

    int getFps();

    int getUps();
}
