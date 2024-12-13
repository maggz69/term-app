package src;

import java.io.IOException;

public class Screen {
    private int width;
    private int height;
    private int colorMode;

    private char[][] screenBuffer;

    public Screen(int width, int height, int colorMode) {
        this.width = width;
        this.height = height;
        this.colorMode = colorMode;
        this.screenBuffer = new char[height][width];
        clearScreen();
    }

    public Screen() {
        this(80, 25, 2);
    }

    public void clearScreen(){
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                screenBuffer[i][j] = ' ';
            }
        }
    }

    public void refreshScreen() {
        try {
            new ProcessBuilder("clear").inheritIO().start().waitFor();
        }catch(IOException | InterruptedException e) {
            System.out.println("Error while clearing the screen " + e.getMessage());
        }

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                System.out.print(screenBuffer[i][j]);
            }
            System.out.println();
        }
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public void drawCharacter(int x, int y,int colorIndex, char c) {
        if(!isWithinBounds(x, y)) {
            throw new IllegalArgumentException("Coordinates are out of bounds");
        }
        screenBuffer[y][x] = c;
    }

    public void drawLine(int x1, int y1, int x2, int y2, int colorIndex, char c){
        // draw a line from (x1, y1) to (x2, y2) with colorIndex and character c

        if(!isWithinBounds(x1, y1) || !isWithinBounds(x2, y2)) {
            throw new IllegalArgumentException("Coordinates are out of bounds");
        }

        if(x1 == x2) {
            for(int i = Math.min(y1, y2); i <= Math.max(y1, y2); i++) {
                screenBuffer[i][x1] = c;
            }
        }else if(y1 == y2) {
            for(int i = Math.min(x1, x2); i <= Math.max(x1, x2); i++) {
                screenBuffer[y1][i] = c;
            }
        }else {
            throw new IllegalArgumentException("Only horizontal and vertical lines are supported");
        }
    }




}