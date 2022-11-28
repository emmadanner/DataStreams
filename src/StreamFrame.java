import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamFrame extends JFrame
{
    JButton quitButton, chooserButton, loadButton;
    JPanel mainPanel, titlePanel, displayPanel, buttonPanel, stringPanel, filePanel, filterPanel, otherPanel;
    JLabel titleLabel, stringLabel, fileLabel;
    JTextArea originalFile, filteredFile;
    JTextField stringTextField, fileTextField;
    JScrollPane scroller;
    JFileChooser chooser = new JFileChooser();

    File fileChoice;
    String fileName;

    List<String> fileInformation;

    public StreamFrame()
    {
        setTitle("Data Stream");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        add(mainPanel);

        otherPanel = new JPanel();
        otherPanel.setLayout(new GridLayout(2, 1));

        displayPanel = new JPanel();
        displayPanel.setLayout(new GridLayout(1, 2));

        mainPanel.add(otherPanel, BorderLayout.NORTH);
        mainPanel.add(displayPanel, BorderLayout.CENTER);

        createTitlePanel();
        createStringChoicePanel();
        createDisplayPanel();
        createFilterPanel();
        createButtonPanel();

        setVisible(true);
    }

    private void createTitlePanel()
    {
        titlePanel = new JPanel();
        titleLabel = new JLabel("Data Streams", JLabel.CENTER);
        titleLabel.setVerticalTextPosition(JLabel.BOTTOM);
        titleLabel.setHorizontalTextPosition(JLabel.CENTER);
        titleLabel.setFont(new Font("Comic Sans Ms", Font.BOLD, 30));

        titlePanel.add(titleLabel);
        otherPanel.add(titlePanel, new GridLayout(1, 1));
    }

    private void createStringChoicePanel()
    {
        stringPanel = new JPanel();

        fileLabel = new JLabel("File: ");
        fileTextField = new JTextField("                        ");
        fileLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 25));

        fileTextField.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        fileTextField.setEditable(false);

        stringLabel = new JLabel("Search for: ");
        stringTextField = new JTextField("                        ");
        stringLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 25));

        stringTextField.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        stringTextField.setEditable(false);

        stringPanel.add(fileLabel);
        stringPanel.add(fileTextField);
        stringPanel.add(stringLabel);
        stringPanel.add(stringTextField);

        otherPanel.add(stringPanel, new GridLayout(2, 1));
    }

    private void createDisplayPanel()
    {
        filePanel = new JPanel();

        originalFile = new JTextArea(30, 90);
        originalFile.setFont(new Font("Comic Sans Ms", Font.PLAIN, 15));
        originalFile.setEditable(false);
        scroller = new JScrollPane(originalFile);

        filePanel.add(scroller);
        displayPanel.add(filePanel, new GridLayout(1, 1));
    }

    private void createFilterPanel()
    {
        filterPanel = new JPanel();

        filteredFile = new JTextArea(30, 90);
        filteredFile.setFont(new Font("Comic Sans Ms", Font.PLAIN, 15));
        filteredFile.setEditable(false);
        scroller = new JScrollPane(filteredFile);

        filterPanel.add(scroller);
        displayPanel.add(filterPanel, new GridLayout(1, 2));
    }

    private void createButtonPanel()
    {
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));

        loadButton = new JButton("Pick your file:");
        loadButton.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
        loadButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                fileRead();
            }
        });

        chooserButton = new JButton("Search for text");
        chooserButton.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
        chooserButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                fileFilter();
            }
        });

        quitButton = new JButton("Quit");
        quitButton.setFont(new Font("Comic Sans MS", Font.BOLD, 25));
        quitButton.addActionListener(new ActionListener()
        {
            JOptionPane pane = new JOptionPane();
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int result = JOptionPane.showConfirmDialog(pane, "Are you sure you really want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION)
                {
                    System.exit(0);
                }
                else
                {
                    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                }
            }
        });

        buttonPanel.add(loadButton);
        buttonPanel.add(chooserButton);
        buttonPanel.add(quitButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void fileRead()
    {
        Path target = new File(System.getProperty("user.dir")).toPath();
        target = target.resolve("src");
        chooser.setCurrentDirectory((target.toFile()));

        try
        {
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
            {
                target = chooser.getSelectedFile().toPath();

                Scanner inFile = new Scanner(target);

                while (inFile.hasNextLine())
                {
                    fileChoice = chooser.getSelectedFile();
                    Path file = fileChoice.toPath();
                    fileName = fileChoice.getName();
                    fileTextField.setText(fileName);
                    fileInformation = new ArrayList<>();

                    try (Stream<String> stream = Files.lines(Paths.get(String.valueOf(file))))
                    {
                        fileInformation = stream
                                            .map(String::toUpperCase)
                                            .collect(Collectors.toList());
                    }
                    for (Object line : fileInformation)
                    {
                        originalFile.append(line +"\n");
                    }
                    originalFile.append("\n\n");
                    break;
                }
                inFile.close();
            }
            else
            {
                originalFile.setText("Didn't set a file.  Try again");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void fileFilter()
    {
        String words = JOptionPane.showInputDialog("Enter the word you want to search the file for: ");
        stringTextField.setText(words);

        List<String> results = fileInformation.stream().filter(str -> str.toLowerCase().replaceAll("[^A-Za-z]", " ").contains(words)).collect(Collectors.toList());

        for (String lines : results)
        {
            filteredFile.append(lines + "\n");
        }
        filteredFile.append("\n\n");
    }
}
