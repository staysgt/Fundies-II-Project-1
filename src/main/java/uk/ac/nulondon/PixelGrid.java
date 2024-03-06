package uk.ac.nulondon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.awt.Color;

/**
 * this class represents an image as a grid of pixels
 */
public class PixelGrid {
     ArrayList<ArrayList<Pixel>> photoGrid = new ArrayList<>();
    /**
     * constructor that instantiates photoGrid
     */
    public PixelGrid() {
        photoGrid = new ArrayList<>();
    }

    /**
     * constructor that takes in an array list of array lists of pixels and converts it to type PixelGrid
     * @param givenList an array list of array list of pixels to be converted to PixelGrid
     */
    public PixelGrid(ArrayList<ArrayList<Pixel>> givenList) {
        int numOfLists = givenList.size();
        if (numOfLists > 0) {
            for (int i = 0; i < numOfLists; i++) {
                ArrayList<Pixel> row = new ArrayList<>();
                for (int j = 0; j < givenList.getFirst().size(); j++) {
                    row.add(givenList.get(i).get(j));
                }
                photoGrid.add(row);
            }
        }
    }

    /**
     * converts an image into a type PixelGrid
     * @param givenImage image that is to be converted into a PixelGrid
     */
    public void imageToArray(BufferedImage givenImage) {
        int imageWidth = givenImage.getWidth();
        int imageHeight = givenImage.getHeight();

        // iterates through every row to add it to the photoGrid
        for (int i = 0; i < imageWidth; i++) {
            // inside the for loop so that it makes a new row for each iteration
            ArrayList<Pixel> row = new ArrayList<>();
            for (int j = 0; j < imageHeight; j++) {
                // get the r,g,b coordinate somewhere
                // use the i and j to index into the image

                // gets the color from the given image
                Color color = new Color(givenImage.getRGB(i, j));
                // creates a pixel out of these colors
                Pixel newPixel = new Pixel(color.getRed(), color.getGreen(), color.getBlue());

                // adds the new color to the row
                row.add(newPixel);
            }
            // adds the row to the photoGrid - the array list of array lists of integers
            photoGrid.add(row);
        }
    }

    /**
     * changes the value of a single pixel in a PixelGrid
     * @param x the x value of the pixel to be altered
     * @param y the y value of the pixel to be altered
     * @param p the color of to change the pixel to
     */
    public void setPixel(int x, int y, Pixel p) {
        photoGrid.get(x).set(y,p);
    }

    public void setPixels(int x, int y, int width, int height, Pixel p) {
        for(int i = x; i < height; i++) {
            for(int j = y; j < width; j++) {
                photoGrid.get(j).set(i,p);
            }
        }


    }

    /**
     * gets the color at a given coordinate point
     * @param x x coordinate of pixel
     * @param y y coordinate of pixel
     * @return rgb value at a given point
     */
    public Color getColorAt(int x, int y) {
        Pixel pixel = photoGrid.get(x).get(y);
        int r = pixel.getR();
        int g = pixel.getG();
        int b = pixel.getB();
        return new Color(r, g, b);
    }

    /**
     * helper function that turns a type PixelGrid into a string
     * @return a string version of a PixelGrid
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        int numOfRows = photoGrid.size();
        int numOfCol = photoGrid.getFirst().size();
        for (int i = 0; i < numOfCol; i++) {
            for (int j = 0; j < numOfRows; j++) {
                // if statement to get rid of trailing space
                if ((j != numOfCol - 1)) {
                    s.append(photoGrid.get(j).get(i)).append(" ");
                } else {
                    s.append(photoGrid.get(j).get(i));
                }
            }
            s.append(System.lineSeparator());
        }
        return s.toString();
    }

    /**
     * finds the bluest column
     * @return the index of the bluest column
     */
    public int bluestColumnFinder() {
        int numOfRows = photoGrid.getFirst().size();
        int numOfCol = photoGrid.size();
        int[] columnBlueness = new int[numOfCol];
        int bluestIndex = 0;

        for (int i = 0; i < numOfRows; i++) {
            for (int j = 0; j < numOfCol; j++) {
                columnBlueness[j] += photoGrid.get(j).get(i).getB();
            }
        }

        int maxBlue = columnBlueness[0];
        for (int i = 0; i < numOfCol; i++) {
            if (columnBlueness[i] > maxBlue) {
                maxBlue = columnBlueness[i];
                bluestIndex = i;
            }
        }
        return bluestIndex;
    }

    /**
     * highlights the bluest column
     * @param bluestColIndex the index of the bluest column, found by bluestColumnFinder
     */
    public void highlightBluest(int bluestColIndex) {
        int numOfRows = photoGrid.getFirst().size();
        Pixel bluePixel = new Pixel(0, 0, 255);
        for (int i = 0; i < numOfRows; i++) {
            photoGrid.get(bluestColIndex).set(i, bluePixel);
        }
//        save(String.format("target/tmp%d", ++step));
    }

    /**
     * removes the bluest column
     * @param bluestColIndex the index of the bluest column, found by bluestColumnFinder
     */
    public void removeBluest(int bluestColIndex) {
        photoGrid.remove(bluestColIndex);
    }

    /**
     * gets a random number to be removed from the image
     * @return a random index within the bounds of the image
     */
    public int getRandom() {
        Random random = new Random();
        int restriction = photoGrid.size();
//                - (commandsExecuted / 2);
        if (restriction != 0) {
            return random.nextInt(0, restriction);
        }
        else {
            return 0;
        }
    }

    /**
     * highlights a random column in the image
     * @param randomNum a random index within in the bounds of the image
     */
    public void highlightRandom(int randomNum) {
        int numOfRows = photoGrid.getFirst().size();
        Pixel redPixel = new Pixel(255, 0, 0);
        for (int i = 0; i < numOfRows; i++) {
            photoGrid.get(randomNum).set(i, redPixel);
        }
    }

    /**
     * removes a random column in the image
     * @param randomNum a random index within in the bounds of the image, the same as highlight random
     */
    public void randomRemove(int randomNum) {
        photoGrid.remove(randomNum);
    }

    /**
     * exports the image into a new file path
     * @param filename the filepath that the image will be put in
     */
    public void saveImage(String filename) {
        int height = photoGrid.size();
        int width = photoGrid.getFirst().size();
        BufferedImage image = new BufferedImage(height, width, BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                Pixel currentPixel = photoGrid.get(i).get(j);
                int r = currentPixel.getR();
                int g = currentPixel.getG();
                int b = currentPixel.getB();
                Color color = new Color(r,g,b);
                image.setRGB(i,j, color.getRGB());
            }
        }
        try {
            ImageIO.write(image, "png", new File(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * gets the number of columns (size) of a PixelGrid
     * @return number of columns
     */
    public int getSize() {
        return photoGrid.get(0).size();
    }

    /**
     * adds rows into a PixelGrid
     * @param row the row that is going to be added into a PixelGrid
     */
    private void add(ArrayList<Pixel> row) {
        photoGrid.add(row);
    }

    /**
     * This method copies a PixelGrid onto another PixelGrid
     * @return a copied PixelGrid
     */
    public PixelGrid copy() {
        int numOfRows = photoGrid.size();
        int numOfCol = photoGrid.getFirst().size();
        PixelGrid pG = new PixelGrid();
        for(int i = 0; i < numOfRows; i++) {
            ArrayList<Pixel> row = new ArrayList<>();
            for(int j = 0; j < numOfCol; j++) {
                Pixel p = photoGrid.get(i).get(j);
                row.add(p);
            }
            pG.add(row);
        }
        return pG;
    }


}
