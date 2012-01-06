package tu.spacebased.bsp1;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.ContainerNameNotAvailableException;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.core.*;

/**
 * Hello world!
 *
 */
public class App
{
	private static Capi capi;
	private static URI uri = null;
	
    public static void main( String[] args )
    {
    	
    	//TODO: check if transactions can be removed again
    	/*TODO: this structure of an app initializing all the containers
    	 * is quite ugly, but at least it works. */
    	
    	Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
            	try {
        			capi.clearSpace(uri);
        		} catch (MzsCoreException e) {
        			 System.out.println("this should never happen :S");
        		}
            }
        });
            
		ContainerReference cRef = null;
		ContainerReference shittyRef = null;
		ContainerReference sellRef = null;
		
		try {
			uri = new URI("xvsm://localhost:9877");
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        String containerName = "store";
        String shittyContainerName = "shitty";
        String sellContainerName = "sell";
        
        MzsCore core = DefaultMzsCore.newInstance();
        capi = new Capi(core);
        
        TransactionReference transaction = null;
		try {
			transaction = capi.createTransaction(MzsConstants.RequestTimeout.INFINITE, uri);
		} catch (MzsCoreException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

	    try {
			cRef = capi.createContainer(
				containerName,
				uri,
				MzsConstants.Container.UNBOUNDED,
				null,
				Arrays.asList(new KeyCoordinator(), new LabelCoordinator(), new FifoCoordinator()),
				null); //transaction could be a real TransactionRefernce if
						// we need commit-style
		} catch (ContainerNameNotAvailableException e) {
			try {
				cRef = capi.lookupContainer(containerName, uri, MzsConstants.RequestTimeout.INFINITE, null);
				capi.destroyContainer(cRef, null);
				cRef = capi.createContainer(
						containerName,
						uri,
						MzsConstants.Container.UNBOUNDED,
						null,
						Arrays.asList(new KeyCoordinator(), new LabelCoordinator(), new FifoCoordinator()),
						null); //transaction could be a real TransactionRefernce if
								// we need commit-style
			} catch (MzsCoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        try {
        	shittyRef = capi.createContainer(
				shittyContainerName,
				uri,
				MzsConstants.Container.UNBOUNDED,
				null,
				Arrays.asList(new FifoCoordinator(), new AnyCoordinator()),
				null); //transaction could be a real TransactionRefernce if
						// we need commit-style
   		} catch (ContainerNameNotAvailableException e) {
   			try {
   				shittyRef = capi.lookupContainer(shittyContainerName, uri, MzsConstants.RequestTimeout.INFINITE, null);
   				capi.destroyContainer(shittyRef, null);
   				shittyRef = capi.createContainer(
   						shittyContainerName,
   						uri,
   						MzsConstants.Container.UNBOUNDED,
   						null,
   						Arrays.asList(new FifoCoordinator(), new AnyCoordinator()),
   						null); //transaction could be a real TransactionRefernce if
   								// we need commit-style
   			} catch (MzsCoreException e1) {
   				// TODO Auto-generated catch block
   				e1.printStackTrace();
   			}
   		} catch (MzsCoreException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
        
        try {
        	sellRef = capi.createContainer(
				sellContainerName,
				uri,
				MzsConstants.Container.UNBOUNDED,
				null,
				Arrays.asList(new KeyCoordinator(), new LabelCoordinator(), new FifoCoordinator(), new AnyCoordinator()),
				null); //transaction could be a real TransactionRefernce if
						// we need commit-style
   		} catch (ContainerNameNotAvailableException e) {
   			try {
   				sellRef = capi.lookupContainer(sellContainerName, uri, MzsConstants.RequestTimeout.INFINITE, null);
   				capi.destroyContainer(sellRef, null);
   				sellRef = capi.createContainer(
   						sellContainerName,
   						uri,
   						MzsConstants.Container.UNBOUNDED,
   						null,
   						Arrays.asList(new FifoCoordinator(), new AnyCoordinator()),
   						null); //transaction could be a real TransactionRefernce if
   								// we need commit-style
   			} catch (MzsCoreException e1) {
   				// TODO Auto-generated catch block
   				e1.printStackTrace();
   			}
   		} catch (MzsCoreException e) {
   			// TODO Auto-generated catch block
   			e.printStackTrace();
   		}
        
        Integer i = 0;

        Entry entry = new Entry(i, KeyCoordinator.newCoordinationData("uniqueId"));
        
    	try {
			capi.write(cRef, MzsConstants.RequestTimeout.INFINITE, transaction, entry);
			capi.commitTransaction(transaction);
		} catch (MzsCoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
