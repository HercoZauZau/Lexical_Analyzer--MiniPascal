# Mini Pascal Lexical Analyzer

This program is a simple Lexical Analyzer for a subset of the Pascal programming language, known as Mini Pascal. It provides a graphical user interface for managing source code, illustrating the tokens with their respective classes and displaying the elapsed time of the analysis.

## Features

- Code source management: You can enter and edit the Mini Pascal source code using a text editor.
- Token illustration: The program displays the tokens found in the source code along with their respective classes in a table.
- Export/Import: You can export the source code to a file or import source code from a file.

## Technologies Used

- Java
- Java Swing (for the GUI)

## Getting Started

1. Make sure you have Java installed on your system.
2. Download the Mini Pascal Lexical Analyzer program.
3. Compile the Java source files.
4. Run the program using the main class `Main`.

## Usage

1. Launch the program.
2. Enter the Mini Pascal source code in the text editor.
3. Click the "Analyze" button to perform the lexical analysis.
4. The table will display the tokens found in the source code, along with their classes.
5. You can export the source code using the "Export" button or import source code using the "Import" button.

## Examples

Here are a few examples to help you understand the program:

### Example 1: Valid Mini Pascal Code

```pascal
program HelloWorld;
begin
    writeln('Hello, World!');
end.
```

After analyzing the code, the program will display the following tokens:

| Lexeme      | Token Class       | Line |
|-------------|-------------------|------|
| program     | Reserved Keyword  | 1    |
| HelloWorld  | Identifier        | 1    |
| ;           | Punctuation       | 1    |
| begin       | Reserved Keyword  | 2    |
| writeln     | Identifier        | 3    |
| (           | Punctuation       | 3    |
| 'Hello, World!' | String           | 3    |
| )           | Punctuation       | 3    |
| ;           | Punctuation       | 3    |
| end         | Reserved Keyword  | 4    |
| .           | Punctuation       | 4    |

## Contributing

Contributions are welcome! If you find any issues or have suggestions for improvements, please feel free to open an issue or submit a pull request.

## License

This project is licensed under the MIT License. You can find the details in the [LICENSE](LICENSE) file.

## Acknowledgments

- The Mini Pascal programming language is based on the Pascal language.
- This program was developed as a learning exercise and is not intended for production use.
