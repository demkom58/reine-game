package com.crown;

import com.crown.output.window.Window;

public interface Game {
    Window getWindow();

    void input();

    void update();

    void render(float delta);
}
