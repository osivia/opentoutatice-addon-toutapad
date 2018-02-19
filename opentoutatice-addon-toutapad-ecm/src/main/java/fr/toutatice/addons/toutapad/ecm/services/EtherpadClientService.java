/*
 * (C) Copyright 2016 Académie de Rennes (http://www.ac-rennes.fr/), OSIVIA (http://www.osivia.com) and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package fr.toutatice.addons.toutapad.ecm.services;

import org.etherpad_lite_client.EPLiteException;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author Loïc Billon / AC-rennes
 *
 */
public interface EtherpadClientService {
	
	public static final String PAD_CONTENT_MIME_TYPE_HTML = "html";
	public static final String PAD_CONTENT_MIME_TYPE_TEXT = "text";
	public static final String URL_IDENTIFICATION_PARAMETER = "&userName=%s";
	public static final String URL_READ_ONLY_PARAMETERS = "?noColors=true&showControls=false&showChat=false&showLineNumbers=false&useMonospaceFont=false";
	public static final String URL_WRITE_PARAMETERS = "?showControls=true&showChat=true&showLineNumbers=true&useMonospaceFont=false";

	public void createPAD(DocumentModel document) throws ClientException;
	public void deletePAD(DocumentModel document) throws ClientException;
	public String getPADContent(DocumentModel document, String mimetype) throws EPLiteException;
	public String getPADURL(DocumentModel document, boolean authentified) throws EPLiteException;
//	public String getPADPublicURL(DocumentModel document) throws ClientException;
	public String getPADReadOnlyURL(DocumentModel document) throws EPLiteException;
	public boolean isPADViewConnectedMode() throws ClientException;
	
	
	/**
	 * Request an access to a pad with user session (given in core session) on a pad.
	 * 
	 * @param session the user session
	 * @param document th pad
	 * @return a session id
	 */
	public String grantAccess(CoreSession session, DocumentModel document) throws EPLiteException;
	
	/**
	 * Copy a pad and its content
	 * @param from
	 * @param to
	 */
	public void copyPAD(DocumentModel from, DocumentModel to)  throws ClientException;
	
}
