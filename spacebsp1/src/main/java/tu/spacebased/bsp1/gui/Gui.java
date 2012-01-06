package tu.spacebased.bsp1.gui;

import javax.swing.*;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import org.mozartspaces.capi3.*;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsConstants.RequestTimeout;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;

import tu.spacebased.bsp1.components.Component;
import tu.spacebased.bsp1.components.Computer;
import tu.spacebased.bsp1.workers.Producer;

public class Gui implements ActionListener {
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
	public enum Components {
	    CPU, GPU, MAINBOARD, RAM
	}
	
	private static JFrame frame;
	private static JPanel workerPanel = new JPanel(new FlowLayout());
	private static JPanel notificationPanel = new JPanel(new FlowLayout());
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
    private static JTextField workerName = new JTextField("20");
    private static JTextField partsCount = new JTextField(" 0");
    private static JTextField failureNotification = new JTextField(30);
    
    private static ArrayList<Computer> selledComputerEntries = null;
    private static ArrayList<Computer> failedComputerEntries = null;
    
    private static DefaultListModel listModel = new DefaultListModel();
    private static DefaultListModel listModel1 = new DefaultListModel();
	// For Container in space
	static ContainerReference cRef = null;
//	static ContainerReference shittyRef = null;
//	static ContainerReference sellRef = null;
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
        String worker[] = {"CPU","GPU","MAINBOARD", "RAM"};
        String parts[] = {"CPU", "GPU", "MAINBOARD", "RAM"};
        listModel.addElement("None");
        listModel1.addElement("None");
        //String shipped[] = {"None"};
        //String failed[] = {"None"};
       
        workerList = new JList(worker);
    	partsTypeList = new JList(parts);
    	partsTypeList.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
    	shippedProducts = new JList(listModel);
    	failedProducts = new JList(listModel1);
        
        // Sizes
//        createWorkerButton.setBounds(10,20,80,20);
//        workerList.setBounds(100,10,400,25);
        
        // Components of the worker panel
        workerPanel.add(workerList, FlowLayout.LEFT);
        workerPanel.add(createWorkerButton, FlowLayout.LEFT);
        workerPanel.add(errorRate, FlowLayout.LEFT);
        workerPanel.add(workerName, FlowLayout.LEFT);
        
        failureNotification.setEditable(false);
        notificationPanel.add(failureNotification, FlowLayout.LEFT);
        
        // Components of the parts panel
        partsPanel.add(partsTypeList, FlowLayout.LEFT);
        partsPanel.add(partsCount);
        
        // Components of the statistics Panel
        statisticsPanel1.add(shippedProducts);
        statisticsPanel1.add(showDetailsShipped);
        statisticsPanel2.add(failedProducts);
        statisticsPanel2.add(showDetailsFailed);
        
        // actions
        createWorkerButton.addActionListener( new ActionListener() { 
	    	  public void actionPerformed( ActionEvent e ) { 
	    	      // (int quantity,int errorRate,Components component)
	    		  if (workerList.getSelectedValue() != null) {
		    		  if ((workerName.getText() != null) && (errorRate.getText() != null)) {
		    			  createProducer(Integer.parseInt(workerName.getText()), Double.parseDouble(errorRate.getText()), getComponent(workerList.getSelectedValue().toString()));
		    		  } else {
		    			  failureNotification.setText("NO ERROR RATE OR NO QUANTITY INSERTED");
		    		  }
	    		  } else {
	    			  failureNotification.setText("NO COMPONENT SELECTED");
	    		  }
	    	  } 
    	} );
        
        showDetailsShipped.addActionListener( new ActionListener() { 
        	public void actionPerformed( ActionEvent e ) { 
        		// modificate shippedProducts and showDetailsShipped
        		// UEBERGEBE DEM JTABLE DIE DATEN DES AUSGEWAEHLTN COMPUTERS
        		System.out.println("Shipped Product ausgewaehlt: " + shippedProducts.getSelectedValue() + " mit index " + shippedProducts.getSelectedIndex());
        		if ((shippedProducts.getSelectedValue() != null)) {
        			if (!shippedProducts.getSelectedValue().toString().equals("None")) {
        				new GuiTable(shippedProducts.getSelectedValue().toString(), selledComputerEntries.get(shippedProducts.getSelectedIndex()));
        			} else {
        				failureNotification.setText("Cant Show Info for None");
        			}
        		} else {
        			failureNotification.setText("NO COMPUTER SELECTED TO SHOW INFO");
        		}
        	}
    	} );
        
        showDetailsFailed.addActionListener( new ActionListener() { 
        	public void actionPerformed( ActionEvent e ) { 
        		// modificate failedProducts and showDetailsFailed     
        		if ((failedProducts.getSelectedValue() != null)) {
        			if (!failedProducts.getSelectedValue().toString().equals("None")) {
        				new GuiTable(failedProducts.getSelectedValue().toString(), failedComputerEntries.get(failedProducts.getSelectedIndex()));
        			} else {
        				failureNotification.setText("Cant Show Info for None");
        			}
        		} else {
        			failureNotification.setText("NO COMPUTER SELECTED TO SHOW INFO");
        		}
        	}
    	} );

        
        // frame.getContentPane().add(createWorkerButton);
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
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 4;
        mainPanel.add(notificationPanel, c);
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
        
