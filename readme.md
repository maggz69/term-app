# Terminal Application

This application simulates a terminal screen where you can draw characters, lines, and render text using byte commands. The application reads commands from the standard input and executes them on the screen.

## How to Run

To run the Terminal Application, you need to provide byte commands through the command line. Each command consists of a command code followed by the necessary parameters.

### Sample Commands

1. **Screen Setup Command**: This command sets up the screen with the specified width, height, and color mode.
    - Command Code: `0x01`
    - Parameters: `width`, `height`, `colorMode`
    - Example: `0x01 80 25 2`

2. **Draw Character Command**: This command draws a character at the specified coordinates with the given color index.
    - Command Code: `0x02`
    - Parameters: `x`, `y`, `colorIndex`, `character`
    - Example: `0x02 10 5 1 65` (Draws character 'A' at position (10, 5) with color index 1)

### Running the Application

To run the application with the above commands, you can use the following steps:

1. **Compile the Application**:
    ```bash
    mvn clean install
    ```

2. **Run the Application**:
    ```bash
    echo -e "\x01\x50\x19\x02\x02\x0A\x05\x01\x41" | java TerminalApplication
    ```

    This command sets up the screen with width 80, height 25, color mode 2, and then draws the character 'A' at position (10, 5) with color index 1.

### Additional Sample Input

1. **Draw Line Command**: This command draws a horizontal or vertical line between two points with the given color index and character.
    - Command Code: `0x03`
    - Parameters: `x1`, `y1`, `x2`, `y2`, `colorIndex`, `character`
    - Example: `0x03 5 5 15 5 1 45` (Draws a horizontal line from (5, 5) to (15, 5) with color index 1 and character '-')

2. **Render Text Command**: This command renders text starting at the specified coordinates with the given color index.
    - Command Code: `0x04`
    - Parameters: `x`, `y`, `colorIndex`, `text`
    - Example: `0x04 20 10 2 "Hello"` (Renders the text "Hello" starting at position (20, 10) with color index 2)

To run the application with these commands:
```bash
echo -e "\x01\x50\x19\x02\x03\x05\x05\x0F\x05\x01\x2D\x04\x14\x0A\x02\x48\x65\x6C\x6C\x6F" | java TerminalApplication
```

This command sets up the screen, draws a horizontal line, and renders the text "Hello".

## Notes

- Ensure that the byte commands are correctly formatted and provided in the correct sequence.
- The application currently supports only horizontal and vertical lines.

For more details, refer to the source code and the command implementations in the `src` directory.