package framework;

import org.dyn4j.dynamics.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;

public abstract class Game extends JFrame
{
    private long lastTime;
    private boolean closing;

    protected final double worldScale;

    protected final World world;

    protected final Canvas canvas;

    protected final JCheckBox debugCheckBox;


    public Game(String name, double worldScale)
    {
        super(name);
        this.worldScale = worldScale;

        world = new World();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        debugCheckBox = null;

        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                stop();
                super.windowClosing(e);
            }
        });

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        canvas = new Canvas();
        //Toolkit tk = Toolkit.getDefaultToolkit();
        //Dimension dimension = new Dimension(tk.getScreenSize().width, tk.getScreenSize().height);
        //canvas.setPreferredSize(dimension);
        //canvas.setMinimumSize(dimension);
        //canvas.setMaximumSize(dimension);

        add(canvas);

        setResizable(false);

        pack();


    }

    public void run()
    {
        loadContent();

        setVisible(true);
        lastTime = System.nanoTime();

        canvas.setIgnoreRepaint(true);
        canvas.createBufferStrategy(2);

        Thread thread = new Thread()
        {
          public void run()
          {
              while (!isClosing())
              {
                  gameLoop();
                  Thread.yield();
              }
          }
        };


        thread.setDaemon(true);
        thread.start();
    }

    private void gameLoop()
    {
        long time = System.nanoTime();
        double deltaTime = (time - lastTime) / 1e9; //1000000000.0;
        lastTime = time;

        update(deltaTime);
        Graphics2D g2d = (Graphics2D)canvas.getBufferStrategy().getDrawGraphics();
        draw(g2d);
        g2d.dispose();

        BufferStrategy strategy = canvas.getBufferStrategy();
        if (!strategy.contentsLost()) {
            strategy.show();
        }
    }

    protected abstract void update(double deltaTime);

    protected abstract void draw(Graphics2D g2d);

    protected abstract void loadContent();

    public synchronized void stop()
    {
        closing = true;
    }

    public boolean isClosing()
    {
        return closing;
    }
}
