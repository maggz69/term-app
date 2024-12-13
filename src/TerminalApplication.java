package src;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

interface Command {

    int commandLength = 4;
    byte commandCode = 0x01;
    byte[] data;

    void executeCommand(byte[] data, Screen screen) throws IllegalArgumentException;

}

public class TerminalApplication {

    public static void main(String[] args) {
        try {
            InputStream inputStream = System.in;
            Screen screen = null;

            Map<Byte, Command> commands = initializeCommands(inputStream);
        } catch (Exception e) {
            System.out.println("Error while initializing the terminal application " + e.getMessage());
        }
    }

    private static Map<Byte, Command> initializeCommands(InputStream inputStream) {
        Map<Byte, Command> commands = new HashMap<>();

        // read the first byte to ensure it is the ScreenSetupCommand
        ScreenSetupCommand screenSetupCommand = new ScreenSetupCommand();
        byte[] screenSetup = new byte[screenSetupCommand.commandLength];

        try {
            inputStream.read(screenSetup);
            screenSetupCommand.addCommandParameters(screenSetup[1], screenSetup[2], screenSetup[3]);
        } catch (Exception e) {
            System.out.println("Error while reading the screen setup command " + e.getMessage());
        }

        commands.put(screenSetupCommand.commandCode, screenSetupCommand);

        // loop to read the rest of the commands
        // read each byte then determine the command code, read the command.length and add the command to the map
        // if the command code is not found, print an error message, if the command code ix 0xff, break the loop

        boolean isBreakCondition = false;

        int offset = 3;

        byte[] dataCursor = new byte[1];

        while(!isBreakCondition) {
            try {
                inputStream.read(dataCursor, offset, 1);

                if(dataCursor[0] == 0xff) {
                    isBreakCondition = true;
                    break;
                }


            }
        }

        return commands;
    }

    private Command mapCommandCodeToCommand(byte commandCode, Screen screen) throws IllegalArgumentException {
        switch (commandCode) {
            case 0x2:
                return new DrawCharacterCommand(screen);
            case 0x3:
            default:
                throw new IllegalArgumentException("Invalid command code");
        }
    }

}

class ScreenSetupCommand implements Command {
    Screen screen;

    byte commandCode;
    int commandLength;
    byte[] data;

    ScreenSetupCommand() {
        this.commandCode = 0x01;
        this.commandLength = 4;
    }

    @Override
    public void executeCommand(byte[] data, Screen screen) throws IllegalArgumentException {
        if (data.length != commandLength) {
            throw new IllegalArgumentException("Invalid data length");
        }

        int width = data[1];
        int height = data[2];
        int colorMode = data[3];

        this.screen = new Screen(width, height, colorMode);
    }

    public void addCommandParameters(int width, int height, int colorMode) {
        data = new byte[commandLength];

        data[0] = (byte) width;
        data[1] = (byte) height;
        data[2] = (byte) colorMode;
    }
}

class DrawCharacterCommand implements Command {

    byte commandCode;
    int commandLength;
    Screen screen;
    byte[] data;

    DrawCharacterCommand(Screen screen) {
        this.commandCode = 0x02;
        this.commandLength = 4;
        this.screen = screen;
    }

    @Override
    public void executeCommand(byte[] data, Screen screen) throws IllegalArgumentException {
        screen.drawCharacter((int) data[0], (int) data[1], (int) data[2], (char) data[3]);
    }

    public void addCommandParameters(int x, int y, int colorIndex, char c) {
        data = new byte[commandLength];

        data[0] = (byte) x;
        data[1] = (byte) y;
        data[2] = (byte) colorIndex;
        data[3] = (byte) c;
    }
}

class DrawLineCommand implements Command {

    byte commandCode;
    int commandLength;
    Screen screen;
    byte[] data;

    DrawLineCommand(Screen screen) {
        this.commandCode = 0x03;
        this.commandLength = 8;
        this.screen = screen;
    }

    @Override
    public void executeCommand(byte[] data, Screen screen) throws IllegalArgumentException {
        screen.drawLine((int) data[0], (int) data[1], (int) data[2], (int) data[3], (int) data[4], (char) data[5]);
    }

    public void addCommandParameters(int x1, int y1, int x2, int y2, int colorIndex, char c) {
        data = new byte[commandLength];

        data[0] = (byte) x1;
        data[1] = (byte) y1;
        data[2] = (byte) x2;
        data[3] = (byte) y2;
        data[4] = (byte) colorIndex;
        data[5] = (byte) c;
    }
}

class RenderTextCommand implements Command {

    byte commandCode;
    int commandLength;
    Screen screen;
    byte[] data;
    byte[] text;

    RenderTextCommand(Screen screen) {
        this.commandCode = 0x04;
        this.commandLength = 4;
        this.screen = screen;
    }

    @Override
    public void executeCommand(byte[] data, Screen screen) throws IllegalArgumentException {
        screen.renderText((int) data[0], (int) data[1], (int) data[2], text);
    }

    public void addCommandParameters(int x, int y, int colorIndex, byte[] text) {
        data = new byte[commandLength];

        data[0] = (byte) x;
        data[1] = (byte) y;
        data[2] = (byte) colorIndex;
        this.text = text;
    }
}

class MoveCursorCommand implements Command {

    byte commandCode;
    int commandLength;
    Screen screen;
    byte[] data;

    MoveCursorCommand(Screen screen) {
        this.commandCode = 0x05;
        this.commandLength = 2;
        this.screen = screen;
    }

    @Override
    public void executeCommand(byte[] data, Screen screen) throws IllegalArgumentException {
        screen.moveCursor((int) data[0], (int) data[1]);
    }

    public void addCommandParameters(int x, int y) {
        data = new byte[commandLength];

        data[0] = (byte) x;
        data[1] = (byte) y;
    }
}

class DrawAtCursorCommand implements Command {

    byte commandCode;
    int commandLength;
    Screen screen;
    byte[] data;

    DrawAtCursorCommand(Screen screen) {
        this.commandCode = 0x06;
        this.commandLength = 2;
        this.screen = screen;
    }

    @Override
    public void executeCommand(byte[] data, Screen screen) throws IllegalArgumentException {
        screen.drawAtCursor((char) data[0], data[1]);
    }

    public void addCommandParameters(char c, int colorIndex) {
        data = new byte[commandLength];

        data[0] = (byte) c;
        data[1] = (byte) colorIndex;
    }
}
