import javafx.scene.transform.Affine;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Hud
{
    public class TextSettings
    {
        private int textSize;
        private String text;
        private Point2D location;
        private String fontName;
        private int fontStyle;

        public TextSettings(String text, int textSize, Point2D location, String fontName, int fontStyle)
        {
            this.text = text;
            this.textSize = textSize;
            this.location = location;
            this.fontName = fontName;
            this.fontStyle = fontStyle;
        }

        public int getTextSize()
        {
            return textSize;
        }

        public void setTextSize(int textSize)
        {
            this.textSize = textSize;
        }

        public String getText()
        {
            return text;
        }

        public void setText(String text)
        {
            this.text = text;
        }

        public Point2D getLocation()
        {
            return location;
        }

        public void setLocation(Point2D location)
        {
            this.location = location;
        }

        public String getFontName()
        {
            return fontName;
        }
        public void setFontName(String fontName)
        {
            this.fontName = fontName;
        }

        public int getFontStyle()
        {
            return fontStyle;
        }

        public void setFontStyle(int fontStyle)
        {
            this.fontStyle = fontStyle;
        }

    }

    private Map<TextSettings, Long> textList;
    public Hud()
    {
        textList = new HashMap<>();
    }

    public void setText(TextSettings ts, long duration)
    {
        duration += System.currentTimeMillis();
        textList.put(ts, duration);
    }

    public static void drawTextThisFrame(Graphics2D g2d, String text, int size, Point2D location, String fontName, int fontStyle)
    {
        AffineTransform ot = g2d.getTransform();
        Font font = new Font(fontName, fontStyle, size);

        AffineTransform textLocation = new AffineTransform();

        textLocation.translate(location.getX(), location.getY());

        g2d.setTransform(textLocation);
        g2d.setFont(font);
        g2d.drawString(text, 0, 0);
        g2d.setTransform(ot);
    }
    public void draw(Graphics2D g2d)
    {
        Iterator it = textList.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            long duration = (long)pair.getValue();

            if (System.currentTimeMillis() < duration)
            {
                TextSettings ts = (TextSettings) pair.getKey();
                drawTextThisFrame(g2d, ts.getText(), ts.getTextSize(), ts.getLocation(), ts.getFontName(), ts.getFontStyle());
            }
            else
                it.remove();
        }
    }
}
