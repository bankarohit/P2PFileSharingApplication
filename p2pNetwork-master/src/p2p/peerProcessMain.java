package p2p;

import java.io.IOException;

public class peerProcessMain {
	private static String peerId;

	public static void main(String args[]) throws IOException {
		peerId = args[0];
		init();
		System.out.println(CommonProperties.print());
		Peer current = Peer.getInstance();
		current.createTCPConnections();
		current.listenForConnections();
	}

	private static void init() {
		// Updates Log Configuration at run time so that peerId is appended to
		// the filename

		new LoadConfig();
		new LoadPeerList();
		Handshake.setId(peerId);
		if (LoadPeerList.getPeer(peerId).hasSharedFile()) {
			SharedFile.getInstance().splitFile();
		}
	}

	public static String getId() {
		return peerId;
	}
}
