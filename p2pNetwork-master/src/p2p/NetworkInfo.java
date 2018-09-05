package p2p;

public class NetworkInfo {

	private int number;
	private String peerId;
	private String hostName;
	private int port;
	private boolean hasSharedFile;

	public String getPeerId() {
		return peerId;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setPeerId(String peerId) {
		this.peerId = peerId;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	protected boolean hasSharedFile() {
		return hasSharedFile;
	}

	public void setHasSharedFile(boolean hasSharedFile) {
		this.hasSharedFile = hasSharedFile;
	}

	@Override
	public String toString() {
		return "Peer [peerId=" + peerId + ", hostName=" + hostName + ", port=" + port + ", hasSharedFile="
				+ hasSharedFile + "]";
	}
}
