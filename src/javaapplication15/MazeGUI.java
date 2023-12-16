/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package javaapplication15;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MazeGUI extends JFrame {
    private int[][] maze;
    private final List<List<Point>> paths;
    private final List<Color> threadColors;

    public MazeGUI(int[][] maze, List<List<Point>> paths, List<Color> threadColors) {
        this.maze = maze;
        this.paths = paths;
        this.threadColors = threadColors;

        setTitle("not a simple Maze");
        setSize(300, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
            }
        });
    }

    private void handleMouseClick(MouseEvent e) {
        int cellSize = 30;
        int xOffset = 50;
        int yOffset = 50;

        int row = (e.getY() - yOffset) / cellSize;
        int col = (e.getX() - xOffset) / cellSize;

        if (row >= 0 && row < maze.length && col >= 0 && col < maze[0].length) {
            maze[row][col] = 1 - maze[row][col];

            // Recalculate paths
            recalculatePaths();

            repaint();
        }
    }

    private void recalculatePaths() {
        paths.clear();
        int mazeSize = maze.length;
        int numThreads = 4;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(new RatMazeSolver(copyMaze(maze), paths));
            threads.add(thread);
            thread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
private static int[][] copyMaze(int[][] original) {
        int rows = original.length;
        int cols = original[0].length;
        int[][] copy = new int[rows][cols];

        for (int i = 0; i < rows; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, cols);
        }

        return copy;
    }
    @Override
    public void paint(Graphics g) {
    super.paint(g);

    int cellSize = 30;
    int xOffset = 50;
    int yOffset = 50;

    for (int i = 0; i < maze.length; i++) {
        for (int j = 0; j < maze[i].length; j++) {
            Color color = maze[i][j] == 1 ? Color.white : Color.black;
            g.setColor(color);
            g.fillRect(xOffset + cellSize * j, yOffset + cellSize * i, cellSize, cellSize);
            g.setColor(Color.BLACK);
            g.drawRect(xOffset + cellSize * j, yOffset + cellSize * i, cellSize, cellSize);
        }
    }
        // Draw each path with its assigned color

// Draw each path with its assigned color
        for (List<Point> path : paths) {
            int index = paths.indexOf(path);
            if (index < threadColors.size()) {  // Check if the index is within bounds
                Color color = threadColors.get(index);

                for (Point point : path) {
                    int x = point.x;
                    int y = point.y;
                    g.setColor(color);
                    g.fillRect(xOffset + cellSize * y, yOffset + cellSize * x, cellSize, cellSize);
                }
            }
        }
    }

    public static void main(String[] args) {
        int mazeSize = Integer.parseInt(JOptionPane.showInputDialog("Enter maze size:"));
        int[][] maze = new int[mazeSize][mazeSize];

        int numThreads = 4;
        List<List<Point>> allPaths = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        List<Color> threadColors = new ArrayList<>();

        // Assign a unique color to each thread
        for (int i = 0; i < numThreads; i++) {
            Color color = new Color((int) (Math.random() * 0x1000000));
            threadColors.add(color);
        }

        SwingUtilities.invokeLater(() -> {
            MazeGUI mazeGUI = new MazeGUI(maze, allPaths, threadColors);
            mazeGUI.setVisible(true);

            // Start threads after GUI is visible
            for (int i = 0; i < numThreads; i++) {
                Thread thread = new Thread(new RatMazeSolver(copyMaze (maze), allPaths));
                threads.add(thread);
                thread.start();
            }

            // Wait for all threads to finish
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Repaint the GUI with the calculated paths
            mazeGUI.recalculatePaths();
            mazeGUI.repaint();
        });
    }
}



