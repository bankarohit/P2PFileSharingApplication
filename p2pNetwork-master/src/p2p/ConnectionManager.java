package p2p;

import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

public class ConnectionManager {

	private static ConnectionManager connectionManager;
	private HashSet<Connection> allConnections;
	// private HashMap<String, Connection> interested; // interested but choked
	private HashSet<Connection> notInterested;
	// private ArrayList<String> interestedPeerIds;
	private PriorityQueue<Connection> preferredNeighbors;
	// Banka
	public HashSet<String> peersWithFullFile = new HashSet<String>();
	private int k = CommonProperties.getNumberOfPreferredNeighbors();
	private int m = CommonProperties.getOptimisticUnchokingInterval();
	private int p = CommonProperties.getUnchokingInterval();
	private int n = LoadPeerList.numberOfPeers();
	private SharedFile sharedFile;
	// private int maxConnections = k + 1;
	// private int totalConnections = 0;
	private BroadcastThread broadcaster;

	public int getPeersWithFile() {
		return peersWithFullFile.size();
	}

	private ConnectionManager() {
		notInterested = new HashSet<>();
		preferredNeighbors = new PriorityQueue<>(k + 1,
				(a, b) -> (int) a.getBytesDownloaded() - (int) b.getBytesDownloaded());
		broadcaster = BroadcastThread.getInstance();
		sharedFile = SharedFile.getInstance();
		allConnections = new HashSet<>();
		monitor();
	}

	// TODO: Stop timer task p when a peer has the entire file himself & choose
	// neighbors randomly
	private void monitor() {
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (peersWithFullFile.size() == n - 1 && sharedFile.isCompleteFile()) {
					System.exit(0);
				}
				if (preferredNeighbors.size() > 1) {
					Connection conn = preferredNeighbors.poll();
					conn.setDownloadedbytes(0);
					for (Connection connT : preferredNeighbors) {
						connT.setDownloadedbytes(0);
					}
					broadcaster.addMessage(new Object[] { conn, Message.Type.CHOKE, Integer.MIN_VALUE });
					LoggerUtil.getInstance().logChangePreferredNeighbors(getTime(), peerProcessMain.getId(),
							preferredNeighbors);
					// System.out.println("Choking:" + conn.getRemotePeerId());
				}
			}
		}, new Date(), p * 1000);

		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				for (Connection conn : allConnections) {
					if (!notInterested.contains(conn) && !preferredNeighbors.contains(conn) && !conn.hasFile()) {
						broadcaster.addMessage(new Object[] { conn, Message.Type.UNCHOKE, Integer.MIN_VALUE });
						preferredNeighbors.add(conn);
						LoggerUtil.getInstance().logOptimisticallyUnchokeNeighbor(getTime(), peerProcessMain.getId(),
								conn.getRemotePeerId());
					}
				}
			}
		}, new Date(), m * 1000);

	}

	public static synchronized ConnectionManager getInstance() {
		if (connectionManager == null) {
			connectionManager = new ConnectionManager();
		}
		return connectionManager;
	}

	protected synchronized void tellAllNeighbors(int pieceIndex) {
		for (Connection conn : allConnections) {
			broadcaster.addMessage(new Object[] { conn, Message.Type.HAVE, pieceIndex });
		}
	}

	/*
	 * If preferredNeighbors < k, send unchoke & add to preferredNeighbors
	 * Otherwise, remove from not interested & add to interested
	 */
	public synchronized void addInterestedConnection(String peerId, Connection connection) {
		if (preferredNeighbors.size() <= k && !preferredNeighbors.contains(connection)) {
			connection.setDownloadedbytes(0);
			preferredNeighbors.add(connection);
			broadcaster.addMessage(new Object[] { connection, Message.Type.UNCHOKE, Integer.MIN_VALUE });
		}
		notInterested.remove(connection);
	}

	/*
	 * Remove from interested & add to not interested.
	 */
	public synchronized void addNotInterestedConnection(String peerId, Connection connection) {
		notInterested.add(connection);
		preferredNeighbors.remove(connection);
	}

	protected synchronized void createConnection(Socket socket, String peerId) {
		new Connection(socket, peerId);
	}

	protected synchronized void createConnection(Socket socket) {
		new Connection(socket);
	}

	public String getTime() {
		return Calendar.getInstance().getTime() + ": ";
	}

	public synchronized void addAllConnections(Connection connection) {
		// TODO Auto-generated method stub
		allConnections.add(connection);
	}

	// Banka
	public void addToPeersWithFullFile(String str) {
		peersWithFullFile.add(str);
	}
}
