import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;

import java.awt.image.BufferedImage;
import java.util.Random;

public class AsteroidSpawner
{
    private final int DEFAULT_ASTEROID_COUNT = 10;
    private final int DEFAULT_LRG_ASTEROIDS_ON_SCREEN = 2;
    private final double ASTEROID_SIZE = 1.5;

    private final World world;
    private final BufferedImage[] asteroidImages;
    private final double worldScale;
    private int level;
    private int numberOfAsteroids;
    private int maxAsteroidsOnScreen;

    // 10, 2
    public AsteroidSpawner(World world, double worldScale, int level, BufferedImage[] asteroidImages)
    {
        this.world = world;
        this.worldScale = worldScale;
        this.level = level;
        this.asteroidImages = asteroidImages;
        numberOfAsteroids = level*2 + DEFAULT_ASTEROID_COUNT;
        maxAsteroidsOnScreen = level + DEFAULT_LRG_ASTEROIDS_ON_SCREEN;
    }

    private final Random random = new Random();
    public void update(double deltaTime, int screenWidth, int screenHeight)
    {
        int largeAsteroidCount = 0;
        for (Body b : world.getBodies())
            if (b instanceof Asteroid)
            {
                Asteroid asteroid = (Asteroid)b;
                if (asteroid.getSize() == Asteroid.Size.LARGE)
                    largeAsteroidCount++;
            }

        if (largeAsteroidCount >= maxAsteroidsOnScreen) return;

        Asteroid asteroid = new Asteroid(asteroidImages[0], ASTEROID_SIZE/worldScale, Asteroid.Size.LARGE);
        /*
            Boven = 1
            Rechts = 2
            Onder = 3
            Links = 4
         */
        final int spawnSide = random.nextInt(4)+1;

        double x = 0, y = 0;
        Vector2 direction = new Vector2(0,0);
        final int minX = (int)((screenWidth / worldScale) / 3);
        //final int maxX =
        final double force = 40*2;
        switch (spawnSide)
        {
            case 1:
                //x = random.nextInt((int)((screenWidth/worldScale) - (asteroid.getImage().getHeight() * asteroid.getScale()) / 2))+(asteroid.getImage().getHeight() * asteroid.getScale())/2;
                x = random.nextInt((int)(screenWidth / worldScale));
                y = 0 - ((asteroid.getImage().getHeight() * asteroid.getScale()) / 2);
                direction = new Vector2(0, force);
                break;
            case 2:
                x = (screenWidth / worldScale) + ((asteroid.getImage().getWidth() * asteroid.getScale()) / 2);
                y = random.nextInt((int)(screenHeight/worldScale));
                //y = random.nextInt((int)((screenHeight/worldScale) - (asteroid.getImage().getWidth() * asteroid.getScale()) / 2))+(asteroid.getImage().getWidth() * asteroid.getScale())/2;
                direction = new Vector2(-force, 0);
                break;
            case 3:
                //x = random.nextInt((int)((screenWidth/worldScale) - (asteroid.getImage().getWidth() * asteroid.getScale()) / 2))+(asteroid.getImage().getWidth() * asteroid.getScale())/2;
                x = random.nextInt((int)(screenWidth / worldScale));
                y = (screenHeight/worldScale) + ((asteroid.getImage().getHeight() * asteroid.getScale()) /2);
                direction = new Vector2(0, -force);
                break;
            case 4:
                x = 0 - ((asteroid.getImage().getWidth() * asteroid.getScale()) / 2);
                y = random.nextInt((int)(screenHeight/worldScale));
                direction = new Vector2(force, 0);
                break;
        }


        //asteroid.getTransform().setTranslation((0 - asteroid.getImage().getWidth()) / worldScale, (screenHeight / 2)/worldScale);
        asteroid.getTransform().setTranslation(x, y);
        //asteroid.applyForce(new Vector2(20, 0));
        asteroid.applyForce(direction);
        asteroid.applyImpulse(10/10);
        asteroid.setBorderCheck(true); // Nog niet borderchecken als het object wordt ingespawnd
        world.addBody(asteroid);
    }
}
