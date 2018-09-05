package p2p;

import java.nio.ByteBuffer;

public abstract class Message {

	protected ByteBuffer bytebuffer;
	protected byte type;
	protected byte[] content;
	protected byte[] messageLength = new byte[4];
	protected byte[] payload;

	public static enum Type {
		CHOKE, UNCHOKE, INTERESTED, NOTINTERESTED, HAVE, BITFIELD, REQUEST, PIECE, HANDSHAKE;
	}

	abstract protected int getMessageLength();

	abstract protected byte[] getPayload();

}
