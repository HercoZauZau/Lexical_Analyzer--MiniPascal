import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Token {
    private String lexeme;
    private String tokenClass;
    private int line;

    public Token(String lexeme, String tokenClass, int line) {
        this.lexeme = lexeme;
        this.tokenClass = tokenClass;
        this.line = line;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String getTokenClass() {
        return tokenClass;
    }

    public int getLine() {
        return line;
    }
}

class LexicalAnalyzer {
    private static final String[] RESERVED_KEYWORDS = {
            "program", "var", "begin", "end", "integer", "real", "if", "then", "else", "while", "do"
    };
    private static final String[] RELATIONAL_OPERATORS = {
            "=", "<>", "<", "<=", ">=", ">", "or", "and"
    };
    private static final String[] ARITHMETIC_OPERATORS = {
            "+", "-", "*", "/"
    };

    public List<Token> analyze(String sourceCode) throws LexicalException {
        List<Token> tokens = new ArrayList<>();
        String[] lines = sourceCode.split("\\r?\\n");

        boolean inComment = false;
        boolean inString = false;
        StringBuilder stringToken = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];

            int commentIndex = line.indexOf("//");
            if (commentIndex != -1) {
                line = line.substring(0, commentIndex);
            }

            if (inComment) {
                int endCommentIndex = line.indexOf("*/");
                if (endCommentIndex != -1) {
                    inComment = false;
                    line = line.substring(endCommentIndex + 2);
                } else {
                    continue;
                }
            }

            line = addSpacesToPunctuation(line);
            String[] words = line.split("\\s+");

            for (String word : words) {
                if (inString) {
                    if (word.endsWith("'")) {
                        inString = false;
                        stringToken.append(" ").append(word.substring(0, word.length() - 1));
                        tokens.add(new Token(stringToken.toString(), "String", i + 1));
                        stringToken.setLength(0);
                    } else {
                        stringToken.append(" ").append(word);
                    }
                } else if (isReservedKeyword(word)) {
                    tokens.add(new Token(word, "Reserved Keyword", i + 1));
                } else if (isRelationalOperator(word)) {
                    tokens.add(new Token(word, "Relational Operator", i + 1));
                } else if (isArithmeticOperator(word)) {
                    tokens.add(new Token(word, "Arithmetic Operator", i + 1));
                } else if (isIdentifier(word)) {
                    tokens.add(new Token(word, "Identifier", i + 1));
                } else if (isNumber(word)) {
                    tokens.add(new Token(word, "Number", i + 1));
                } else if (isStringStart(word)) {
                    inString = true;
                    stringToken.append(word.substring(1));
                } else if (isCommentStart(word)) {
                    if (!word.endsWith("*/")) {
                        inComment = true;
                    }
                }
            }
        }

        return tokens;
    }

    private String addSpacesToPunctuation(String line) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (isPunctuation(c)) {
                if (i > 0 && !isInQuotes(line, i)) {
                    sb.append(" ");
                }
                sb.append(c);
                if (i < line.length() - 1 && !isInQuotes(line, i)) {
                    sb.append(" ");
                }
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private boolean isInQuotes(String line, int index) {
        boolean inSingleQuotes = false;
        boolean inDoubleQuotes = false;

        for (int i = 0; i < index; i++) {
            char c = line.charAt(i);
            if (c == '\'') {
                inSingleQuotes = !inSingleQuotes;
            } else if (c == '"') {
                inDoubleQuotes = !inDoubleQuotes;
            }
        }

        return inSingleQuotes || inDoubleQuotes;
    }

    private boolean isPunctuation(char c) {
        return Character.getType(c) == Character.OTHER_PUNCTUATION;
    }

    private boolean isReservedKeyword(String word) {
        for (String keyword : RESERVED_KEYWORDS) {
            if (keyword.equalsIgnoreCase(word)) {
                return true;
            }
        }
        return false;
    }

    private boolean isRelationalOperator(String word) {
        for (String operator : RELATIONAL_OPERATORS) {
            if (operator.equals(word)) {
                return true;
            }
        }
        return false;
    }

    private boolean isArithmeticOperator(String word) {
        for (String operator : ARITHMETIC_OPERATORS) {
            if (operator.equals(word)) {
                return true;
            }
        }
        return false;
    }

    private boolean isIdentifier(String word) {
        return word.matches("[a-zA-Z][a-zA-Z0-9]*");
    }

    private boolean isNumber(String word) {
        return word.matches("\\d+");
    }

    private boolean isStringStart(String word) {
        return word.startsWith("'") && !word.endsWith("'");
    }

    private boolean isCommentStart(String word) {
        return word.startsWith("/*") && !word.endsWith("*/");
    }
}

