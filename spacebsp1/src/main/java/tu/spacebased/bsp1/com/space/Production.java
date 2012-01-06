package tu.spacebased.bsp1.com.space;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.core.Capi;
import org.mozartspaces.core.ContainerReference;
import org.mozartspaces.core.DefaultMzsCore;
import org.mozartspaces.core.Entry;
import org.mozartspaces.core.MzsConstants;
import org.mozartspaces.core.MzsCore;
import org.mozartspaces.core.MzsCoreException;
import org.mozartspaces.core.TransactionReference;
import org.mozartspaces.core.MzsConstants.RequestTimeout;

import tu.spacebased.bsp1.components.Component;

public class Production {
	
	private static Capi capi;
	private static ContainerReference cRef = null;
    private static String containerName = "store";
    private static TransactionReference transaction = null;
    static URI uri = null;
	
	public static void setUp() {
		MzsCore core = DefaultMzsCore.newInstance();
	    capi = new Capi(core);
	    
		try {
			uri = new URI(Settings.uriString);
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			transaction = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, uri);
		} catch (MzsCoreException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			cRef = capi.lookupContainer(containerName, uri, MzsConstants.RequestTimeout.INFINITE, transaction);
			capi.commitTransaction(transaction);
		} catch (MzsCoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	public static void pushComponent(Component component) {
		Entry entry = new Entry(component, LabelCoordinator.newCoordinationData(component.toString()));
		
		try {
			transaction = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, uri);
		} catch (MzsCoreException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			capi.write(cRef, MzsConstants.RequestTimeout.TRY_ONCE, transaction, entry);
			capi.commitTransaction(transaction);
		} catch (MzsCoreException e) {
			 System.out.println("this should never happen :S");
		}
	}
	
	public static int getNewPartID() {
		ArrayList<Integer>readEntries = null;
		
		try {
			readEntries = capi.take(cRef, KeyCoordinator.newSelector("uniqueId"), 1000, null);
		} catch (MzsCoreException e) {
			 System.out.println("this should never happen :S");
		}
		
		Integer id = readEntries.get(0);
		
		Entry postId = new Entry(id+1, KeyCoordinator.newCoordinationData("uniqueId"));
		
		try {
			transaction = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, uri);
		} catch (MzsCoreException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		try {
			capi.write(cRef, RequestTimeout.INFINITE, transaction, postId);
			capi.commitTransaction(transaction);
		} catch (MzsCoreException e) {
			 System.out.println("this should never happen :S");
		}
		
		return id;
	}
}
