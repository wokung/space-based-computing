package tu.spacebased.bsp1.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import tu.spacebased.bsp1.components.Computer;

public class GuiTable implements ActionListener {
     
     Container mainContainer;
     JFrame frame;
     
     JButton schliessen;
     JTable tabelle;
     DefaultTableModel tabellenmodell;

    
     
    // String spalten[] = { "ComputerNr.", "ErstellerID", "TesterID", "LogistikerID", "CPUID", "CPUCreatorID", "MainboardID", "MainboardCreatorID", "GPUID","GPUCREATORID", "RAMIDs","RAMCREATORID", "Defekt"};
     

     
     public GuiTable(String chosenComputer, Computer computer) {
          frame = new JFrame();
          mainContainer = frame.getContentPane();
          frame.setLayout(new BorderLayout());
          
          frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
          
          schliessen = new JButton("close");
          schliessen.addActionListener(this);

          mainContainer.add(schliessen, BorderLayout.NORTH);
          
          // Spalten anlegen // TODO RAM BESSER ANZEIEGN
          Vector<String> spalten = new Vector<String>();
          spalten.add("ComputerNr.");
          spalten.add("ErstellerID");
          spalten.add("TesterID");
          spalten.add("LogistikerID");
          spalten.add("CPUID");
          spalten.add("CPUCreatorID");
          spalten.add("MainboardID");
          spalten.add("MainboardCreatorID");
          spalten.add("GPUID");
          spalten.add("GPUCREATORID");
          spalten.add("RAMIDs");
          spalten.add("RAMCREATORID");
          spalten.add("Defekt");
          
          
       // Datenmodelle anlegen // TODO: ram wird nicht gut angezeigt
          Vector<Vector<String>> data = new Vector<Vector<String>>();
			Vector<String> rowA = new Vector<String>();
			rowA.add(  chosenComputer );
			rowA.add(  String.valueOf(computer.getMakerID()) );
			rowA.add(  String.valueOf(computer.getTesterID()) );
			rowA.add(  String.valueOf(computer.getLogisticianID()) );
			rowA.add(  String.valueOf(computer.getCpu().getID()) );
			rowA.add( String.valueOf(computer.getCpu().getMakerID()) );
			rowA.add(  String.valueOf(computer.getMainboard().getID()) );
			rowA.add(  String.valueOf(computer.getMainboard().getMakerID()) );
			if (computer.getGpu() != null) {
				rowA.add( String.valueOf(computer.getGpu().getID()) );
				rowA.add(  String.valueOf(computer.getGpu().getMakerID()));
			} else {
				rowA.add("none" );
				rowA.add(  "none");
			}
			rowA.add( String.valueOf(computer.getRam().get(0).getID()) );
			rowA.add( String.valueOf(computer.getRam().get(0).getMakerID()) );
			if (computer.isDefect()) {
				rowA.add("true");
			} else {
				rowA.add("false");
			}
			
			
			data.add( rowA );
          	
          /**
			String[][] data = new String[][]{
     				{chosenComputer, String.valueOf(computer.getMakerID()), String.valueOf(computer.getTesterID()), String.valueOf(computer.getLogisticianID()),
     					String.valueOf(computer.getCpu().getID()), String.valueOf(computer.getCpu().getMakerID()), String.valueOf(computer.getMainboard().getID()), 
     					String.valueOf(computer.getMainboard().getMakerID()), String.valueOf(computer.getGpu().getID()), String.valueOf(computer.getGpu().getMakerID()),
     					String.valueOf(computer.getCpu().getID()), String.valueOf(computer.getRam().get(0).getID()), String.valueOf(computer.getRam().get(0).getMakerID()), 
     					String.valueOf(computer.isDefect())}, {}
     	  };
          */
          //tabellenmodell = new DefaultTableModel(data, spalten);
          //tabelle = new JTable(tabellenmodell);
		  tabelle = new JTable(data, spalten);

          // Um die Spalten der Tabelle anzuzeigen muss die Tabelle
          // ueber eine ScrollPane hinzugefuegt werden
          mainContainer.add(new JScrollPane(tabelle));
          
          frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
          frame.pack();
          frame.setTitle("JTable Beispiel");
          frame.setVisible(true);
     }
     
     public void actionPerformed(ActionEvent arg0) {
    	 frame.dispose();
     }
}