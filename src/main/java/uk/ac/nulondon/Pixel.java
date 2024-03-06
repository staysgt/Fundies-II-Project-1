package uk.ac.nulondon;

/**
 * the pixel class represents a singular pixel in an image
 * it has 3 member variables - r,g, and b which represent rgb values of a pixel
 */
public class Pixel {
    // a pixel will contain 3 integer values - r,g,b
    private int r;
    private int b;
    private int g;

    /**
     * constructor for a pixel
     * @param red the red rgb value
     * @param green the green rgb value
     * @param blue the blue rgb value
     */
    public Pixel(int red, int green, int blue) {
        this.r = red;
        this.b = blue;
        this.g = green;
    }

    /**
     * gets the value of red
     * @return the value of red
     */
    public int getR() {
        return r;
    }
    /**
     * gets green value
     * @return the value of green
     */
    public int getG() {
        return g;
    }
    /**
     * gets blue value
     * @return the value of blue
     */
    public int getB() {
        return b;
    }

    /**
     * helper function to turn the pixel into a string
     * @return a pixel represented as a string
     */
    @Override
    public String toString() {
        String s = "";
        s += this.getR() + " " + this.getG() + " " + this.getB();

        return s;
    }

}
