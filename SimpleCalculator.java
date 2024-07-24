import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleCalculator {
    private JFrame frame;
    private JTextField textField;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SimpleCalculator window = new SimpleCalculator();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public SimpleCalculator() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 250, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        textField = new JTextField();
        textField.setBounds(20, 10, 200, 30);
        frame.getContentPane().add(textField);
        textField.setColumns(10);

        // creating number buttons and operations
        int x = 20, y = 50;
        for (int i = 9; i >= 0; i--) {
            addButton(frame, String.valueOf(i), x, y);
            x += 60;
            if (x >= 200) {
                x = 20;
                y += 50;
            }
        }

        addButton(frame, "+", 20, 250);
        addButton(frame, "-", 80, 250);
        addButton(frame, "*", 140, 250);
        addButton(frame, "/", 200, 250);
        addButton(frame, "=", 20, 300);
        addButton(frame, "C", 80, 300);
    }

    private void addButton(JFrame frame, String text, int x, int y) {
        JButton btn = new JButton(text);
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (text.equals("=")) {
                    try {
                        textField.setText(String.valueOf(eval(textField.getText())));
                    } catch (Exception ex) {
                        textField.setText("Error");
                    }
                } else if (text.equals("C")) {
                    textField.setText("");
                } else {
                    textField.setText(textField.getText() + text);
                }
            }
        });
        btn.setBounds(x, y, 50, 40);
        frame.getContentPane().add(btn);
    }

    public static double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                return x;
            }
        }.parse();
    }
}
