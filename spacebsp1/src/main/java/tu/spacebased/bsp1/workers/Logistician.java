package tu.spacebased.bsp1.workers;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.CapiUtil;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.MzsConstants.RequestTimeout;

import tu.spacebased.bsp1.components.Computer;

/**
 * Es soll in jeder Abteilung (Produktion, Montage, Test, Logistik) festgehalten werden, welche(r) Mitarbeiter/-in die Arbeiten ausgef�hrt hat.
 * Jede(r) Mitarbeiter/-in wird dabei durch eine eigene ID identifiziert. Alle Beteiligten k�nnen dynamisch hinzugef�gt und weggenommen werden. 
 * Jede(r) Mitarbeiter/-in stellt f�r sich ein kleines unabh�ngiges Programm dar (mit eigener main-Methode) mit der Ausnahme von Produzenten/-innen, 
 * die von anderen Programmen erzeugt und verwendet werden k�nnen.
 * 
 * In der Logistik werden die vollst�ndig getesteten Computer von Logistik-Mitarbeitern/-innen ausgeliefert (= als fertig markiert). 
 * Es werden nur funktionst�chtige Computer ausgeliefert. Defekte Computer werden in einem eigenen Lager gelagert.
 * @author Kung
 */
public class Logistician {
	
	// For Container in space
	private static Capi capi;
	private static ContainerReference cRef = null;
	private static ContainerReference shittyRef = null;
	private static ContainerReference sellRef = null;
    private static String containerName = "store";
    private static String shittyContainerName = "shitty";
    private static String sellContainerName = "sell";
	
	public void main(String [] args)
	{
		MzsCore core = DefaultMzsCore.newInstance();
	    Capi capi = new Capi(core);
	    
	    URI uri = null;
		try {
			uri = new URI("xvsm://localhost:9877");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
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
					Arrays.asList(new AnyCoordinator()),
					null, capi);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			sellRef = CapiUtil.lookupOrCreateContainer(
					sellContainerName,
					uri,
					Arrays.asList(new AnyCoordinator()),
					null, capi);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ArrayList<Computer> computerList = null;
	
		for (;;) {
			try {
				computerList = capi.take(cRef, LabelCoordinator.newSelector("testedComputer",1), RequestTimeout.INFINITE, null);
			} catch (MzsCoreException e) {
				 System.out.println("this should never happen :S");
			}
			Computer computer = computerList.get(0);
			
			//TODO: keep track of Logistician that processed it;
			
			if (computer.isDefect()) {
				Entry compEntry = new Entry(computer);
				
				try {
					capi.write(compEntry, shittyRef, RequestTimeout.INFINITE, null);
				} catch (MzsCoreException e) {
					 System.out.println("this should never happen :S");
				}
			} else {
				Entry compEntry = new Entry(computer);
				
				try {
					capi.write(compEntry, sellRef, RequestTimeout.INFINITE, null);
				} catch (MzsCoreException e) {
					 System.out.println("this should never happen :S");
				}
			}
		}
	}
}
