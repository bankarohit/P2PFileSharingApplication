package p2p;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Peer {

	private static Peer host = new Peer();
	private NetworkInfo network;
	ConnectionManager connectionManager;
	public static boolean allPeersReceivedFiles = false;

	private Peer() {
		network = LoadPeerList.getPeer(peerProcessMain.getId());
		connectionManager = ConnectionManager.getInstance();
	}

	public static Peer getInstance() {
		return host;
	}

	// TODO: Use filePieces of filehandler instead of network
	public boolean hasFile() {
		return network.hasSharedFile();
	}
	// TODO: Optimize by maintaining index upto which all files have been received

	public NetworkInfo getNetwork() {
		return network;
	}

	public void setNetwork(NetworkInfo network) {
		this.network = network;
	}

	public void listenForConnections() throws IOException {

		ServerSocket socket = null;
		try {
			socket = new ServerSocket(network.getPort());
			// TODO: End connection when all peers have received files
			while (false == allPeersReceivedFiles) {
				Socket peerSocket = socket.accept();
				connectionManager.createConnection(peerSocket);
			}
		} catch (Exception e) {
			System.out.println("Closed exception");
		} finally {
			socket.close();
		}
	}

	public void createTCPConnections() {
		HashMap<String, NetworkInfo> map = LoadPeerList.getPeerList();
		int myNumber = network.getNumber();
		for (String peerId : map.keySet()) {
			NetworkInfo peerInfo = map.get(peerId);
			if (peerInfo.getNumber() < myNumber) {
				new Thread() {
					@Override
					public void run() {
						createConnection(peerInfo);
					}
				}.start();

			}
		}
	}

	private void createConnection(NetworkInfo peerInfo) {
		int peerPort = peerInfo.getPort();
		String peerHost = peerInfo.getHostName();
		try {
			Socket clientSocket = new Socket(peerHost, peerPort);
			connectionManager.createConnection(clientSocket, peerInfo.getPeerId());
			Thread.sleep(300);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
