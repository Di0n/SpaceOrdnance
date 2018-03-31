package framework;

import org.dyn4j.dynamics.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

public abstract class Game extends JFrame
{
    private long lastTime;
    private boolean closing;

    protected final double worldScale;

    protected final World world;

    protected final Canvas canvas;

    protected boolean antiAliasing;


    public Game(String name, double worldScale)
    {
        super(name);
        this.worldScale = worldScale;

        world = new World();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
        setVisible(true);

        loadContent();

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
                  //Thread.yield();

                  try {Thread.sleep(5);} catch (InterruptedException ix) {}
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

        if (antiAliasing)
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        AffineTransform ot = g2d.getTransform();
        draw(g2d);

        g2d.setTransform(ot);
        g2d.dispose();

        BufferStrategy strategy = canvas.getBufferStrategy();
        if (!strategy.contentsLost()) {
            strategy.show();
        }

        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * Game loop voor game logica
     * @param deltaTime
     */
    protected abstract void update(double deltaTime);

    /**
     * Game loop voor grafische handelingen
     * @param g2d
     */
    protected abstract void draw(Graphics2D g2d);

    /**
     * Laad hier de benodigde game assets
     */
    protected abstract void loadContent();


    public synchronized void stop()
    {
        closing = true;
    }

    /**
     * Geeft aan of de game wordt gevraagd om te stoppen
     * @return
     */
    public final boolean isClosing()
    {
        return closing;
    }

    public void addKeyboardListener(KeyAdapter keyAdapter)
    {
        this.canvas.addKeyListener(keyAdapter);
    }

    public void addMouseListener(MouseAdapter mouseAdapter)
    {
        this.canvas.addMouseListener(mouseAdapter);
    }
}
