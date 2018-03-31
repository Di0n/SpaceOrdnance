package framework;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GameKeyListener extends KeyAdapter
{
    private Set<Integer> pressedKeys = new HashSet<>(); // Voor de toetsen die ingedrukt zijn.
    private Set<Integer> clickedKeys = new HashSet<>(); // Voor de toetsen die omlaag en omhoog zijn gegaan, geklikt.
    @Override
    public void keyPressed(KeyEvent e)
    {
        pressedKeys.add(e.getKeyCode());
    }

    @Override public void keyReleased(KeyEvent e)
    {
        pressedKeys.remove(e.getKeyCode());
        clickedKeys.add(e.getKeyCode());
    }

    public boolean isKeyDown(int key) { return pressedKeys.contains(key); }

    public boolean isKeyPressed(int key) { return clickedKeys.contains(key); }

    public void update()
    {
        clickedKeys.clear();
    }
}
