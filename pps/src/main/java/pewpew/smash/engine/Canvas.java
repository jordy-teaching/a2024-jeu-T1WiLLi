package pewpew.smash.engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import lombok.Getter;

@Getter
public class Canvas {
    private Graphics2D graphics2D;
    private AffineTransform originalTransform;
    private Shape originalClip;

    public Canvas(Graphics2D graphics2d) {
        this.graphics2D = graphics2d;
        this.originalTransform = graphics2D.getTransform();
        this.originalClip = graphics2D.getClip();
    }

    // Transformation Methods
    public void translate(float x, float y) {
        graphics2D.translate(x, y);
    }

    public void scale(float x, float y) {
        originalTransform = graphics2D.getTransform();
        graphics2D.scale(x, y);
    }

    public void rotate(double angle, int centerX, int centerY) {
        graphics2D.rotate(Math.toRadians(angle), centerX, centerY);
    }

    public void resetScale() {
        graphics2D.setTransform(originalTransform);
    }

    public void setColor(Color color) {
        graphics2D.setColor(color);
    }

    // Image Rendering Methods
    public void renderImage(BufferedImage img, int x, int y, int width, int height) {
        if (img != null) {
            graphics2D.drawImage(img, x, y, width, height, null);
        }
    }

    public void renderCircularImage(BufferedImage image, int x, int y, int diameter) {
        Shape circle = new Ellipse2D.Float(x, y, diameter, diameter);
        graphics2D.setClip(circle);
        graphics2D.drawImage(image, x, y, diameter, diameter, null);
        resetClipAndRenderBorder(circle);
    }

    // Shape Rendering Methods
    public void renderRectangle(int x, int y, int width, int height, Color color) {
        setGraphicsProperties(color, 1);
        graphics2D.fillRect(x, y, width, height);
    }

    public void renderRectangleBorder(int x, int y, int width, int height, int strokeWidth, Color color) {
        setGraphicsProperties(color, strokeWidth);
        graphics2D.drawRect(x, y, width, height);
    }

    public void renderCircle(int centerX, int centerY, int radius, Color color) {
        setGraphicsProperties(color, 1);
        graphics2D.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    public void renderPolygon(Polygon polygon, Color color) {
        setColor(color);
        graphics2D.fillPolygon(polygon);
    }

    public void renderLine(int x1, int y1, int x2, int y2, int strokeWidth, Color color) {
        setGraphicsProperties(color, strokeWidth);
        graphics2D.drawLine(x1, y1, x2, y2);
    }

    public void renderGlow(int x, int y, int width, int height, Color glowColor) {
        graphics2D.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 50));
        for (int i = 1; i <= 3; i++) {
            graphics2D.setStroke(new BasicStroke(i));
            graphics2D.drawRect(x - i, y - i, width + 2 * i, height + 2 * i);
        }
    }

    // Text Rendering Methods
    public void renderString(String text, int x, int y, Color color) {
        setGraphicsProperties(color, 1);
        graphics2D.drawString(text, x, y);
    }

    public void renderString(String text, int x, int y) {
        graphics2D.drawString(text, x, y);
    }

    // Shadow Rendering Methods
    public void renderShadow(int x, int y, int width, int height, int shadowOffset, Color shadowColor) {
        setGraphicsProperties(shadowColor, 1);
        graphics2D.fillRect(x + shadowOffset, y + shadowOffset, width, height);
    }

    public void renderShadowCircle(int x, int y, int diameter, int shadowOffset) {
        graphics2D.setColor(new Color(0, 0, 0, 100));
        graphics2D.fillOval(x + shadowOffset, y + shadowOffset, diameter, diameter);
    }

    // Font Handling
    public void setFont(Font font) {
        graphics2D.setFont(font);
    }

    public void resetFont() {
        graphics2D.setFont(GameFont.DEFAULT_FONT.getFont());
    }

    // Private Utility Methods
    private void setGraphicsProperties(Color color, int strokeWidth) {
        graphics2D.setColor(color);
        graphics2D.setStroke(new BasicStroke(strokeWidth));
    }

    private void resetClipAndRenderBorder(Shape shape) {
        graphics2D.setClip(originalClip);
        graphics2D.setColor(Color.WHITE);
        graphics2D.draw(shape);
    }

    // GameFont Enum
    public enum GameFont {
        DEFAULT_FONT(new Font("Segoe UI", Font.BOLD, 12)),
        SMALL_FONT(new Font("Verdana", Font.PLAIN, 8)),
        LARGE_FONT(new Font("Georgia", Font.BOLD, 24));

        private final Font font;

        GameFont(Font font) {
            this.font = font;
        }

        public Font getFont() {
            return font;
        }
    }
}