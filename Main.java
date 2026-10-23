
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Main {
    public static void main(String[] args) {
        boolean trace = false;
        String fileName = null;

        if (args.length == 1) {
            if (args[0].equals("--trace")) {
                trace = true;
            } else {
                fileName = args[0];
            }
        } else if (args.length == 2 && args[0].equals("--trace")) {
            trace = true;
            fileName = args[1];
        } else if (args.length > 0) {
            System.out.println(
                    "Usage: java Main [--trace] [source-file]");
            return;
        }

        // Open a file chooser if no filename was provided.
        if (fileName == null) {
            JFileChooser chooser =
                    new JFileChooser(new File("."));

            chooser.setDialogTitle(
                    "Choose a MiniLang program");

            chooser.setFileFilter(
                    new FileNameExtensionFilter(
                            "Text files (*.txt)", "txt"));

            int result = chooser.showOpenDialog(null);

            if (result != JFileChooser.APPROVE_OPTION) {
                System.out.println("No file selected.");
                return;
            }

            fileName = chooser.getSelectedFile()
                    .getAbsolutePath();
        }

        try {
            String content = new String(
                    Files.readAllBytes(Paths.get(fileName)));

            Lexer lexer = new Lexer(content);
            List<Token> tokens = lexer.tokenize();

            if (trace) {
                System.out.println(
                        "[TOKEN] Recognized tokens:");

                for (Token token : tokens) {
                    System.out.println("[TOKEN] " + token);
                }

                System.out.println(
                        "[PARSE] Starting program");
            }

            Map<String, Object> variables =
                    new HashMap<String, Object>();

            Parser parser =
                    new Parser(tokens, variables, trace);

            parser.parseProgram();

            if (trace) {
                System.out.println(
                        "[TRACE] Program finished.");
            }

        } catch (Exception error) {
            System.out.println(
                    "Error: " + error.getMessage());
        }
    }
}
