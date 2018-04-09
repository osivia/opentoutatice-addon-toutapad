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
package fr.toutatice.addons.toutapad.ecm.listeners;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.event.CoreEventConstants;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.addons.toutapad.ecm.services.EtherpadClientService;

/**
 * Event if a pad is copied. 
 * The content is copied in the new pad without revision marks
 * @author Loïc Billon
 *
 */
public class ToutapadEventListenerCopyPad implements EventListener {

	private static final Log log = LogFactory.getLog(ToutapadEventListenerCopyPad.class);
	
	private EtherpadClientService service = null;
	
	public void handleEvent(Event event) throws NuxeoException {
		if (event.getContext() instanceof DocumentEventContext) {
			DocumentEventContext eventContext = (DocumentEventContext) event.getContext();
			DocumentModel to = eventContext.getSourceDocument();
									
			if ("ToutaticePad".equals(to.getType())) {
				try {
					
					DocumentRef createdFrom = (DocumentRef) eventContext.getProperty(CoreEventConstants.SOURCE_REF);
					CoreSession coreSession = eventContext.getCoreSession();
					DocumentModel from = coreSession.getDocument(createdFrom);
					
					getEtherpadClientService().copyPAD(from, to);
					
					
				} catch (Exception e) {
					log.error("Failed to copy a pad, error: " + e.getMessage());
					event.markRollBack("Impossible de copier le PAD '" + to.getTitle() + "'", e);
					String message = ComponentUtils.translate(FacesContext.getCurrentInstance(), "toutatice.acrennes.addons.toutapad.msg.create.error");
					FacesMessages.instance().add(StatusMessage.Severity.ERROR, message);			
				}
			}
		}
	}
	
	private EtherpadClientService getEtherpadClientService() throws NuxeoException {
		if (null == this.service) {
			this.service = Framework.getLocalService(EtherpadClientService.class);
		}

		if (null == this.service) {
			throw new NuxeoException("Failed to obtain the Etherpad client service");
		}
		
		return this.service;
	}

}
