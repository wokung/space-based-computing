package tu.spacebased.bsp1.gui;

import javax.swing.*;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import org.mozartspaces.capi3.*;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.CapiUtil;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;

import tu.spacebased.bsp1.components.Component;
import tu.spacebased.bsp1.components.Computer;
import tu.spacebased.bsp1.workers.Producer;
import tu.spacebased.bsp1.workers.Producer.Components;

public class Gui{
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
	
	private static JFrame frame;
	private static JPanel workerPanel = new JPanel(new FlowLayout());
	private static JPanel partsPanel = new JPanel(new FlowLayout());
	private static JPanel statisticsPanel1 = new JPanel(new FlowLayout());
	private static JPanel statisticsPanel2 = new JPanel(new FlowLayout());
	private static JButton createWorkerButton = new JButton("Create Worker");
	private static JButton showDetailsShipped = new JButton("show Details");
	private static JButton showDetailsFailed = new JButton("show Details");
	private static JList workerList;
	private static JList partsTypeList;
	private static JList shippedProducts;
	private static JList failedProducts;
    private static JTextField errorRate = new JTextField("0.1");
    private static JTextField workerName = new JTextField("Jesus");
    private static JTextField partsCount = new JTextField("123");
    
	// For Container in space
	static ContainerReference cRef = null;
	static ContainerReference shittyRef = null;
	static ContainerReference sellRef = null;
	static Capi capi = null;
    
