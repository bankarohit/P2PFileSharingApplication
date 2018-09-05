package p2p;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.BitSet;
import java.util.concurrent.ConcurrentHashMap;

public class FileHandler {

	private static ConcurrentHashMap<Integer, byte[]> file = new ConcurrentHashMap<Integer, byte[]>();
	private static volatile BitSet filePieces = new BitSet(CommonProperties.getNumberOfPieces());

	public static byte[] getPiece(int index) {
		return file.get(index);
	}

	public static synchronized void receivedPiece(int index) {
		filePieces.set(index);
	}

	public static synchronized boolean isPieceAvailable(int index) {
		return filePieces.get(index);
	}

	// SplitFile function will get the appropriate file and split into chunks.
	public static void splitFile() {
		String filename = Constants.COMMON_PROPERTIES_FILE_PATH + CommonProperties.getFileName();
		// System.out.println(filename);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filename);
			int pieceSize = CommonProperties.getPieceSize();
			byte[] piece = new byte[pieceSize];

			long fileSize = CommonProperties.getFileSize();
			if (fileSize < pieceSize)
				pieceSize = (int) fileSize;
			int pieceIndex = 0;
			try {
				while (fileSize > 0) {
					fileSize -= fis.read(piece);
					if (fileSize < pieceSize)
						pieceSize = (int) fileSize;
					file.put(pieceIndex, piece);
					filePieces.set(pieceIndex++);
					// System.out.println(piece.hashCode());
					byte[] empty = new byte[pieceSize];
					piece = empty;
				}
			} catch (IOException fileReadError) {
				fileReadError.printStackTrace();
				System.out.println("Error while splitting file");
			}

		} catch (FileNotFoundException e) {

			System.out.println("Error reading common.cfg file");
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error while closing fileinputstream after reading file");
			}
		}
		System.out.println("FileHandler.splitFile() - Filepieces: " + filePieces.length());
		System.out.println("FileHandler.splitFile() - Filepieces cardinality: " + filePieces.get(0));
	}

	// WriteToFile will write all chunks to an output file.
	/*
	 * public static void writeToFile() {
	 * 
	 * // TODO: Need to give correct filename. String filename =
	 * Constants.COMMON_PROPERTIES_MY_FILE_PATH; FileOutputStream fos = null; try {
	 * fos = new FileOutputStream(filename); for (int i = 0; i < file.size(); i++) {
	 * try { fos.write(file.get(i)); // System.out.println(file.get(i).hashCode());
	 * } catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } } } catch (FileNotFoundException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } }
	 */

	public synchronized static BitSet getFilePieces() {
		return filePieces;
	}

	public static void setFilePieces(BitSet filePieces) {
		FileHandler.filePieces = filePieces;
	}

	public static boolean isBitSetEmpty() {
		return filePieces.cardinality() == 0;
	}
}