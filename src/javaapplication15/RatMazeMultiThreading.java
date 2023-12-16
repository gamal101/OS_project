/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package javaapplication15;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

class RatMazeSolver implements Runnable {
    private static int threadCount = 0;
    private static final ReentrantLock lock = new ReentrantLock();
    private static final AtomicBoolean finish = new AtomicBoolean(false);

    private int[][] maze;
    private List<List<Point>>
           allPaths;
    private int threadId;

    public RatMazeSolver(int[][] maze, List<List<Point>> allPaths) {
        this.maze = maze;
        this.allPaths = allPaths;
        this.threadId = getNextThreadId();
    }

    private int getNextThreadId() {
        lock.lock();
        try {
            return threadCount++;
        } finally {
            lock.unlock();
        }
    }

    private void solveMazeUtil(int x, int y, List<Point> path) {
        int N = maze.length;

        if (x == N - 1 && y == N - 1) {
            path.add(new Point(x, y));
            allPaths.add(new ArrayList<>(path));
            path.remove(path.size() - 1);
            return;
        }

        if (isValidMove(x, y)) {
            path.add(new Point(x, y));

            // Move down
            solveMazeUtil(x + 1, y, path);

            // Move right
            solveMazeUtil(x, y + 1, path);

            // Backtrack
            path.remove(path.size() - 1);
        }
    }

    private boolean isValidMove(int x, int y) {
        int N = maze.length;
        return (x >= 0 && x < N && y >= 0 && y < N && maze[x][y] == 1);
    }

    @Override
    public void run() {
        //System.out.println("Thread " + threadId + " started.");

        List<Point> path = new ArrayList<>();
        solveMazeUtil(0, 0, path);

       // System.out.println("Thread " + threadId + " finished.");
        finish.set(true);
    }
}



