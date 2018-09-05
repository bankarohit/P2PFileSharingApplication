package p2p;

import java.nio.ByteBuffer;

import p2p.Message.Type;

public class MessageManager {
	private static MessageManager messageManager;
	private SharedFile sharedFile;

	private MessageManager() {
		sharedFile = SharedFile.getInstance();
	}

	public static synchronized MessageManager getInstance() {
		if (messageManager == null) {
			messageManager = new MessageManager();
		}
		return messageManager;
	}

	public synchronized Type getType(byte type) {
		switch (type) {
		case 0:
			return Type.CHOKE;
		case 1:
			return Type.UNCHOKE;
		case 2:
			return Type.INTERESTED;
		case 3:
			return Type.NOTINTERESTED;
		case 4:
			return Type.HAVE;
		case 5:
			return Type.BITFIELD;
		case 6:
			return Type.REQUEST;
		case 7:
			return Type.PIECE;
		}
		return null;
	}

	public synchronized int processLength(byte[] messageLength) {
		return ByteBuffer.wrap(messageLength).getInt();
	}

	public synchronized int getMessageLength(Type messageType, int pieceIndex) {
		switch (messageType) {
		case CHOKE:
		case UNCHOKE:
		case INTERESTED:
		case NOTINTERESTED:
			return 1;
		case REQUEST:
		case HAVE:
			return 5;
		case BITFIELD:
			BitField bitfield = BitField.getInstance();
			return bitfield.getMessageLength();
		case PIECE:
			System.out.println("Shared file" + sharedFile.getPiece(pieceIndex) + " asking for piece " + pieceIndex);
			int payloadLength = 5 + SharedFile.getInstance().getPiece(pieceIndex).length;
			return payloadLength;
		case HANDSHAKE:
			return 32;
		}
		return -1;
	}

	public synchronized byte[] getMessagePayload(Type messageType, int pieceIndex) {
		byte[] payload = new byte[5];

		switch (messageType) {
		case CHOKE:
			return new byte[] { 0 };
		case UNCHOKE:
			return new byte[] { 1 };
		case INTERESTED:
			return new byte[] { 2 };
		case NOTINTERESTED:
			return new byte[] { 3 };
		case HAVE:
			payload[0] = 4;
			byte[] havePieceIndex = ByteBuffer.allocate(4).putInt(pieceIndex).array();
			System.arraycopy(havePieceIndex, 0, payload, 1, 4);
			break;
		case BITFIELD:
			BitField bitfield = BitField.getInstance();
			payload = bitfield.getPayload();
			break;
		case REQUEST:
			payload[0] = 6;
			byte[] index = ByteBuffer.allocate(4).putInt(pieceIndex).array();
			System.arraycopy(index, 0, payload, 1, 4);
			break;
		case PIECE:
			byte[] piece = sharedFile.getPiece(pieceIndex);
			int pieceSize = piece.length;
			int totalLength = 5 + pieceSize;
			payload = new byte[totalLength];
			payload[0] = 7;
			byte[] data = ByteBuffer.allocate(4).putInt(pieceIndex).array();
			System.arraycopy(data, 0, payload, 1, 4);
			System.arraycopy(piece, 0, payload, 5, pieceSize);
			break;
		case HANDSHAKE:
			return Handshake.getMessage();
		}
		return payload;
	}
}
