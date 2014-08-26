package fr.toutatice.addons.toutapad.ecm.services;

import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface EtherpadClientService {

	public void createPAD(DocumentModel document) throws ClientException;
	public void deletePAD(DocumentModel document) throws ClientException;
	public Map getPADContent(DocumentModel document) throws ClientException;
	public boolean doPADExists() throws ClientException;
	
}