        try {
			cRef = capi.lookupContainer(containerName, uri, MzsConstants.RequestTimeout.INFINITE, null);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
//        try {
//			shittyRef = capi.lookupContainer(shittyContainerName, uri, MzsConstants.RequestTimeout.INFINITE, null);
//		} catch (MzsCoreException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//        
//        try {
//			sellRef = capi.lookupContainer(sellContainerName, uri, MzsConstants.RequestTimeout.INFINITE, null);
//		} catch (MzsCoreException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
        
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
        
 
        // TODO: I think update and revalidation should move into an own thread?
        while(true) {
        	//System.out.println("TRYING WHILE");
        	
        	//Parts list
        	
        	ArrayList<Component>readEntries = null;
        	
        	//This is quite ugly, but i don't care right now
    	
    		try {
    			int part = partsTypeList.getSelectedIndex();
    		
	        	switch(part) {
	        	case (0):
	        		try {
	    				readEntries = capi.read(cRef, LabelCoordinator.newSelector("CPU", MzsConstants.Selecting.COUNT_MAX), 200, null);
	    			} catch (MzsCoreException e) {
	    				 System.out.println("transaction timeout. retry.");
	    			}
	        	break;
	        	case (1):
	        		try {
	    				readEntries = capi.read(cRef, LabelCoordinator.newSelector("GPU", MzsConstants.Selecting.COUNT_ALL), 200, null);
	    			} catch (MzsCoreException e) {
	    				 System.out.println("transaction timeout. retry.");
	                     continue;
	    			}
	        	break;
	        	case (2):
	        		try {
	    				readEntries = capi.read(cRef, LabelCoordinator.newSelector("MAINBOARD", MzsConstants.Selecting.COUNT_ALL), 200, null);
	    			} catch (MzsCoreException e) {
	    				 System.out.println("transaction timeout. retry.");
	                     continue;
	    			}
	        	break;
	        	case (3):
	        		try {
	    				readEntries = capi.read(cRef, LabelCoordinator.newSelector("RAM", MzsConstants.Selecting.COUNT_ALL), 200, null);
	    			} catch (MzsCoreException e) {
	    				 System.out.println("transaction timeout. retry.");
	                     continue;
	    			}
	        	}
	        	Integer count = readEntries.size();
	        		        	
	        	partsCount.setText(count.toString());
	        	partsCount.repaint();
    		} catch (NullPointerException n) {
    			continue;
    		}
    		System.out.println("TRYING TO GET COMPUTERS");
        	//Computer lists

        	ArrayList<Computer>compEntries = null;
        	
        	//This is quite ugly, but i don't care right now
			try {
				compEntries = capi.read(cRef, LabelCoordinator.newSelector("sell",MzsConstants.Selecting.COUNT_ALL), RequestTimeout.TRY_ONCE, null);

			} catch (MzsCoreException e) {
				 System.out.println("transaction timeout. retry." + e.toString());
                 
			}
			
			System.out.println("DEBUG: Trying to find computers for sellRef: ");
			
			if ((!compEntries.isEmpty()) && ((compEntries.size() != listModel.size()) || (listModel.get(0).equals("None")))) {
				
				System.out.println("DEBUG: Found computers for sellRef, proceeding with iterator: " + compEntries.size());
				
				Iterator<Computer> it = compEntries.iterator();
				
				listModel.clear();
				
				Integer z = 1;
				while (it.hasNext()) {
					Computer comp = (Computer) it.next();
					listModel.addElement(z.toString());
					z++;
				}
				if (listModel != null) {
					if (!listModel.isEmpty()) {
						//shippedProducts = new JList(listModel);
						//shippedProducts.repaint();
						//statisticsPanel1.repaint();
						selledComputerEntries = compEntries;
					}
				}
			} else {
				System.out.println("DEBUG: ComputerEntries not needing to update, retrying ... ");
			}
	
        	//This is quite ugly, but i don't care right now
			try {
				compEntries = capi.read(cRef, LabelCoordinator.newSelector("shitty",MzsConstants.Selecting.COUNT_ALL), RequestTimeout.TRY_ONCE, null);

			} catch (MzsCoreException e) {
				 System.out.println("transaction timeout. retry." + e.toString());
                 
			}
			System.out.println("DEBUG: Trying to find computers for shittyRef: ");
			
			if ((!compEntries.isEmpty()) && ((compEntries.size() != listModel1.size()) || (listModel1.get(0).equals("None")))) {
				
				System.out.println("DEBUG: Found computers for sellRef, proceeding with iterator: " + compEntries.size());
				
				Iterator<Computer> it = compEntries.iterator();
			
				listModel1.clear();
				
				Integer z = 1;
				while (it.hasNext()) {
					Computer comp = (Computer) it.next();
					listModel1.addElement(z.toString());
					z++;
				}
				if (listModel1 != null) {
					if (!listModel1.isEmpty()) {
						//failedProducts = new JList(listModel1);
						//failedProducts.repaint();
						//statisticsPanel2.repaint();
						failedComputerEntries = compEntries;
					}
				}
			} else {
				System.out.println("DEBUG: ComputerEntries no need to update, retrying ... ");
			}
			
	        try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
    
    public static Producer.Components getComponent(String comp) {
    	if (comp != null) {
    		return Producer.Components.valueOf(comp);
    	} else {
    		return null;
    	}
    }

    //Create Producer
    public static void createProducer(int quantity,double errorRate,tu.spacebased.bsp1.workers.Producer.Components components) {
    	
    	String makerID;

    	UUID uid= UUID.randomUUID();
    	makerID = uid.toString();
    	// TODO: remove Producer prod = 
    	new Producer(quantity, makerID, errorRate, components);
		
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
