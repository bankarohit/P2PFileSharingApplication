package p2p;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class SharedFile extends Thread {
	private static ConcurrentHashMap<Integer, byte[]> file;
	private volatile static BitSet filePieces;
	private static FileChannel writeFileChannel;
	private LinkedBlockingQueue<byte[]> fileQueue;
	private static SharedFile sharedFile;
	private volatile HashMap<Connection, Integer> requestedPieces;

	private SharedFile() {
		fileQueue = new LinkedBlockingQueue<>();
		requestedPieces = new HashMap<>();
	}

	public static synchronized SharedFile getInstance() {
		if (null == sharedFile) {
			sharedFile = new SharedFile();
			sharedFile.start();
		}
		return sharedFile;
	}

	static {
		file = new ConcurrentHashMap<Integer, byte[]>();
		filePieces = new BitSet(CommonProperties.getNumberOfPieces());
		try {
			File createdFile = new File(Constants.COMMON_PROPERTIES_CREATED_FILE_PATH + peerProcessMain.getId()
					+ File.separatorChar + CommonProperties.getFileName());
			createdFile.getParentFile().mkdirs(); // Will create parent directories if not exists
			createdFile.createNewFile();
			writeFileChannel = FileChannel.open(createdFile.toPath(), StandardOpenOption.WRITE);
		} catch (IOException e) {
			System.out.println("Failed to create new file while receiving the file from host peer");
			e.printStackTrace();
		}
	}

	public void splitFile() {
		File filePtr = new File(Constants.COMMON_PROPERTIES_FILE_PATH + CommonProperties.getFileName());
		FileInputStream fis = null;
		DataInputStream dis = null;
		int fileSize = (int) CommonProperties.getFileSize();
		int numberOfPieces = CommonProperties.getNumberOfPieces();
		// System.out.println("Filesize: " + fileSize);
		try {
			fis = new FileInputStream(filePtr);
			dis = new DataInputStream(fis);
			int pieceSize = CommonProperties.getPieceSize();
			int pieceIndex = 0;
			// TODO: will fileInputStream always read pieceSize amount of data?
			try {
				for (int i = 0; i < CommonProperties.getNumberOfPieces(); i++) {
					pieceSize = i != numberOfPieces - 1 ? CommonProperties.getPieceSize()
							: fileSize % CommonProperties.getPieceSize();
					byte[] piece = new byte[pieceSize];
					// System.out.println("Reading piece of size: " + pieceSize);
					dis.readFully(piece);
					file.put(pieceIndex, piece);
					filePieces.set(pieceIndex++);
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
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Error while closing fileinputstream after reading file");
			}
		}
	}

	public synchronized byte[] getPiece(int index) {
		return file.get(index);
	}

	@Override
	public void run() {
		while (true) {
			try {
				byte[] payload = fileQueue.take();
				int pieceIndex = ByteBuffer.wrap(payload, 0, 4).getInt();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public synchronized void setPiece(byte[] payload) {
		filePieces.set(ByteBuffer.wrap(payload, 0, 4).getInt());
		file.put(ByteBuffer.wrap(payload, 0, 4).getInt(), Arrays.copyOfRange(payload, 4, payload.length));
		try {
			fileQueue.put(payload);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public synchronized void writeToFile(String peerId) {
		String filename = Constants.COMMON_PROPERTIES_CREATED_FILE_PATH + peerId + File.separatorChar
				+ CommonProperties.getFileName();
		System.out.println(filename);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filename);
			for (int i = 0; i < file.size(); i++) {
				try {
					fos.write(file.get(i));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean isPieceAvailable(int index) {
		return filePieces.get(index);
	}

	public synchronized boolean isCompleteFile() {
		return filePieces.cardinality() == CommonProperties.getNumberOfPieces();
	}

	public synchronized int getReceivedFileSize() {
		return filePieces.cardinality();
	}

	protected synchronized int getRequestPieceIndex(Connection conn) {
		if (isCompleteFile()) {
			System.out.println("File received");
			return Integer.MIN_VALUE;
		}
		BitSet peerBitset = conn.getPeerBitSet();
		int numberOfPieces = CommonProperties.getNumberOfPieces();
		BitSet peerClone = (BitSet) peerBitset.clone();
		BitSet myClone = (BitSet) filePieces.clone();
		peerClone.andNot(myClone);
		if (peerClone.cardinality() == 0) {
			return Integer.MIN_VALUE;
		}
		myClone.flip(0, numberOfPieces);
		myClone.and(peerClone);
		System.out.println(peerClone + " " + myClone);
		int[] missingPieces = myClone.stream().toArray();
		return missingPieces[new Random().nextInt(missingPieces.length)];
	}

	protected BitSet getFilePieces() {
		return filePieces;
	}

	protected synchronized boolean hasAnyPieces() {
		return filePieces.nextSetBit(0) != -1;
	}

	public synchronized void addRequestedPiece(Connection connection, int pieceIndex) {
		requestedPieces.put(connection, pieceIndex);

	}

	public synchronized void removeRequestedPiece(Connection connection) {
		requestedPieces.remove(connection);
	}

}
