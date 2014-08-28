package fr.toutatice.addons.toutapad.ecm.services;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface EtherpadClientService {
	
	public static final String PAD_CONTENT_MIME_TYPE_HTML = "html";
	public static final String PAD_CONTENT_MIME_TYPE_TEXT = "text";

	public void createPAD(DocumentModel document) throws ClientException;
	public void deletePAD(DocumentModel document) throws ClientException;
	public String getPADContent(DocumentModel document, String mimetype) throws ClientException;
	public String getPADURL(DocumentModel document) throws ClientException;
	public String getPADReadOnlyURL(DocumentModel document) throws ClientException;
	
}
