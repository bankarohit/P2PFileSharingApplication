package p2p;

public class CommonProperties {

	private static int numberOfPreferredNeighbors;
	private static int unchokingInterval;
	private static int optimisticUnchokingInterval;
	private static String fileName;
	private static long fileSize;
	private static int pieceSize;
	private static int numberOfPieces;

	public static int getNumberOfPreferredNeighbors() {
		return numberOfPreferredNeighbors;
	}

	public static int getUnchokingInterval() {
		return unchokingInterval;
	}

	public static int getNumberOfPieces() {
		return numberOfPieces;
	}

	public static void calculateNumberOfPieces() {
		numberOfPieces = (int) (fileSize % pieceSize) == 0 ? (int) (fileSize / pieceSize)
				: (int) (fileSize / pieceSize) + 1;
		System.out.println("CommonProperties.calculateNumberOfPieces - Number of pieces: " + numberOfPieces);
	}

	public static void setUnchokingInterval(int p) {
		unchokingInterval = p;
	}

	public static int getOptimisticUnchokingInterval() {
		return optimisticUnchokingInterval;
	}

	public static void setOptimisticUnchokingInterval(int m) {
		optimisticUnchokingInterval = m;
	}

	public static String getFileName() {
		return fileName;
	}

	public static void setFileName(String name) {
		fileName = name;
	}

	public static long getFileSize() {
		return fileSize;
	}

	public static void setFileSize(long size) {
		fileSize = size;
	}

	public static int getPieceSize() {
		return pieceSize;
	}

	public static void setPieceSize(int size) {
		pieceSize = size;
	}

	public static String print() {
		return "PeerProperties [numberOfPreferredNeighbors=" + numberOfPreferredNeighbors + ", unchokingInterval="
				+ unchokingInterval + ", optimisticUnchokingInterval=" + optimisticUnchokingInterval + ", fileName="
				+ fileName + ", fileSize=" + fileSize + ", pieceSize=" + pieceSize + "]";
	}

	public static void setNumberOfPreferredNeighbors(int k) {
		numberOfPreferredNeighbors = k;
	}

}
