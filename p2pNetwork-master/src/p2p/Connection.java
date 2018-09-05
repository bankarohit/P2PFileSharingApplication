package p2p;

import java.io.IOException;
import java.net.Socket;
import java.util.BitSet;

public class Connection {
	Upload upload;
	Download download;
	SharedData sharedData;
	double bytesDownloaded;
	Socket peerSocket;
	String remotePeerId;
	boolean choked;
	private ConnectionManager connectionManager = ConnectionManager.getInstance();

	public double getBytesDownloaded() {
		return bytesDownloaded;
	}

	protected Upload getUpload() {
		return upload;
	}

	public synchronized void addBytesDownloaded(long value) {
		bytesDownloaded += value;
	}

	public synchronized boolean isChoked() {
		return choked;
	}

	public Connection(Socket peerSocket) {
		this.peerSocket = peerSocket;
		sharedData = new SharedData(this);
		upload = new Upload(peerSocket, sharedData);
		download = new Download(peerSocket, sharedData);
		createThreads(upload, download);
		sharedData.setUpload(upload);
		sharedData.start();
	}

	public Connection(Socket peerSocket, String peerId) {
		this.peerSocket = peerSocket;
		sharedData = new SharedData(this);
		upload = new Upload(peerSocket, peerId, sharedData);
		download = new Download(peerSocket, peerId, sharedData);
		createThreads(upload, download);
		LoggerUtil.getInstance().logTcpConnectionTo(Peer.getInstance().getNetwork().getPeerId(), peerId);
		sharedData.sendHandshake();
		sharedData.setUpload(upload);
		sharedData.start();
	}

	public void createThreads(Upload upload, Download download) {
		Thread uploadThread = new Thread(upload);
		Thread downloadThread = new Thread(download);
		uploadThread.start();
		downloadThread.start();
	}

	public synchronized void sendMessage(int messageLength, byte[] payload) {
		upload.addMessage(messageLength, payload);
	}

	public void close() {
		try {
			peerSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized String getRemotePeerId() {
		return remotePeerId;
	}

	public synchronized void tellAllNeighbors(int pieceIndex) {
		connectionManager.tellAllNeighbors(pieceIndex);
	}

	protected synchronized void addRequestedPiece(int pieceIndex) {
		SharedFile.getInstance().addRequestedPiece(this, pieceIndex);
	}

	public synchronized void addInterestedConnection() {
		connectionManager.addInterestedConnection(remotePeerId, this);
	}

	public synchronized void addNotInterestedConnection() {
		connectionManager.addNotInterestedConnection(remotePeerId, this);
	}

	public void receiveMessage() {
		download.receiveMessage();
	}

	public void setPeerId(String value) {
		remotePeerId = value;
	}

	public synchronized void removeRequestedPiece() {
		SharedFile.getInstance().removeRequestedPiece(this);
	}

	public synchronized BitSet getPeerBitSet() {
		// TODO Auto-generated method stub
		return sharedData.getPeerBitSet();
	}

	public synchronized boolean hasFile() {
		return sharedData.hasFile();
	}

	public synchronized void addAllConnections() {
		// TODO Auto-generated method stub
		connectionManager.addAllConnections(this);
	}

	public synchronized void setDownloadedbytes(int n) {
		bytesDownloaded = n;
	}
}
