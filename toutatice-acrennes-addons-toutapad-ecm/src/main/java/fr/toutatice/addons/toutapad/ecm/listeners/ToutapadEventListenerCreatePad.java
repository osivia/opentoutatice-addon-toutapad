package fr.toutatice.addons.toutapad.ecm.listeners;

import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.ui.web.util.ComponentUtils;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.addons.toutapad.ecm.services.EtherpadClientService;

public class ToutapadEventListenerCreatePad implements EventListener {

	private static final Log log = LogFactory.getLog(ToutapadEventListenerCreatePad.class);
	
	private EtherpadClientService service = null;
	
	public void handleEvent(Event event) throws ClientException {
		if (event.getContext() instanceof DocumentEventContext) {
			DocumentEventContext eventContext = (DocumentEventContext) event.getContext();
			DocumentModel document = eventContext.getSourceDocument();
			if ("Toutapad".equals(document.getType())) {
				try {
					getEtherpadClientService().createPAD(document);
				} catch (Exception e) {
					log.error("Failed to create a pad, error: " + e.getMessage());
					event.markRollBack("Impossible de créer le PAD '" + document.getTitle() + "'", e);
					String message = ComponentUtils.translate(FacesContext.getCurrentInstance(), "toutatice.acrennes.addons.toutapad.msg.create.error");
					FacesMessages.instance().add(StatusMessage.Severity.ERROR, message);			
				}
			}
		}
	}
	
	private EtherpadClientService getEtherpadClientService() throws ClientException {
		if (null == this.service) {
			this.service = Framework.getLocalService(EtherpadClientService.class);
		}

		if (null == this.service) {
			throw new ClientException("Failed to obtain the Etherpad client service");
		}
		
		return this.service;
	}

}
