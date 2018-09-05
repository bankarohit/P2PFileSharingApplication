package p2p;

import java.util.BitSet;

public class BitField extends Message {

	private static BitField bitfield;
	private SharedFile sharedFile;

	private BitField() {
		init();
	}

	private void init() {
		type = 5;
		payload = new byte[CommonProperties.getNumberOfPieces() + 1];
		content = new byte[CommonProperties.getNumberOfPieces()];
		sharedFile = SharedFile.getInstance();
		payload[0] = type;
		BitSet filePieces = sharedFile.getFilePieces();
		for (int i = 0; i < CommonProperties.getNumberOfPieces(); i++) {
			if (filePieces.get(i)) {
				// content[i] = 1;
				payload[i + 1] = 1;
			}
		}
	}

	public synchronized static BitField getInstance() {
		if (bitfield == null) {
			bitfield = new BitField();
		}
		return bitfield;
	}

	@Override
	protected synchronized int getMessageLength() {
		init();
		return payload.length;
	}

	@Override
	protected synchronized byte[] getPayload() {
		return payload;
	}

}
