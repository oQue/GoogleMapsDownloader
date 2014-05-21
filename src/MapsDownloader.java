import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.*;

public class MapsDownloader extends JPanel {

    static JTextArea console;
    static JTextField startTextField;
    static JTextField endTextField;
    static JSlider zoomLevel;
    static GoogleMap map;
    static JFileChooser f;

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    private static void updateTextArea(final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                console.append(text);
            }
        });
    }

    private static void redirectSystemStreams() {
        OutputStream out = new OutputStream() {
            public void write(int b) {
                updateTextArea(String.valueOf((char) b));
            }

            public void write(byte[] b, int off, int len) {
                updateTextArea(new String(b, off, len));
            }

            public void write(byte[] b) {
                write(b, 0, b.length);
            }
        };
        System.setOut(new PrintStream(out, true));
        //System.setErr(new PrintStream(out, true));
    }

    private static void initMap(String destination) throws IllegalArgumentException {
        double startLat, startLon, endLat, endLon;
        startLat = Double.parseDouble(startTextField.getText().split(",")[0]);
        startLon = Double.parseDouble(startTextField.getText().split(",")[1]);
        endLat = Double.parseDouble(endTextField.getText().split(",")[0]);
        endLon = Double.parseDouble(endTextField.getText().split(",")[1]);
        int zoom = zoomLevel.getValue();

        if (startLat >= endLat && startLon <= endLon) {
            map = new GoogleMap(startLat, startLon, endLat, endLon, zoom, destination);
        }
        else {
            throw new IllegalArgumentException();
        }
    }

    private static class CreateButtonHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {

            Thread t = new Thread(new Runnable() {
                public void run() {

                    try {
                        initMap("./");
                        f = null;
                        f = new JFileChooser();
                        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        f.setDialogTitle("Location to save image");
                        int result = f.showSaveDialog(null);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            if ( System.getProperty("os.name").toLowerCase().contains("win") )
                                initMap(f.getSelectedFile().toString());
                            else
                                initMap(f.getCurrentDirectory().toString());
                            map.bulkDownload();
                            map.mergeImages();
                        }
                    } catch (IllegalArgumentException ex) {
                        System.out.println("Provide coordinates of north-west and south-east corners.\n" +
                                "Format: 0.0000,0.0000");
                        //System.out.println(ex.toString());
                    } catch (IOException ex) {
                        System.out.println("Check your Internet connection");
                        return;
                    } catch (InterruptedException ex) {
                        System.out.println("An error occurred during downloading");
                        return;
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            console.repaint();
                        }
                    });
                }
            });
            t.start();
        }
    }

    private static class CalculateButtonHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            try {
                initMap("./");
                System.out.println("Image size: " + map.getWidth() + "x" + map.getHeight());
                System.out.println("Number of tiles: " + map.getNumTiles());
            }
            catch (IllegalArgumentException ex) {
                System.out.println("Provide coordinates of north-west and south-east corners.\n" +
                        "Format: 0.0000,0.0000");
                //System.out.println(ex.toString());
            }
        }
    }

    public static void main(String[] args) {

        redirectSystemStreams();

        JLabel startLabel = new javax.swing.JLabel("North-West Coordinates");
        JLabel endLabel = new javax.swing.JLabel("South-East Coordinates");
        JLabel zoomLabel = new javax.swing.JLabel("Zoom Level");
        JButton calculateButton = new javax.swing.JButton("Get map dimensions");
        JButton createButton = new javax.swing.JButton("Create map");
        zoomLevel = new javax.swing.JSlider(10, 22, 16);
        startTextField = new javax.swing.JTextField("N-W coord");
        endTextField = new javax.swing.JTextField("S-E coord");
        console = new javax.swing.JTextArea();
        JScrollPane scroll = new JScrollPane(console);

        CreateButtonHandler create = new CreateButtonHandler();
        createButton.addActionListener(create);

        CalculateButtonHandler calculate = new CalculateButtonHandler();
        calculateButton.addActionListener(calculate);

        zoomLevel.setLabelTable( zoomLevel.createStandardLabels(3) );
        zoomLevel.setMinorTickSpacing(1);
        zoomLevel.setPaintTicks(true);
        zoomLevel.setPaintLabels(true);

        console.setColumns(25);
        console.setRows(10);
        console.setEditable(false);

        scroll.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

        JPanel content = new JPanel();
        content.setLayout(new GridLayout(0, 1, 7, 7) );
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel north = new JPanel();
        north.setLayout(new GridLayout(0, 2, 7, 7));
        JPanel center = new JPanel();
        center.setLayout(new GridLayout(0, 1, 7, 7));
        JPanel south = new JPanel();
        south.setLayout(new BorderLayout());

        content.add(north);
        content.add(center);
        content.add(south);

        north.add(startLabel);
        north.add(startTextField);
        north.add(endLabel);
        north.add(endTextField);
        north.add(zoomLabel);
        north.add(zoomLevel);

        center.add(calculateButton);
        center.add(createButton);

        south.add(scroll, BorderLayout.CENTER);

        JFrame window = new JFrame("Easy Google Maps Downloader");
        window.setContentPane(content);
        window.setSize(550, 400);
        window.setLocation(100, 100);
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

}
