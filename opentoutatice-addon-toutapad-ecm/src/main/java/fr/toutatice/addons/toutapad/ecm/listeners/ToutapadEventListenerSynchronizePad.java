package fr.toutatice.addons.toutapad.ecm.listeners;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.platform.query.nxql.CoreQueryDocumentPageProvider;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.addons.toutapad.ecm.helpers.ToutapadDocumentHelper;
import fr.toutatice.addons.toutapad.ecm.services.EtherpadClientService;

public class ToutapadEventListenerSynchronizePad implements EventListener {
	
	private static final Log log = LogFactory.getLog(ToutapadEventListenerSynchronizePad.class);
	
	public void handleEvent(Event event) throws NuxeoException {
		if (event.getContext() instanceof EventContextImpl) {

			try {
				Collection<Repository> repositories = Framework.getService(RepositoryManager.class).getRepositories();
				for (Repository repository : repositories) {
					UnrestrictedSessionRunner runner = new ToutapadContentSynchronizationRunner(repository.getName());
					runner.runUnrestricted();
				}
			} catch (Exception e) {
				log.error("Failed to synchronize the pad(s) content, error: " + e.getMessage());
			}
		}
	}

	private class ToutapadContentSynchronizationRunner extends UnrestrictedSessionRunner {
		private EtherpadClientService service = null;
		
		public ToutapadContentSynchronizationRunner(String repository) {
			super(repository);
		}

		@Override
		public void run() throws NuxeoException {
			List<DocumentModel> padsList = getActivePADs();
			if (null != padsList && 0 < padsList.size()) {
				for (DocumentModel pad : padsList) {
					try {
						String content = getEtherpadClientService().getPADContent(pad, EtherpadClientService.PAD_CONTENT_MIME_TYPE_TEXT);
						ToutapadDocumentHelper.synchronizePad(this.session, pad, content);
					} catch (Exception e) {
						log.error("Failed to synchronize the pad '" + pad.getTitle()+ "' (id='" + pad.getId() + "'), error: " + e.getMessage());
					}
				}
			}
		}
		
		private EtherpadClientService getEtherpadClientService() throws NuxeoException {
			if (null == this.service) {
				this.service = Framework.getLocalService(EtherpadClientService.class);
				if (null == this.service) {
					throw new NuxeoException("Failed to obtain the Etherpad client service");
				}
			}
			
			return this.service;
		}
		
	    @SuppressWarnings("unchecked")
		private List<DocumentModel> getActivePADs() {
	        PageProviderService ppService = Framework.getService(PageProviderService.class);
	        if (ppService == null) {
	            throw new RuntimeException("Missing PageProvider service");
	        }
	        
	        Map<String, Serializable> props = new HashMap<String, Serializable>();
	        props.put(CoreQueryDocumentPageProvider.CORE_SESSION_PROPERTY, (Serializable) this.session);
            props.put(CoreQueryDocumentPageProvider.USE_UNRESTRICTED_SESSION_PROPERTY, Boolean.TRUE);
			PageProvider<DocumentModel> pp = (PageProvider<DocumentModel>) ppService.getPageProvider("GET_ACTIVE_PADS_FOR_SYNCHRONISATION", null, null, null, props);
	        if (pp == null) {
	            throw new NuxeoException("Page provider not found: " + "GET_ACTIVE_PADS_FOR_SYNCHRONISATION");
	        }
	        return pp.getCurrentPage();
	    }
		
	}

}
