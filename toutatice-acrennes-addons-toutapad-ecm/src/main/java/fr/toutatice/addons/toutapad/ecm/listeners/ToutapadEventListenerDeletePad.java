package fr.toutatice.addons.toutapad.ecm.listeners;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.addons.toutapad.ecm.services.EtherpadClientService;

public class ToutapadEventListenerDeletePad implements EventListener {

	private static final Log log = LogFactory.getLog(ToutapadEventListenerDeletePad.class);
	
	private EtherpadClientService service = null;
	
	public void handleEvent(Event event) throws ClientException {
		try {
			if (event.getContext() instanceof DocumentEventContext) {
				DocumentEventContext eventContext = (DocumentEventContext) event.getContext();
				DocumentModel document = eventContext.getSourceDocument();
				if ("Toutapad".equals(document.getType())) {
					getEtherpadClientService().deletePAD(document);
				}
			}
		} catch (Exception e) {
			log.warn("Failed to delete a pad (but it might not exist), cause: " + e.getMessage());
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
