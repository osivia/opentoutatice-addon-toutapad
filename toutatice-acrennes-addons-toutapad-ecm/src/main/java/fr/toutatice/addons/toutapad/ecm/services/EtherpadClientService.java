package fr.toutatice.addons.toutapad.ecm.services;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface EtherpadClientService {
	
	public static final String PAD_CONTENT_MIME_TYPE_HTML = "html";
	public static final String PAD_CONTENT_MIME_TYPE_TEXT = "text";
	public static final String URL_IDENTIFICATION_PARAMETER = "&amp;userName=%s";
	public static final String URL_READ_ONLY_PARAMETERS = "?showControls=false&amp;showChat=false&amp;showLineNumbers=false&amp;useMonospaceFont=false";
	public static final String URL_WRITE_PARAMETERS = "?showControls=true&amp;showChat=true&amp;showLineNumbers=true&amp;useMonospaceFont=false";

	public void createPAD(DocumentModel document) throws ClientException;
	public void deletePAD(DocumentModel document) throws ClientException;
	public String getPADContent(DocumentModel document, String mimetype) throws ClientException;
	public String getPADURL(DocumentModel document, boolean authentified) throws ClientException;
	public String getPADPublicURL(DocumentModel document) throws ClientException;
	public String getPADReadOnlyURL(DocumentModel document) throws ClientException;
	
}
