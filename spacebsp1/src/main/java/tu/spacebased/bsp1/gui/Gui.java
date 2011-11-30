package tu.spacebased.bsp1.gui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.*;

public class Gui{
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Create and set up the window.
        JFrame frame = new JFrame("Mighty Computer Factory Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panels
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
       
        JPanel workerPanel = new JPanel(new FlowLayout());
        JPanel partsPanel = new JPanel(new FlowLayout());
        JPanel statisticsPanel = new JPanel(new FlowLayout());
        
//        workerPanel.setBounds(10, 50, 200, 500);
        
        JButton createWorkerButton = new JButton("Create Worker");
        JButton showDetailsShipped = new JButton("show Details");
        JButton showDetailsFailed = new JButton("show Details");
        
        JTextField partsCount = new JTextField("123");
        partsCount.setEditable(false);
        
        // TODO: these are dummies - add actual 'ListModels' to the Lists 
        String worker[] = {"Juan","Pablo","Miguel"};
        String parts[] = {"Mainboard", "etc.", "etc."};
        String shipped[] = {"34", "453", "512"};
        String failed[] = {"13", "23", "42", "75", "92", "150"};
        
        JList workerList = new JList(worker);
        JList partsTypeList = new JList(parts);
        JList shippedProducts = new JList(shipped);
        JList failedProducts = new JList(failed);
        
        // Sizes
//        createWorkerButton.setBounds(10,20,80,20);
//        workerList.setBounds(100,10,400,25);
        
        // Components of the worker panel
        workerPanel.add(workerList, FlowLayout.LEFT);
        workerPanel.add(createWorkerButton, FlowLayout.LEFT);
        
        // Components of the parts panel
        partsPanel.add(partsTypeList, FlowLayout.LEFT);
        partsPanel.add(partsCount);
        
        // Components of the statistics Panel
        statisticsPanel.add(shippedProducts);
        statisticsPanel.add(showDetailsShipped);
        statisticsPanel.add(failedProducts);
        statisticsPanel.add(showDetailsFailed);

//        frame.getContentPane().add(createWorkerButton);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(new JLabel("Workers:"), c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        mainPanel.add(new JLabel("Parts:"), c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(workerPanel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;
        mainPanel.add(partsPanel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        mainPanel.add(new JLabel("Statistics:"), c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        mainPanel.add(statisticsPanel, c);
        frame.getContentPane().add(mainPanel);
//        frame.getContentPane().add(partsPanel);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
        
        // TODO: I think we should call a 'UpdateFields()'-Method here, or such
        
        frame.setBounds(100, 100, 600, 480);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
