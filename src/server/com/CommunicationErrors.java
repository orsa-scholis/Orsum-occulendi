package server.com;

public enum CommunicationErrors {
	unknownErr("error:Unbekannte Anfrage"),
	nameTooShort("error:Name zu kurz"),
	notYetConnected("error:Noch nicht mit dem Server verbunden"),
	gameExists("error:Ein Spiel mit dem selben Namen existier bereits, bitte wähle einene anderen Namen!"),
	gameFull("error:full"),
	emptyChatMessage("error:Chat Message was empty");

	private final String errorMessage;

	private CommunicationErrors(String errMsg){
		this.errorMessage = errMsg;
	}

	@Override
	public String toString(){
		return errorMessage;
	}
}
