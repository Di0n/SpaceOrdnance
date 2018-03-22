package framework;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GameKeyListener extends KeyAdapter
{
    private Set<Integer> pressedKeys = new HashSet<>();
    @Override
    public void keyPressed(KeyEvent e)
    {
        pressedKeys.add(e.getKeyCode());
    }

    @Override public void keyReleased(KeyEvent e)
    {
        pressedKeys.remove(e.getKeyCode());
    }

    public Set<Integer> getPressedKeys()
    {
        return pressedKeys;
    }

    public void update()
    {

    }
}
