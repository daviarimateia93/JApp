package japp.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public abstract class ImageHelper {

    protected ImageHelper() {

    }

    public static BufferedImage createResizedCopy(BufferedImage originalImage, int scaledWidth, int scaledHeight,
            boolean preserveAlpha) {
        final int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        final BufferedImage bufferedImage = new BufferedImage(scaledWidth, scaledHeight, imageType);
        final Graphics2D graphics2d = bufferedImage.createGraphics();

        if (preserveAlpha) {
            graphics2d.setComposite(AlphaComposite.Src);
        }

        graphics2d.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
        graphics2d.dispose();

        return bufferedImage;
    }

    public static BufferedImage createResizedCopyProportionally(BufferedImage originalImage, int maxSize,
            boolean preserveAlpha) {
        final double percentage;

        if (originalImage.getWidth() > originalImage.getHeight()) {
            percentage = ((double) maxSize / (double) originalImage.getWidth());
        } else {
            percentage = ((double) maxSize / (double) originalImage.getHeight());
        }

        final int width = (int) Math.round(originalImage.getWidth() * percentage);
        final int height = (int) Math.round(originalImage.getHeight() * percentage);

        return createResizedCopy(originalImage, width, height, preserveAlpha);
    }
}
