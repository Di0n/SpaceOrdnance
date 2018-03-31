public class GameAssets
{
    private static GameAssets instance = new GameAssets();

    private GameAssets()
    {

    }

    public static GameAssets getInstance()
    {
        return instance;
    }

}
