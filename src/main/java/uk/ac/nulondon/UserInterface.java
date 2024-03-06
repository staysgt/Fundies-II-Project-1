package uk.ac.nulondon;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

/**
 * contains the methods that implement the user interface
 */
public class UserInterface {
    private boolean shouldQuit = false;
    private boolean wantsToStop = false;

    private static final Scanner scanner = new Scanner(System.in);
    // tracks the number of columns deleted so the program knows when to stop
    private int columnsDeleted = 0;
    // tracks the number of commands executed and is used for naming versions of the image
    private int commandsExecuted = 0;
    private int undoNum = 0;

    // used to store versions to optimize the undo feature
    static Stack<PixelGrid> stack = new Stack<>();
    ArrayList<String> arrayOfInput = new ArrayList<>();
    BufferedImage oldImg;
    private String folder = "";
    PixelGrid pG = new PixelGrid();

    /**
     * prompts the user for the filepath of the image
     * @throws IOException in order to accommodate reading the image from imageIO
     */
    private void givenFilePath() throws IOException {
        System.out.println("enter the filepath or enter 'new' to create a new image or enter 'q' to quit: ");
        final String choice = scanner.next();
        try {
            if (choice.equalsIgnoreCase("new")) {
                arrayOfInput.add(choice);
                System.out.println("enter the filepath of where you want to store your image, ending with the name of the image: ");
                folder = scanner.next();
                System.out.println("enter the desired height of your image: ");
                int height = Integer.parseInt(scanner.next());
                System.out.println("enter the desired width of your image: ");
                int width = Integer.parseInt(scanner.next());
                BufferedImage blank = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                pG.imageToArray(blank);
                stack.push(pG.copy());
                wantsToSetPixel();
            } else {
                arrayOfInput.add(choice);
                File originalFile = new File(choice);
                oldImg = ImageIO.read(originalFile);
                pG.imageToArray(oldImg);
                stack.push(pG.copy());
                uiMenu();
                userInput();
            }
        } catch (NumberFormatException | IOException e) {
            System.out.println(e + "\ntry again");
            givenFilePath();
        }
    }


    /**
     * gets input from the user and executes commands based on the input
     * @throws IOException in order to accommodate reading the image from imageIO
     */
    private void userInput() throws IOException {
        String choice;
        ArrayList<String> valid = new ArrayList<>(Arrays.asList("b", "r", "u", "q", "a", "g"));
        choice = scanner.next().toLowerCase();
        if ((columnsDeleted / 2) == pG.getSize() - 1) {
            System.out.println("you have deleted all of the columns!");
            shouldQuit = true;
        }
        while (!shouldQuit && valid.contains(choice)) {
            switch (choice) {
                case "b" -> wantsBluestRemoved();
                case "r" -> wantsRandomRemoved();
                case "u" -> undo();
                case "q" -> {
                    shouldQuit = true;
                    System.out.println("thanks for playing!");
                }
                case "a" -> wantsToSetPixel();
                case "g" -> wantsValueOfPixel();
            }
        }
        if (!valid.contains(choice)) {
            System.out.println("invalid letter. try again.");
            uiMenu();
            userInput();
        }

    }


    /**
     * creates the name of the filepath based on the number of commands executed
     *
     * @return name of the filepath
     */
    private String getFilePath() {
        String s = arrayOfInput.getFirst();
        if (s.equalsIgnoreCase("new")) {
            return folder + commandsExecuted + ".png";
        } else {
            String[] sSplit = s.split(".png");
            s = sSplit[0] + commandsExecuted + ".png";
            return s;
        }
    }


    /**
     * the choices that the user can choose from, printted out
     */
    private static void uiMenu() {
        System.out.println("""
                Make a selection:\s
                 a - alter the image
                 b - remove bluest column\s
                 r - remove a randomly selected column\s
                 u - undo the most recent deletion\s
                 g - get color at a value
                 q - to quit""");
    }

    /**
     * is run when a user wants to set a pixel in the image
     * @throws IOException in order to accommodate reading the image from imageIO
     */
    private void wantsToSetPixel() throws IOException {
        System.out.println("You are in: image altering");
        while (!wantsToStop) {
            System.out.println("To set one pixel enter 'o', to set multiple enter 'm'. To quit type 'q'.");
            final String choice = scanner.next();
            if (choice.equalsIgnoreCase("q")) {
                wantsToStop = true;
                System.out.println("image has been altered");
                uiMenu();
                userInput();
            } else if (choice.equalsIgnoreCase("o") || choice.equalsIgnoreCase("m")) {
                int x = 0;
                int y = 0;
                int r;
                int g;
                int b;
                Pixel p = null;
                try {
                    System.out.println("enter x value");
                    x = Integer.parseInt(scanner.next());
                    System.out.println("enter y value");
                    y = Integer.parseInt(scanner.next());
                    System.out.println("enter r value, between 0 and 255");
                    r = Integer.parseInt(scanner.next());
                    System.out.println("enter g value, between 0 and 255");
                    g = Integer.parseInt(scanner.next());
                    System.out.println("enter b value, between 0 and 255");
                    b = Integer.parseInt(scanner.next());
                    p = new Pixel(r, g, b);
                    if (choice.equalsIgnoreCase("o")) {
                        pG.setPixel(x, y, p);
                    }
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    System.out.println(e + "\ninvalid numbers. make sure x and y are less than the height and width of the image, and the rgb values are between 0 and 255");
                }
                if (choice.equalsIgnoreCase("m")) {
                    int h = 0;
                    int w = 0;
                    try {
                        System.out.println("enter height value");
                        h = Integer.parseInt(scanner.next());
                        System.out.println("enter width value");
                        w = Integer.parseInt(scanner.next());
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        System.out.println(e + "\ninvalid numbers. make sure x and y are less than the height and width of the image, and the rgb values are between 0 and 255");
                    }
                    pG.setPixels(x, y, w, h, p);
                }
                pG.saveImage(getFilePath());
                wantsToSetPixel();
            } else {
                System.out.println("invalid input. try again.");
                wantsToSetPixel();
            }
        }
    }