class LexicalException extends Exception {
    public LexicalException(String message) {
        super(message);
    }
}

class Controller {
    private LexicalAnalyzer lexicalAnalyzer;
    private View view;

    public Controller(LexicalAnalyzer lexicalAnalyzer, View view) {
        this.lexicalAnalyzer = lexicalAnalyzer;
        this.view = view;
    }

    public void analyzeSourceCode(String sourceCode) {
        try {
            long startTime = System.currentTimeMillis();
            List<Token> tokens = lexicalAnalyzer.analyze(sourceCode);
            long elapsedTime = System.currentTimeMillis() - startTime;
            view.displayTokens(tokens, elapsedTime);
        } catch (LexicalException e) {
            view.displayError(e.getMessage());
        }
    }

    public void exportSourceCode(String sourceCode, File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(sourceCode);
            JOptionPane.showMessageDialog(view.getFrame(), "Source code exported successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view.getFrame(), "Failed to export source code.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String importSourceCode(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder sourceCode = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sourceCode.append(line).append("\n");
            }
            return sourceCode.toString();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(view.getFrame(), "Failed to import source code.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return "";
    }
}

class View {
    private JFrame frame;
    private JTextArea sourceCodeTextArea;
    private JTable tokenTable;
    private DefaultTableModel tokenTableModel;
    private JLabel elapsedTimeLabel;

    public View() {
        frame = new JFrame("Pascal Lexical Analyzer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        sourceCodeTextArea = new JTextArea();
        sourceCodeTextArea.setLineWrap(true);
        JScrollPane sourceCodeScrollPane = new JScrollPane(sourceCodeTextArea);
        frame.add(sourceCodeScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        JButton analyzeButton = new JButton("Analyze");
        analyzeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sourceCode = sourceCodeTextArea.getText();
                Controller controller = new Controller(new LexicalAnalyzer(), View.this);
                controller.analyzeSourceCode(sourceCode);
            }
        });
        buttonPanel.add(analyzeButton);

        JButton exportButton = new JButton("Export");
        exportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String sourceCode = sourceCodeTextArea.getText();
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    Controller controller = new Controller(new LexicalAnalyzer(), View.this);
                    controller.exportSourceCode(sourceCode, file);
                }
            }
        });
        buttonPanel.add(exportButton);

        JButton importButton = new JButton("Import");
        importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                if (fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    Controller controller = new Controller(new LexicalAnalyzer(), View.this);
                    String sourceCode = controller.importSourceCode(file);
                    sourceCodeTextArea.setText(sourceCode);
                }
            }
        });
        buttonPanel.add(importButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        JPanel tokenPanel = new JPanel();
        tokenPanel.setLayout(new BorderLayout());

        tokenTableModel = new DefaultTableModel(new Object[]{"Lexeme", "Token Class", "Line"}, 0);
        tokenTable = new JTable(tokenTableModel);
        JScrollPane tokenScrollPane = new JScrollPane(tokenTable);
        tokenPanel.add(tokenScrollPane, BorderLayout.CENTER);

        elapsedTimeLabel = new JLabel("Elapsed Time: 0 ms");
        tokenPanel.add(elapsedTimeLabel, BorderLayout.SOUTH);

        frame.add(tokenPanel, BorderLayout.EAST);

        frame.setVisible(true);
    }

    public void displayTokens(List<Token> tokens, long elapsedTime) {
        tokenTableModel.setRowCount(0);
        for (Token token : tokens) {
            tokenTableModel.addRow(new Object[]{token.getLexeme(), token.getTokenClass(), token.getLine()});
        }
        elapsedTimeLabel.setText("Elapsed Time: " + elapsedTime + " ms");
    }

    public void displayError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Lexical Error", JOptionPane.ERROR_MESSAGE);
    }

    public JFrame getFrame() {
        return frame;
    }
}

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new View();
            }
        });
    }
}
