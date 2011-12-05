package tu.spacebased.bsp1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tu.spacebased.bsp1.exceptions.RegisterServerException;
import tu.spacebased.bsp1.exceptions.ServerNotFoundException;


/**
 * Hello world!
 *
 */
public class App extends java.rmi.server.UnicastRemoteObject implements Server, Serializable {
	
	/**
	 * ATM STATIC PLEASE CHANGE
	 */
    private static String host = "localhost";
    private static int port = 11203;
	private static java.util.Properties registry=null;
    private static String id;	   
    
    
    
	private static final long serialVersionUID = 8979900387821377669L;
	private static Registry reg = null;
	private static Map<String,IRemote> sMap = Collections.synchronizedMap(new HashMap<String,IRemote>());
	private ArrayList<String> serverNames = new ArrayList<String>();
	private IRemote serverRemoteInstance;
			
	public App() throws RemoteException {
		super(0);	
		//serverNames.add("Producer");
		serverNames.add("Assembler");
		serverNames.add("Tester");
		serverNames.add("Logistician");
		new Thread(
				new Runnable() {
					public void run() {
						try {
							while(!Thread.interrupted()){
								Thread.sleep(500);
		        				
				                for (String name : serverNames) {
				                	if (name != null) {
					                    try {
					                        Server remote = (Server) App.reg.lookup(name);
					                        IRemote sRem = remote.getRemote();
					                        if (sRem != null) {
						                        try {
						                        	App.registerServer(sRem,name);
						                        	serverNames.remove(name);
						        				} catch (RegisterServerException e) {
						        					System.out.println(e.getMessage());
						        					UnicastRemoteObject.unexportObject(sRem,true);
						        					return;
						        				}
					                        }
					                    } catch (ConnectException ex) {
					                        System.err.println(ex);
					                    } catch (Exception ex) {
					                        System.err.println(ex);
					                    }
				                	}
				                    if (serverNames.isEmpty() || serverNames.size()==0) {
				                    	break;
				                    	//Thread.currentThread().interrupt();
				                    }
				                }
							}
						} catch (InterruptedException ex) {
			                System.err.println(ex);
			            }
					}
				}
			).start();
	}
	
    public static void main( String[] args )
    {
    	//readRegistryProperties();
    	//TODO: check if transactions can be removed again
    	/*TODO: this structure of an app initializing all the containers
    	 * is quite ugly, but at least it works. */
	
        	try {
				reg = LocateRegistry.createRegistry(port);
			} catch (RemoteException e1) {
				System.out.println("Root Server kann nicht gestartet werden. Moeglicherweise haben sie bereites einen Root Server gestartet");
				return;
			}
			try {
				Server n = new App();
				Naming.rebind("rmi://"+host+":"+port+"/"+id, n);
				

				BufferedReader stdIn = new BufferedReader(
		                new InputStreamReader(System.in));
				
			   System.out.println("Server up. Hit enter to exit.");
				try {
					stdIn.readLine();	//warten auf enter
				} catch (IOException e) {
					System.out.println("Es ist ein Fehler aufgetreten. Die Anwendung wurde beendet.");
					UnicastRemoteObject.unexportObject(n,true);
					return;
				}			
				UnicastRemoteObject.unexportObject(n,true);	
			} catch (RemoteException e) {
				System.out.println("Es ist ein Verbindungsproblem aufgetreten. Die Anwendung wurde beendet");
			}
			catch (MalformedURLException e) {
				System.out.println("Es ist ein Fehler aufgetreten. Die Anwendung wurde beendet.");
			}
            
    }
/**
	public static void readRegistryProperties(){
		java.io.InputStream in = ClassLoader.getSystemResourceAsStream("registry.prop");
		if (in != null) {
			registry = new java.util.Properties();
			try {
				registry.load(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
		    host = registry.getProperty("registry.host"); 
		    port = Integer.parseInt(registry.getProperty("registry.port"));
		 
		} else {			
			System.out.println("properties file nicht gefunden"); 

		} 
	}
**/
    
	public static void registerServer(IRemote sRem, String tempURL) throws RegisterServerException {
		if(sMap.containsKey(tempURL)){	//existiert bereits
			throw new RegisterServerException(tempURL+ " existiert bereits. Server kann nicht registriert werden");
		}
		else{ //Server registrieren
			sMap.put(tempURL, sRem);
		}
		System.out.println(" : Registering Server with name '"+tempURL+"'");
	}
	
	
	//CALLBACK
	public IRemote getRemote() throws RemoteException {
		if(serverRemoteInstance == null) {
			IRemote remoteObj = (IRemote) new RemoteImpl(this);
			this.serverRemoteInstance = remoteObj;
		}
		return serverRemoteInstance;
	}
	@Override
	public String getUrl() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void printMsg(String msg) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unregisterServer(String url, String tempURL)
			throws RemoteException, ServerNotFoundException {
		// TODO Auto-generated method stub
		
	}
   
}