    /**
     * is run when a user wants to get the value of a specific pixel
     * @throws IOException in case buffered image doesn't work
     */
    private void wantsValueOfPixel() throws IOException {
        String takenIn = scanner.nextLine();
        String[] takenInSplit = takenIn.split(" ");
        System.out.println("Your selection: get pixel. Provide the x and y coordinates of desired pixel separated with a space");
        if (takenInSplit.length == 2) {
            try {
                int x = Integer.parseInt(takenInSplit[0]);
                int y = Integer.parseInt(takenInSplit[1]);
                System.out.println(pG.getColorAt(x, y));
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                System.out.println(e + "\ntry again");
                wantsValueOfPixel();
            }
            uiMenu();
            userInput();
        }
    }


    /**
     * is run when a user wants the bluest column removed
     * highlights the bluest column and then asks for confirmation to remove it
     * also gives the user the option to quit out of the program
     * @throws IOException in order to accommodate reading the image from imageIO
     */
    private void wantsBluestRemoved() throws IOException {
//     highlights the row and asks for confirmation
        System.out.println("Your selection: remove bluest column." + " Use d to confirm or q to quit");
        pG.highlightBluest(pG.bluestColumnFinder());
        commandsExecuted++;
        pG.saveImage(getFilePath());

        while (!shouldQuit) {
            if (scanner.next().equalsIgnoreCase("d")) {
                // removes the bluest row
                pG.removeBluest(pG.bluestColumnFinder());
                System.out.println("Bluest row has been removed.");
                afterRemoved();
            } else if (scanner.next().equalsIgnoreCase("q")) {
                // quits the program
                System.out.println("exited column remover");
                uiMenu();
                userInput();
            } else {
                // what runs if the user does not enter q or d
                System.out.println("not a valid letter");
                wantsBluestRemoved();
            }
        }
    }

    /**
     * runs after the user removes a column
     * adds to the integer variables that store the number of commands executed and columns deleted
     * @throws IOException in order to accommodate reading the image from imageIO
     */
    private void afterRemoved() throws IOException {
        columnsDeleted++;
        commandsExecuted++;
        stack.push(pG.copy());
        pG.saveImage(getFilePath());
        uiMenu();
        userInput();
    }

    /**
     * is run when a user wants a random column removed
     * highlights a random row and then asks for confirmation to remove it
     * also gives the user the option to quit out of the program
     * @throws IOException in order to accommodate reading the image from imageIO
     */
    private void wantsRandomRemoved() throws IOException {
        int randomCol = pG.getRandom();
        // highlights and then prompts for the next letter
        System.out.println("Your selection: remove a randomly selected column." + " Use d to confirm or q to quit");
        pG.highlightRandom(randomCol);
        commandsExecuted++;
        pG.saveImage(getFilePath());
        while (!shouldQuit) {
            if (scanner.next().equalsIgnoreCase("d")) {
                // removes a random row
                pG.randomRemove(randomCol);
                System.out.println("Random row has been removed.");
                afterRemoved();
            } else if (scanner.next().equalsIgnoreCase("q")) {
                // quits the program
                System.out.println("Program has been quit.");
                shouldQuit = true;
            } else {
                System.out.println("not a valid letter");
                wantsRandomRemoved();
            }
        }
    }

    /**
     * is run when a user asks to undo the last alteration
     * undoes the previous alterations
     * @throws IOException in order to accommodate reading the image from imageIO
     */
    private void undo() throws IOException {
        System.out.println("Your selection: undo the most recent deletion." + " Use d to confirm or q to quit");
        if (scanner.next().equalsIgnoreCase("d")) {
            if(undoNum == 0) {
                stack.pop();
            }
            // this sets the current PixelGrid equal to the image before the most recent task executed
            pG = stack.pop();
            System.out.println("The last task has been undone");
            columnsDeleted--;
            commandsExecuted++;
            undoNum++;
            pG.saveImage(getFilePath());
            uiMenu();
            userInput();
        } else if (scanner.next().equalsIgnoreCase("q")) {
            // quits the program
            System.out.println("Program has been quit.");
            shouldQuit = true;
        } else {
            System.out.println("Not a valid letter. Try again.");
            undo();
        }
    }

    public static void main(String[] args) throws IOException {
        UserInterface test = new UserInterface();
        test.givenFilePath();
    }
}
