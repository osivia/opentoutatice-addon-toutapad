package fr.toutatice.addons.toutapad.ecm.web;


public interface ToutapadActions {
	
	 public String getPADURL();
	 public String getPADContent(String mimetype);
	 public String getPADPublicURL();
	 public boolean isPADAvailable();
	 public boolean isPADViewConnectedMode();

}
