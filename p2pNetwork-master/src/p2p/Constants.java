package p2p;

import java.io.File;

public class Constants {

	public static final String COMMON_PROPERTIES_CONFIG_PATH = System.getProperty("user.dir") + File.separatorChar
			+ "resources/Common.cfg";
	public static final String COMMON_PROPERTIES_FILE_PATH = System.getProperty("user.dir") + File.separatorChar
			+ "resources/";
	public static final String COMMON_PROPERTIES_CREATED_FILE_PATH = System.getProperty("user.dir") + File.separatorChar
			+ "/project/peer_";
	public static final String CPROP_NUMBER_OF_PREFERRED_NEIGHBORS = "NumberOfPreferredNeighbors";
	public static final String CPROP_UNCHOKING_INTERVAL = "UnchokingInterval";
	public static final String CPROP_OPTIMISTIC_UNCHOKING_INTERVAL = "OptimisticUnchokingInterval";
	public static final String CPROP_FILENAME = "FileName";
	public static final String CPROP_FILESIZE = "FileSize";
	public static final String CPROP_PIECESIZE = "PieceSize";

	public static final String PEER_PROPERTIES_CONFIG_PATH = System.getProperty("user.dir") + File.separatorChar
			+ "resources/PeerInfo.cfg";

	public static final String PEER_LOG_FILE_PATH = System.getProperty("user.dir") + File.separatorChar
			+ "/project/log_peer_";
	public static final String PEER_LOG_FILE_EXTENSION = ".log";
}
