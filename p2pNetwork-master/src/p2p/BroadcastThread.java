package p2p;

import java.util.concurrent.LinkedBlockingQueue;

public class BroadcastThread extends Thread {
	private LinkedBlockingQueue<Object[]> queue;
	private MessageManager messageManager;
	private Connection conn;
	private Message.Type messageType;
	private int pieceIndex;
	private static BroadcastThread broadcaster;

	private BroadcastThread() {
		queue = new LinkedBlockingQueue<>();
		messageManager = MessageManager.getInstance();
		conn = null;
		messageType = null;
		pieceIndex = Integer.MIN_VALUE;
	}

	protected static synchronized BroadcastThread getInstance() {
		if (broadcaster == null) {
			broadcaster = new BroadcastThread();
			broadcaster.start();
		}
		return broadcaster;
	}

	protected synchronized void addMessage(Object[] data) {
		try {
			queue.put(data);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			Object[] data = retrieveMessage();
			conn = (Connection) data[0];
			messageType = (Message.Type) data[1];
			pieceIndex = (int) data[2];
			System.out.println(
					"Broadcaster: Building " + messageType + pieceIndex + " to peer " + conn.getRemotePeerId());
			int messageLength = messageManager.getMessageLength(messageType, pieceIndex);
			byte[] payload = messageManager.getMessagePayload(messageType, pieceIndex);
			conn.sendMessage(messageLength, payload);
			System.out.println("Broadcaster: Sending " + messageType + " to peer " + conn.getRemotePeerId());

		}
	}

	private Object[] retrieveMessage() {
		Object[] data = null;
		try {
			data = queue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}

}