    private static void createAndShowGUI() {
    	
        // Create and set up the window.
        frame = new JFrame("Mighty Computer Factory Control Panel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Panels
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
//        workerPanel.setBounds(10, 50, 200, 500);
        
        partsCount.setEditable(false);
        
        // TODO: these are dummies - add actual 'ListModels' to the Lists 
        String worker[] = {"Juan","Pablo","Miguel"};
        String parts[] = {"CPU", "GPU", "MAINBOARD", "RAM"};
        String shipped[] = {};
        String failed[] = {};
       
        workerList = new JList(worker);
    	partsTypeList = new JList(parts);
    	shippedProducts = new JList(shipped);
    	failedProducts = new JList(failed);
        
        // Sizes
//        createWorkerButton.setBounds(10,20,80,20);
//        workerList.setBounds(100,10,400,25);
        
        // Components of the worker panel
        workerPanel.add(workerList, FlowLayout.LEFT);
        workerPanel.add(createWorkerButton, FlowLayout.LEFT);
        workerPanel.add(errorRate, FlowLayout.LEFT);
        workerPanel.add(workerName, FlowLayout.LEFT);
        
        // Components of the parts panel
        partsPanel.add(partsTypeList, FlowLayout.LEFT);
        partsPanel.add(partsCount);
        
        // Components of the statistics Panel
        statisticsPanel1.add(shippedProducts);
        statisticsPanel1.add(showDetailsShipped);
        statisticsPanel2.add(failedProducts);
        statisticsPanel2.add(showDetailsFailed);

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
        c.gridy = 1;        // TODO: I think we should call a 'UpdateFields()'-Method here, or such

        mainPanel.add(partsPanel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        mainPanel.add(new JLabel("Statistics:"), c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        mainPanel.add(statisticsPanel1, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 3;
        mainPanel.add(statisticsPanel2, c);
        frame.getContentPane().add(mainPanel);
//        frame.getContentPane().add(partsPanel);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
        
        frame.setBounds(100, 100, 600, 480);
    }

    public static void main(String[] args) {
    	
    	MzsCore core = DefaultMzsCore.newInstance();
        capi = new Capi(core);
    	
    	URI uri = null;
 		try {
 			uri = new URI("xvsm://localhost:9877");
 		} catch (URISyntaxException e1) {
 			// TODO Auto-generated catch block
 			e1.printStackTrace();
 		}
        String containerName = "store";
        String shittyContainerName = "shitty";
        String sellContainerName = "sell";
        
        try {
			cRef = CapiUtil.lookupOrCreateContainer(
					containerName,
					uri,
					Arrays.asList(new FifoCoordinator()),
					null, capi);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        try {
			shittyRef = CapiUtil.lookupOrCreateContainer(
					shittyContainerName,
					uri,
					Arrays.asList(new FifoCoordinator()),
					null, capi);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        try {
			sellRef = CapiUtil.lookupOrCreateContainer(
					sellContainerName,
					uri,
					Arrays.asList(new FifoCoordinator()),
					null, capi);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
 		
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
        
 
        // TODO: I think update and revalidation should move into an own thread?
        while(true) {
        	
        	//Parts list
        	
        	ArrayList<Component>readEntries;
        	
        	//This is quite ugly, but i don't care right now
        	int part[] = partsTypeList.getSelectedIndices();
        	Integer count = 0;
        	for (int i = 0; i < part.length; i++) {
	        	switch(i) {
	        	case (1):
	        		try {
	    				readEntries = capi.read(cRef, LabelCoordinator.newSelector("CPU", MzsConstants.Selecting.COUNT_ALL), RequestTimeout.INFINITE, null);
	    			} catch (MzsCoreException e) {
	    				 System.out.println("transaction timeout. retry.");
	                     continue;
	    			}
	        		count += readEntries.size();
	        	case (2):
	        		try {
	    				readEntries = capi.read(cRef, LabelCoordinator.newSelector("GPU", MzsConstants.Selecting.COUNT_ALL), RequestTimeout.INFINITE, null);
	    			} catch (MzsCoreException e) {
	    				 System.out.println("transaction timeout. retry.");
	                     continue;
	    			}
	        		count += readEntries.size();
	        	case (3):
	        		try {
	    				readEntries = capi.read(cRef, LabelCoordinator.newSelector("RAM", MzsConstants.Selecting.COUNT_ALL), RequestTimeout.INFINITE, null);
	    			} catch (MzsCoreException e) {
	    				 System.out.println("transaction timeout. retry.");
	                     continue;
	    			}
	        		count += readEntries.size();
	        	case (4):
	        		try {
	    				readEntries = capi.read(cRef, LabelCoordinator.newSelector("MAINBOARD", MzsConstants.Selecting.COUNT_ALL), RequestTimeout.INFINITE, null);
	    			} catch (MzsCoreException e) {
	    				 System.out.println("transaction timeout. retry.");
	                     continue;
	    			}
	        		count += readEntries.size();
	        	}
        	}
        	
        	partsCount.setText(count.toString());
        	partsCount.repaint();
        	
        	//Computer lists
        	ArrayList<Computer>compEntries;
        	
        	//This is quite ugly, but i don't care right now
			try {
				readEntries = capi.read(sellRef, AnyCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL), RequestTimeout.INFINITE, null);
			} catch (MzsCoreException e) {
				 System.out.println("transaction timeout. retry.");
                 continue;
			}
			
			//TODO: this is where i left
			//shippedProducts = ;
        	
        	//This is quite ugly, but i don't care right now
			try {
				readEntries = capi.read(shittyRef, AnyCoordinator.newSelector(MzsConstants.Selecting.COUNT_ALL), RequestTimeout.INFINITE, null);
			} catch (MzsCoreException e) {
				 System.out.println("transaction timeout. retry.");
                 continue;
			}
			
			//TODO: this is where i left
			//shippedProducts =;

	        
	        try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
//Create Producer

    public void createProducer(int quantity,int errorRate,Components component) {
    	
    	int makerID;
    
		ArrayList<Integer>readId = null;
		
		try {
			readId = capi.take(cRef, KeyCoordinator.newSelector("uniqueWorkerId"), RequestTimeout.INFINITE, null);
		} catch (MzsCoreException e) {
			 System.out.println("this should never happen :S");
		}
		
		Entry id = new Entry(readId.get(0)+1, KeyCoordinator.newCoordinationData("uniqueId"));
		
		try {
			capi.write(cRef, RequestTimeout.INFINITE, null, id);
		} catch (MzsCoreException e) {
			 System.out.println("this should never happen :S");
		}
		
		makerID = readId.get(0);
		
		Producer prod = new Producer(quantity, makerID, errorRate, component);
		
		//TODO: Is this then already a thread?
		prod.run();
    }
}
