package de.ender;

import de.ender.coordinates.CoordinateSystem;
import de.ender.functions.Free;
import de.ender.functions.Function;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
     void main() {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setSize(1000, 1000);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());  // Use BorderLayout for automatic resizing

            CoordinateSystem coordinateSystem = new CoordinateSystem(Math.PI / 6, 5, 1, 5, frame);

            frame.add(coordinateSystem, BorderLayout.CENTER);

            frame.setVisible(true);

            //background thread
            Thread consoleThread = new Thread(() -> {
                Scanner scan = new Scanner(System.in);
                while (true) {
                    String line;
                    try {
                        line = scan.nextLine();
                    } catch (Exception e) {
                        break;
                    }
                    if (line == null) continue;
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    if (line.startsWith("add ")) {
                        String[] parts = line.split(" ", 2);
                        if (parts.length < 2) {
                            System.out.println("Usage: add <expression>");
                            continue;
                        }
                        String expr = parts[1];
                        // Validate expression before adding
                        try {
                            new ExpressionEvaluator(expr).evaluate(0); // test with x=0
                            coordinateSystem.addFunction(new Free(expr));
                            System.out.println("Adding x ↦ " + expr);
                        } catch (RuntimeException e) {
                            System.out.println("Error: Invalid expression '" + expr + "': " + e.getMessage());
                        }
                    } else if (line.startsWith("remove ")) {
                        String[] parts = line.split(" ", 2);
                        if (parts.length < 2) {
                            System.out.println("Usage: remove <expression>");
                            continue;
                        }
                        String expr = parts[1];
                        System.out.println("Removing x ↦ " + expr);
                        for (Function f : new ArrayList<>(coordinateSystem.getGraphen())) {
                            if (f.toString().equals(expr)) coordinateSystem.removeFunction(f);
                        }

                    } else if (line.startsWith("set ")) {
                        // support: set stepX <value>
                        //          set stepY <value>
                        //          set stepsX <int>
                        //          set stepsY <int>
                        String[] parts = line.split("\\s+", 3); // limit=3 so the 3rd part contains the full expression
                        if (parts.length < 3) {
                            System.out.println("Usage: set <stepX|stepY|stepsX|stepsY> <value>");
                            continue;
                        }
                        String key = parts[1];
                        String val = parts[2];
                        try {
                            switch (key.toLowerCase()) {
                                case "stepx":
                                case "step_x":
                                    double sx = new ExpressionEvaluator(val).evaluate(1);
                                    coordinateSystem.setStepX(sx);
                                    coordinateSystem.repaint();
                                    System.out.println("set stepX -> " + sx);
                                    break;
                                case "stepy":
                                case "step_y":
                                    double sy = new ExpressionEvaluator(val).evaluate(1);
                                    coordinateSystem.setStepY(sy);
                                    coordinateSystem.repaint();
                                    System.out.println("set stepY -> " + sy);
                                    break;
                                case "stepsx":
                                    int stepsX = (int) Math.round(new ExpressionEvaluator(val).evaluate(1));
                                    coordinateSystem.setStepsX(stepsX);
                                    coordinateSystem.repaint();
                                    System.out.println("set stepsX -> " + stepsX);
                                    break;
                                case "stepsy":
                                    int stepsY = (int) Math.round(new ExpressionEvaluator(val).evaluate(1));
                                    coordinateSystem.setStepsY(stepsY);
                                    coordinateSystem.repaint();
                                    System.out.println("set stepsY -> " + stepsY);
                                    break;
                                default:
                                    System.out.println("Unknown set target: " + key + ". Use stepX/stepY/stepsX/stepsY");
                            }
                        } catch (NumberFormatException nfe) {
                            System.out.println("Invalid number/expression: " + val);
                        } catch (IllegalArgumentException iae) {
                            System.out.println("Could not evaluate expression: " + val + " (" + iae.getMessage() + ")");
                        }
                    } else if(line.startsWith("get ")) {
                        String[] parts = line.split(" ");
                        if(parts.length == 2) {
                            switch(parts[1].toLowerCase()) {
                                case "stepx":
                                    System.out.println(Util.formatLabel(coordinateSystem.getStepX()));
                                    break;
                                case "stepy":
                                    System.out.println(Util.formatLabel(coordinateSystem.getStepY()));
                                    break;
                                case "stepsx":
                                    System.out.println(coordinateSystem.getStepsX());
                                    break;
                                case "stepsy":
                                    System.out.println(coordinateSystem.getStepsY());
                                    break;
                                case "list":
                                    System.out.println(coordinateSystem.getGraphen());
                                    break;
                            }
                        }else{
                            System.out.println("Usage: get <expression>. Did you mean to use: set <expression> <value>?");
                        }
                    }else if(line.startsWith("clear")) {
                        if(coordinateSystem.getGraphen().isEmpty()) {
                            System.out.println("Nothing to clear");
                        }else{
                            coordinateSystem.setGraphen(new ArrayList<>());
                            coordinateSystem.repaint();
                            System.out.println("Clearing all graphs");
                        }

                    }else{
                        System.out.println("Unknown command: " + line);
                    }
                }
            }, "Console-Reader");
            consoleThread.setDaemon(true);
            consoleThread.start();
        });
    }
}



