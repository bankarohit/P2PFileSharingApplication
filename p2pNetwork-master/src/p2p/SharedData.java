package p2p;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.concurrent.LinkedBlockingQueue;

public class SharedData extends Thread {
	private volatile boolean bitfieldSent;
	private BitSet peerBitset;
	private String remotePeerId;
	private Connection conn;
	private volatile boolean uploadHandshake;
	private volatile boolean isHandshakeDownloaded;
	private SharedFile sharedFile;
	private BroadcastThread broadcaster;
	private boolean peerHasFile;
	private Peer host = Peer.getInstance();
	int i = 0;
	private LinkedBlockingQueue<byte[]> payloadQueue;
	private boolean isAlive;
	Upload upload;

	public SharedData(Connection connection) {
		conn = connection;
		payloadQueue = new LinkedBlockingQueue<>();
		isAlive = true;
		sharedFile = SharedFile.getInstance();
		broadcaster = BroadcastThread.getInstance();
		peerBitset = new BitSet(CommonProperties.getNumberOfPieces());
	}

	public void setUpload(Upload value) {
		upload = value;
		if (getUploadHandshake()) {
			broadcaster.addMessage(new Object[] { conn, Message.Type.HANDSHAKE, Integer.MIN_VALUE });
		}
	}

	@Override
	public void run() {
		while (isAlive) {
			try {
				byte[] p = payloadQueue.take();
				processPayload(p);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void addPayload(byte[] payload) {
		try {
			payloadQueue.put(payload);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized BitSet getPeerBitSet() {
		return peerBitset;
	}

	public synchronized void sendHandshake() {
		setUploadHandshake();
	}

	public synchronized void setUploadHandshake() {
		uploadHandshake = true;
	}

	public synchronized boolean getUploadHandshake() {
		return uploadHandshake;
	}

	public void updatePeerId(String peerId) {
		remotePeerId = peerId;
	}

	public synchronized String getRemotePeerId() {
		return remotePeerId;
	}

	public synchronized void setRemotePeerId(String remotePeerId) {
		this.remotePeerId = remotePeerId;
	}

	public synchronized void setBitfieldSent() {
		bitfieldSent = true;
	}

	public synchronized void setPeerBitset(byte[] payload) {
		for (int i = 1; i < payload.length; i++) {
			// System.out.print(payload[i]);
			if (payload[i] == 1) {
				peerBitset.set(i - 1);
			}
		}
		if (peerBitset.cardinality() == CommonProperties.getNumberOfPieces()) {
			peerHasFile = true;
			ConnectionManager.getInstance().addToPeersWithFullFile(remotePeerId);
		}
	}

	public synchronized void updatePeerBitset(int index) {
		peerBitset.set(index);
		// Banka
		if (peerBitset.cardinality() == CommonProperties.getNumberOfPieces()) {
			ConnectionManager.getInstance().addToPeersWithFullFile(remotePeerId);
			peerHasFile = true;
		}
	}

	protected void processPayload(byte[] payload) {
		Message.Type messageType = getMessageType(payload[0]);
		Message.Type responseMessageType = null;
		int pieceIndex = Integer.MIN_VALUE;
		System.out.println("Received message: " + messageType);
		// LoggerUtil.getInstance().logDebug("Received message: " + messageType);
		switch (messageType) {
		case CHOKE:
			// clear requested pieces of this connection
			LoggerUtil.getInstance().logChokingNeighbor(getTime(), peerProcessMain.getId(), conn.getRemotePeerId());
			conn.removeRequestedPiece();
			responseMessageType = null;
			break;
		case UNCHOKE:
			// respond with request
			LoggerUtil.getInstance().logUnchokingNeighbor(getTime(), peerProcessMain.getId(), conn.getRemotePeerId());
			responseMessageType = Message.Type.REQUEST;
			pieceIndex = sharedFile.getRequestPieceIndex(conn);
			break;
		case INTERESTED:
			// add to interested connections
			LoggerUtil.getInstance().logReceivedInterestedMessage(getTime(), peerProcessMain.getId(),
					conn.getRemotePeerId());
			conn.addInterestedConnection();
			responseMessageType = null;
			break;
		case NOTINTERESTED:
			// add to not interested connections
			LoggerUtil.getInstance().logReceivedNotInterestedMessage(getTime(), peerProcessMain.getId(),
					conn.getRemotePeerId());
			conn.addNotInterestedConnection();
			responseMessageType = null;
			break;
		case HAVE:
			// update peer bitset
			// send interested/not interested
			pieceIndex = ByteBuffer.wrap(payload, 1, 4).getInt();
			LoggerUtil.getInstance().logReceivedHaveMessage(getTime(), peerProcessMain.getId(), conn.getRemotePeerId(),
					pieceIndex);
			updatePeerBitset(pieceIndex);
			responseMessageType = getInterestedNotInterested();
			break;
		case BITFIELD:
			// update peer bitset
			// send interested/not interested
			setPeerBitset(payload);
			// try {
			// LoggerUtil.getInstance().logDebug(new String(payload, "UTF-8"));
			// } catch (UnsupportedEncodingException e1) {
			// // TODO Auto-generated catch block
			// e1.printStackTrace();
			// }
			responseMessageType = getInterestedNotInterested();
			break;
		case REQUEST:
			// send requested piece
			responseMessageType = Message.Type.PIECE;
			byte[] content = new byte[4];
			System.arraycopy(payload, 1, content, 0, 4);
			pieceIndex = ByteBuffer.wrap(content).getInt();
			// System.out.println(pieceIndex);
			if (pieceIndex == Integer.MIN_VALUE) {
				System.out.println("received file");
				responseMessageType = null;
			}
			break;
		case PIECE:
			/*
			 * update own bitset & file . Send have to all neighbors & notinterested to
			 * neighbors with same bitset. Respond with request update bytesDownloaded pi =
			 * pieceIndex
			 */
			pieceIndex = ByteBuffer.wrap(payload, 1, 4).getInt();
			conn.addBytesDownloaded(payload.length);
			sharedFile.setPiece(Arrays.copyOfRange(payload, 1, payload.length));
			LoggerUtil.getInstance().logDownloadedPiece(getTime(), peerProcessMain.getId(), conn.getRemotePeerId(),
					pieceIndex, sharedFile.getReceivedFileSize());
			responseMessageType = Message.Type.REQUEST;
			conn.tellAllNeighbors(pieceIndex);
			pieceIndex = sharedFile.getRequestPieceIndex(conn);
			if (pieceIndex == Integer.MIN_VALUE) {
				LoggerUtil.getInstance().logFinishedDownloading(getTime(), peerProcessMain.getId());
				sharedFile.writeToFile(peerProcessMain.getId());
				messageType = null;
				isAlive = false;
				responseMessageType = null;
				// conn.close();
			}
			break;
		case HANDSHAKE:
			remotePeerId = Handshake.getId(payload);
			conn.setPeerId(remotePeerId);
			conn.addAllConnections();
			// System.out.println("Handshake: " + responseMessageType);
			if (!getUploadHandshake()) {
				setUploadHandshake();
				LoggerUtil.getInstance().logTcpConnectionFrom(host.getNetwork().getPeerId(), remotePeerId);
				broadcaster.addMessage(new Object[] { conn, Message.Type.HANDSHAKE, Integer.MIN_VALUE });
				// System.out.println("Added " + messageType + " to broadcaster");
			}
			if (sharedFile.hasAnyPieces()) {
				responseMessageType = Message.Type.BITFIELD;
			}
			// System.out.println("Response Message Type: " + responseMessageType);
			break;
		}

		if (null != responseMessageType) {
			// System.out.println("Shared data if: Added " + responseMessageType + " to
			// broadcaster");
			broadcaster.addMessage(new Object[] { conn, responseMessageType, pieceIndex });
		}
	}

	private boolean isInterested() {
		for (int i = 0; i < CommonProperties.getNumberOfPieces(); i++) {
			if (peerBitset.get(i) && !sharedFile.isPieceAvailable(i)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasFile() {
		return peerHasFile;
	}

	private Message.Type getInterestedNotInterested() {
		if (isInterested()) {
			return Message.Type.INTERESTED;
		}
		return Message.Type.NOTINTERESTED;
	}

	private Message.Type getMessageType(byte type) {
		MessageManager messageManager = MessageManager.getInstance();
		if (!isHandshakeDownloaded()) {
			setHandshakeDownloaded();
			return Message.Type.HANDSHAKE;
		}
		return messageManager.getType(type);
	}

	private boolean isHandshakeDownloaded() {
		return isHandshakeDownloaded;
	}

	private void setHandshakeDownloaded() {
		isHandshakeDownloaded = true;
	}

	public String getTime() {
		return Calendar.getInstance().getTime() + ": ";
	}
}
