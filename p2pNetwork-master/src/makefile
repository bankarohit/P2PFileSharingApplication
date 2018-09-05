JFLAGS = -g -d 
ClassPath = src
JC = javac
JVM = java 
PEERPROCESS = .
MSG = p2p
PEER = p2p
UTIL = p2p
TARGET = src

.SUFFIXES: .java .class

default: Constants.java \
	CommonProperties.java \
	LoadConfig.java \
	NetworkInfo.java \
	FileHandler.java \
	LoadPeerList.java \
	Peer.java \
	LoggerUtil.java \
	Upload.java \
	Download.java \
	SharedFile.java \
	SharedData.java \
	BitField.java \
	Handshake.java \
	MessageManager.java \
	Message.java	 \
	ConnectionManager.java \
	Connection.java	\
	BroadcastThread.java \
	peerProcessMain.java

peerProcess.java: $(PEERPROCESS)/peerProcess.java
	$(JC) $(JFlags) ./$(PEERPROCESS)/peerProcess.java

Handshake.java: $(UTIL)/Handshake.java
	$(JC) $(JFlags) ./$(UTIL)/Handshake.java

MessageManager.java: $(UTIL)/MessageManager.java
	$(JC) $(JFlags) ./$(UTIL)/MessageManager.java


ConnectionManager.java: $(UTIL)/ConnectionManager.java
	$(JC) $(JFlags) ./$(UTIL)/ConnectionManager.java


Connection.java: $(UTIL)/Connection.java
	$(JC) $(JFlags) ./$(UTIL)/Connection.java
	
BroadcastThread.java: $(UTIL)/BroadcastThread.java
	$(JC) $(JFlags) ./$(UTIL)/BroadcastThread.java
	
peerProcessMain.java: $(UTIL)/peerProcessMain.java
	$(JC) $(JFlags) ./$(UTIL)/peerProcessMain.java

	
Constants.java: $(UTIL)/Constants.java
	$(JC) $(JFlags) ./$(UTIL)/Constants.java

CommonProperties.java : $(PEER)/CommonProperties.java
	$(JC) $(JFlags) ./$(PEER)/CommonProperties.java

LoadConfig.java : $(UTIL)/LoadConfig.java
	$(JC) $(JFlags) ./$(UTIL)/LoadConfig.java

NetworkInfo.java : $(PEER)/NetworkInfo.java
	$(JC) $(JFlags) ./$(PEER)/NetworkInfo.java

FileHandler.java: $(PEER)/FileHandler.java
		$(JC) $(JFlags) ./$(PEER)/FileHandler.java

LoadPeerList.java :  $(UTIL)/LoadPeerList.java
		$(JC) $(JFlags) ./$(UTIL)/LoadPeerList.java

Peer.java : $(PEER)/Peer.java 
		$(JC) $(JFlags) ./$(PEER)/Peer.java 

LoggerUtil.java : $(UTIL)/LoggerUtil.java
		$(JC) $(JFlags) ./$(UTIL)/LoggerUtil.java

SharedFile.java : $(PEER)/SharedFile.java 
	$(JC) $(JFlags) ./$(PEER)/SharedFile.java 

SharedData.java : $(PEER)/SharedData.java 
	$(JC) $(JFlags) ./$(PEER)/SharedData.java 

Upload.java : $(PEER)/Upload.java 
	$(JC) $(JFlags) ./$(PEER)/Upload.java 

Download.java : $(PEER)/Download.java 
	$(JC) $(JFlags) ./$(PEER)/Download.java 

BitField.java : $(MSG)/BitField.java 
	$(JC) $(JFlags) ./$(MSG)/BitField.java 

Choke.java : $(MSG)/Choke.java 
	$(JC) $(JFlags) ./$(MSG)/Choke.java 

Have.java : $(MSG)/Have.java 
	$(JC) $(JFlags) ./$(MSG)/Have.java

Interested.java : $(MSG)/Interested.java 
	$(JC) $(JFlags) ./$(MSG)/Interested.java 

Message.java : $(MSG)/Message.java 
	$(JC) $(JFlags) ./$(MSG)/Message.java 

NotInterested.java : $(MSG)/NotInterested.java 
	$(JC) $(JFlags) ./$(MSG)/NotInterested.java 
	
Piece.java : $(MSG)/Piece.java 
	$(JC) $(JFlags) ./$(MSG)/Piece.java 

Request.java : $(MSG)/Request.java 
	$(JC) $(JFlags) ./$(MSG)/Request.java 

Unchoke.java : $(MSG)/Unchoke.java 
	$(JC) $(JFlags) ./$(MSG)/Unchoke.java 



clean:
	$(RM) *.class
