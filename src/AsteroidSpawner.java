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

        asteroid.getTransform().setTranslation((0 - asteroid.getImage().getWidth()+1) / worldScale, (screenHeight / 2)/worldScale);
        asteroid.applyForce(new Vector2(20, 0));
        asteroid.setBorderCheck(true); // Nog niet borderchecken als het object wordt ingespawnd
        world.addBody(asteroid);
    }
}
