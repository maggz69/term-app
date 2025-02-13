package src;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

interface Command {

    int commandLength = 1;
    byte commandCode = 0x00;

    void executeCommand() throws IllegalArgumentException;

    void setDataBytes(byte[] data);
    int getCommandLength();

}

public class TerminalApplication {

    Screen screen;
    Map<Byte, Command> commands;

    public static void main(String[] args) {
        TerminalApplication terminalApplication = new TerminalApplication();
        try {
            InputStream inputStream = System.in;
            terminalApplication.commands = terminalApplication.initializeCommands(inputStream);

            // loop through the commands and execute them
            for (int i = 0; i < terminalApplication.getCommandsLength(); i++) {
                Command command = terminalApplication.commands.get((byte) i);
                command.executeCommand();
            }
        } catch (Exception e) {
            System.out.println("Error while initializing the terminal application " + e.getMessage());
        }
    }

    private int getCommandsLength() {
        return commands.size();
    }

    private Map<Byte, Command> initializeCommands(InputStream inputStream) {
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
        // read each byte then determine the command code, read the command.length and
        // add the command to the map
        // if the command code is not found, print an error message, if the command code
        // ix 0xff, break the loop

        boolean isBreakCondition = false;

        int offset = 3;

        byte[] dataCursor = new byte[1];

        while (!isBreakCondition) {
            try {
                // read one byte at a time
                inputStream.read(dataCursor, offset, 1);

                if (dataCursor[0] == 0xff) {
                    isBreakCondition = true;
                    break;
                }

                byte commandCode = dataCursor[0];
                Command command = mapCommandCodeToCommand(commandCode, screenSetupCommand.screen);

                // read the command length
                byte[] dataBytes = new byte[command.getCommandLength()];
                inputStream.read(dataBytes,offset, command.getCommandLength());

                // adjust the offset
                offset += command.getCommandLength();

                command.setDataBytes(dataBytes);

                commands.put(commandCode, command);

            } catch (Exception e) {
                System.out.println("Error while reading the command code " + e.getMessage());
            }
        }

        return commands;
    }

    private Command mapCommandCodeToCommand(byte commandCode, Screen screen) throws IllegalArgumentException {
        switch (commandCode) {
            case 0x2:
                return new DrawCharacterCommand(screen);
            case 0x3:
                return new DrawLineCommand(screen);
            case 0x4:
                return new RenderTextCommand(screen);
            case 0x5:
                return new MoveCursorCommand(screen);
            case 0x6:
                return new DrawAtCursorCommand(screen);
            case 0x7:
                return new ClearScreenCommand(screen);
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
    public void executeCommand() throws IllegalArgumentException {
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

    @Override
    public int getCommandLength() {
        return commandLength;
    }

    @Override
    public void setDataBytes(byte[] data) {
        this.data = data;
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
    public void executeCommand() throws IllegalArgumentException {
        screen.drawCharacter((int) data[0], (int) data[1], (int) data[2], (char) data[3]);
    }

    public void addCommandParameters(int x, int y, int colorIndex, char c) {
        data = new byte[commandLength];

        data[0] = (byte) x;
        data[1] = (byte) y;
        data[2] = (byte) colorIndex;
        data[3] = (byte) c;
    }

    @Override
    public void setDataBytes(byte[] data) {
        this.data = data;
    }

    @Override
    public int getCommandLength() {
        return commandLength;
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
    public void executeCommand() throws IllegalArgumentException {
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

    @Override
    public void setDataBytes(byte[] data) {
        this.data = data;
    }

    @Override
    public int getCommandLength() {
        return commandLength;
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
    public void executeCommand() throws IllegalArgumentException {
        screen.renderText((int) data[0], (int) data[1], (int) data[2], text);
    }

    public void addCommandParameters(int x, int y, int colorIndex, byte[] text) {
        data = new byte[commandLength];

        data[0] = (byte) x;
        data[1] = (byte) y;
        data[2] = (byte) colorIndex;
        this.text = text;
    }

    @Override
    public void setDataBytes(byte[] data) {
        this.data = data;
    }

    @Override
    public int getCommandLength() {
        return commandLength;
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
    public void executeCommand() throws IllegalArgumentException {
        screen.moveCursor((int) data[0], (int) data[1]);
    }

    public void addCommandParameters(int x, int y) {
        data = new byte[commandLength];

        data[0] = (byte) x;
        data[1] = (byte) y;
    }

    @Override
    public void setDataBytes(byte[] data) {
        this.data = data;
    }

    @Override
    public int getCommandLength() {
        return commandLength;
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
    public void executeCommand() throws IllegalArgumentException {
        screen.drawAtCursor((char) data[0], data[1]);
    }

    public void addCommandParameters(char c, int colorIndex) {
        data = new byte[commandLength];

        data[0] = (byte) c;
        data[1] = (byte) colorIndex;
    }

    @Override
    public void setDataBytes(byte[] data) {
        this.data = data;
    }

    @Override
    public int getCommandLength() {
        return commandLength;
    }
}

class ClearScreenCommand implements Command {

    byte commandCode;
    int commandLength;
    Screen screen;
    byte[] data;

    ClearScreenCommand(Screen screen) {
        this.commandCode = 0x07;
        this.commandLength = 0;
        this.screen = screen;
    }

    @Override
    public void executeCommand() throws IllegalArgumentException {
        screen.clearScreen();
    }

    public void addCommandParameters() {
        data = new byte[commandLength];
    }

    @Override
    public void setDataBytes(byte[] data) {
        this.data = data;
    }

    @Override
    public int getCommandLength() {
        return commandLength;
    }
}
