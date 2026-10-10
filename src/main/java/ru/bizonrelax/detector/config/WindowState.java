package ru.bizonrelax.detector.config;

/**
 * Immutable value object describing saved window geometry and font scale.
 */
public final class WindowState {

    private final int width;
    private final int height;
    private final int x;
    private final int y;
    private final int fontSize;

    public WindowState(int width, int height, int x, int y, int fontSize) {
        this.width = Math.max(320, width);
        this.height = Math.max(240, height);
        this.x = x;
        this.y = y;
        this.fontSize = Math.min(48, Math.max(8, fontSize));
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /** @return saved X coordinate, or -1 if never positioned. */
    public int getX() {
        return x;
    }

    /** @return saved Y coordinate, or -1 if never positioned. */
    public int getY() {
        return y;
    }

    public int getFontSize() {
        return fontSize;
    }

    public boolean hasPosition() {
        return x >= 0 && y >= 0;
    }
}
