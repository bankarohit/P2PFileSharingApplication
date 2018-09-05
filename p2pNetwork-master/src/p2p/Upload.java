package p2p;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.LinkedBlockingQueue;

public class Upload implements Runnable {
	private Socket socket;
	private DataOutputStream out;
	protected LinkedBlockingQueue<Integer> uploadLengthQueue;
	protected LinkedBlockingQueue<byte[]> uploadPayloadQueue;
	private boolean isAlive;

	// client thread initialization
	public Upload(Socket socket, String id, SharedData data) {
		init(socket, data);
	}

	// server thread initialization
	public Upload(Socket socket, SharedData data) {
		init(socket, data);
	}

	private void init(Socket clientSocket, SharedData data) {
		uploadPayloadQueue = new LinkedBlockingQueue<>();
		uploadLengthQueue = new LinkedBlockingQueue<>();
		isAlive = true;
		this.socket = clientSocket;
		// sharedData = data;
		try {
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (isAlive) {
			try {
				int messageLength = uploadLengthQueue.take();
				out.writeInt(messageLength);
				out.flush();
				byte[] payload = uploadPayloadQueue.take();
				out.write(payload);
				out.flush();
			} catch (SocketException e) {
				isAlive = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addMessage(int length, byte[] payload) {
		try {
			uploadLengthQueue.put(length);
			uploadPayloadQueue.put(payload);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
