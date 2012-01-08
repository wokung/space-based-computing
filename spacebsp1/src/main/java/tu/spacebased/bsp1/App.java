package tu.spacebased.bsp1;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mozartspaces.capi3.AnyCoordinator;
import org.mozartspaces.capi3.ContainerNameNotAvailableException;
import org.mozartspaces.capi3.FifoCoordinator;
import org.mozartspaces.capi3.KeyCoordinator;
import org.mozartspaces.capi3.LabelCoordinator;
import org.mozartspaces.core.*;
import org.mozartspaces.core.MzsConstants.Container;
import org.mozartspaces.core.MzsConstants.RequestTimeout;

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
        /*
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
    	
    	*/
    }
    

	public static ContainerReference getCpuContainer(final URI uri, final Capi capi) throws MzsCoreException{
		ContainerReference cref;
		try {
			cref = capi.lookupContainer("CpuContainer", uri, RequestTimeout.DEFAULT, null);
		} catch (MzsCoreException e) {
			List<AnyCoordinator> coordinator = new ArrayList<AnyCoordinator>();
			coordinator.add(new AnyCoordinator());
			cref = capi.createContainer("CpuContainer", uri, Container.UNBOUNDED, coordinator, null, null);
		}
		return cref;
	}

	public static ContainerReference getGpuContainer(final URI uri, final Capi capi) throws MzsCoreException{
		ContainerReference cref;
		try {
			cref = capi.lookupContainer("GpuContainer", uri, RequestTimeout.DEFAULT, null);
		} catch (MzsCoreException e) {
			List<AnyCoordinator> coordinator = new ArrayList<AnyCoordinator>();
			coordinator.add(new AnyCoordinator());
			cref = capi.createContainer("GpuContainer", uri, Container.UNBOUNDED, coordinator, null, null);
		}
		return cref;
	}

	public static ContainerReference getRamContainer(final URI uri, final Capi capi) throws MzsCoreException{
		ContainerReference cref;
		try {
			cref = capi.lookupContainer("RamContainer", uri, RequestTimeout.DEFAULT, null);
		} catch (MzsCoreException e) {
			List<AnyCoordinator> coordinator = new ArrayList<AnyCoordinator>();
			coordinator.add(new AnyCoordinator());
			cref = capi.createContainer("RamContainer", uri, Container.UNBOUNDED, coordinator, null, null);
		}
		return cref;
	}

	public static ContainerReference getMainboardContainer(final URI uri, final Capi capi) throws MzsCoreException{
		ContainerReference cref;
		try {
			cref = capi.lookupContainer("MainboardContainer", uri, RequestTimeout.DEFAULT, null);
		} catch (MzsCoreException e) {
			List<FifoCoordinator> coordinator = new ArrayList<FifoCoordinator>();
			coordinator.add(new FifoCoordinator());
			cref = capi.createContainer("MainboardContainer", uri, Container.UNBOUNDED, coordinator, null, null);
		}
		return cref;
	}

	public static ContainerReference getPcContainer(final URI uri, final Capi capi) throws MzsCoreException{
		ContainerReference cref;
		try {
			cref = capi.lookupContainer("PcContainer", uri, RequestTimeout.DEFAULT, null);
		} catch (MzsCoreException e) {
			List<LabelCoordinator> coordinator = new ArrayList<LabelCoordinator>();
			coordinator.add(new LabelCoordinator());
			cref = capi.createContainer("PcContainer", uri, Container.UNBOUNDED, coordinator, null, null);
		}
		return cref;
	}

	public static ContainerReference getPcDefectContainer(final URI uri, final Capi capi) throws MzsCoreException{
		ContainerReference cref;
		try {
			cref = capi.lookupContainer("PcDefectContainer", uri, RequestTimeout.DEFAULT, null);
		} catch (MzsCoreException e) {
			List<AnyCoordinator> coordinator = new ArrayList<AnyCoordinator>();
			coordinator.add(new AnyCoordinator());
			cref = capi.createContainer("PcDefectContainer", uri, Container.UNBOUNDED, coordinator, null, null);
		}
		return cref;
	}
	
	// for notifications
	public static ContainerReference getEventContainer(final URI uri, final Capi capi) throws MzsCoreException{
		ContainerReference cref;
		try {
			cref = capi.lookupContainer("SpaceEvent", uri, RequestTimeout.DEFAULT, null);
		} catch (MzsCoreException e) {
			List<FifoCoordinator> coordinator = new ArrayList<FifoCoordinator>();
			coordinator.add(new FifoCoordinator());
			cref = capi.createContainer("SpaceEvent", uri, Container.UNBOUNDED, coordinator, null, null);
		}
		return cref;
	}

	public static ContainerReference getStorageContainer(final URI uri, final Capi capi) throws MzsCoreException{
		ContainerReference cref;
		try {
			cref = capi.lookupContainer("Storage", uri, RequestTimeout.DEFAULT, null);
		} catch (MzsCoreException e) {
			List<AnyCoordinator> coordinator = new ArrayList<AnyCoordinator>();
			coordinator.add(new AnyCoordinator());
			cref = capi.createContainer("Storage", uri, Container.UNBOUNDED, coordinator, null, null);
		}
		return cref;
	}
    
}
