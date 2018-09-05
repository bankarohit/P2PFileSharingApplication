package p2p;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class LoadPeerList {

	private static HashMap<String, NetworkInfo> peerList = new HashMap<>();
	static {
		int id = 1;
		try {
			Scanner sc = new Scanner(new File(Constants.PEER_PROPERTIES_CONFIG_PATH));
			while (sc.hasNextLine()) {
				String str[] = sc.nextLine().split(" ");
				NetworkInfo network = new NetworkInfo();
				network.setNumber(id++);
				network.setPeerId(str[0]);
				network.setHostName(str[1]);
				network.setPort(Integer.parseInt(str[2]));
				network.setHasSharedFile(str[3].equals("1") ? true : false);
				peerList.put(str[0], network);
			}
			sc.close();
		} catch (IOException e) {
			System.out.println("PeerInfo.cfg not found/corrupt");
		}
	}

	public static NetworkInfo getPeer(String id) {
		return peerList.get(id);
	}

	public static HashMap<String, NetworkInfo> getPeerList() {
		return peerList;
	}

	public static int numberOfPeers() {
		return peerList.size();
	}

}
