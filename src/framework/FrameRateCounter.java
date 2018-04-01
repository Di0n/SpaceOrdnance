package framework;

import java.util.LinkedList;
import java.util.Queue;

public class FrameRateCounter
{
    private long totalFrames;
    private double totalSeconds;
    private double averageFramesPerSecond;
    private double currentFramesPerSecond;

    private final int MAXIMUM_SAMPLES = 100;

    private Queue<Double> sampleBuffer = new LinkedList<>();

    public FrameRateCounter() {}

    public void update(double deltaTime)
    {
        currentFramesPerSecond = 1.0 / deltaTime;
        sampleBuffer.add(currentFramesPerSecond);

        if (sampleBuffer.size() > MAXIMUM_SAMPLES)
        {
            sampleBuffer.remove();
            double avg = 0;
            int counter = 0;

            for (double d : sampleBuffer)
            {
                avg += d;
                counter++;
            }
            averageFramesPerSecond = avg / counter;
        }
        else
            averageFramesPerSecond = currentFramesPerSecond;

        totalFrames++;
        totalSeconds += deltaTime;
    }

    public double getAverageFramesPerSecond()
    {
        return averageFramesPerSecond;
    }

    public double getCurrentFramesPerSecond()
    {
        return currentFramesPerSecond;
    }
}
