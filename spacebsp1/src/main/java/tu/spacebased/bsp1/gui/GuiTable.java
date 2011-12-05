package tu.spacebased.bsp1.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

     // Spalten anlegen
     String spalten[] = { "ComputerNr.", "ErstellerID", "TesterID", "LogistikerID", "CPUID", "CPUCreatorID", "MainboardID", "MainboardCreatorID", "GPUID","GPUCREATORID", "RAMIDs","RAMCREATORID", "Defekt"};
     

     
     public GuiTable(String chosenComputer, Computer computer) {
          frame = new JFrame();
          mainContainer = frame.getContentPane();
          frame.setLayout(new BorderLayout());

          schliessen = new JButton("close");
          schliessen.addActionListener(this);

          mainContainer.add(schliessen, BorderLayout.NORTH);
          
          	// Datenmodelle anlegen // TODO: ram wird nicht gut angezeigt
          String[][] data = new String[][]{
     				{chosenComputer, String.valueOf(computer.getMakerID()), String.valueOf(computer.getTesterID()), String.valueOf(computer.getLogisticianID()),
     					String.valueOf(computer.getCpu().getID()), String.valueOf(computer.getCpu().getMakerID()), String.valueOf(computer.getMainboard().getID()), 
     					String.valueOf(computer.getMainboard().getMakerID()), String.valueOf(computer.getGpu().getID()), String.valueOf(computer.getGpu().getMakerID()),
     					String.valueOf(computer.getCpu().getID()), String.valueOf(computer.getRam().get(0).getID()), String.valueOf(computer.getRam().get(0).getMakerID()), 
     					String.valueOf(computer.isDefect())}
     	  };
          
          tabellenmodell = new DefaultTableModel(data, spalten);
          tabelle = new JTable(tabellenmodell);

          // Um die Spalten der Tabelle anzuzeigen muss die Tabelle
          // ueber eine ScrollPane hinzugefuegt werden
          mainContainer.add(new JScrollPane(tabelle));
          
          frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
          frame.pack();
          frame.setTitle("JTable Beispiel");
          frame.setVisible(true);
     }

     public void actionPerformed(ActionEvent arg0) {
          System.exit(0);
     }
}