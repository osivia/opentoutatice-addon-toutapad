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
package fr.toutatice.addons.toutapad.ecm.automation;

import net.sf.json.JSONObject;

import org.etherpad_lite_client.EPLiteException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.addons.toutapad.ecm.services.EtherpadClientService;

/**
 * Request to join a pad
 * @author Loïc Billon
 *
 */
@Operation(id = ToutapadJoinRequest.ID, category = Constants.CAT_DOCUMENT, label = "ToutapadJoinRequest", description = "Request to join a pad")
public class ToutapadJoinRequest {

	public static final String ID = "Document.ToutapadJoinRequest";
	
    @Param(name = "path")
    protected String path;
    
    @Param(name = "editionEnabled")
    protected Boolean editionEnabled;

    @Context
    protected CoreSession session;

    /**
     * Return a JSONObject with url of the pad in read/write mode (evaluated by user permissions and editionEnabled parameter).
     * JSONObject contains also a sessionID which has to be set in the response cookie and an error in case of.
     * @return
     */
	@OperationMethod
    public Blob run() {
		
		JSONObject jso = new JSONObject();
		try {
			EtherpadClientService service = Framework.getService(EtherpadClientService.class);
			
			PathRef pathRef = new PathRef(path);
			DocumentModel document = session.getDocument(pathRef);
			
			
			String sessionId = service.grantAccess(session, document);
			jso.accumulate("sessionId", sessionId);
			
			if(session.hasPermission(pathRef, SecurityConstants.WRITE) && editionEnabled) {
				
				String padPublicURL = service.getPADURL(document, true);
				jso.accumulate("url", padPublicURL);
			}
			else if (session.hasPermission(pathRef, SecurityConstants.READ)) {
				String padPublicURL = service.getPADReadOnlyURL(document);
				jso.accumulate("url", padPublicURL);
			}

		}
		catch(EPLiteException ex) {
			jso.accumulate("error", "pad.error");
		}
		
		return new StringBlob(jso.toString(), "application/json");
	}
}
