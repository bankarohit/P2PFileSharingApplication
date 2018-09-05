package p2p;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Download implements Runnable {
	private Socket socket;
	private DataInputStream in;
	private SharedData sharedData;
	private boolean isAlive;

	// client thread initialization
	public Download(Socket socket, String peerId, SharedData data) {
		init(socket, data);
	}

	private void init(Socket socket, SharedData data) {
		this.socket = socket;
		sharedData = data;
		isAlive = true;
		try {
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// server thread initialization
	public Download(Socket socket, SharedData data) {
		init(socket, data);
	}

	@Override
	public void run() {
		receiveMessage();
	}

	protected void receiveMessage() {
		// System.out.println("Receive started");
		while (isAlive()) {
			// System.out.println("Receive started");
			int messageLength = Integer.MIN_VALUE;
			messageLength = receiveMessageLength();
			if (!isAlive()) {
				continue;
			}
			byte[] payload = new byte[messageLength];
			receiveMessagePayload(payload);
			sharedData.addPayload(payload);
			// System.out.println("Receive finished");
		}

	}

	private synchronized boolean isAlive() {
		// TODO Auto-generated method stub
		return isAlive;
	}

	private int receiveMessageLength() {
		int len = Integer.MIN_VALUE;
		byte[] messageLength = new byte[4];
		try {
			receiveRawData(messageLength);
			len = ByteBuffer.wrap(messageLength).getInt();
		} catch (Exception e) {
			// isAlive = false;
			e.printStackTrace();
		}
		return len;
	}

	private void receiveMessagePayload(byte[] payload) {
		receiveRawData(payload);
	}

	private void receiveRawData(byte[] message) {
		try {
			in.readFully(message);
		} catch (EOFException e) {
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
