package fr.toutatice.addons.toutapad.ecm.listeners;

import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.EventContextImpl;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.addons.toutapad.ecm.helpers.ToutapadDocumentHelper;
import fr.toutatice.addons.toutapad.ecm.services.EtherpadClientService;

public class ToutapadEventListenerSynchronizePad implements EventListener {
	
	private static final Log log = LogFactory.getLog(ToutapadEventListenerSynchronizePad.class);
	
	public void handleEvent(Event event) throws ClientException {
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
		private static final String LIST_ACTIVE_PADS_QUERY = "SELECT * FROM Document WHERE ecm:primaryType = 'ToutaticePad' "
				+ "AND ecm:mixinType != 'HiddenInNavigation' "
				+ "AND ecm:isCheckedInVersion = 0 "
				+ "AND ecm:currentLifeCycleState != 'deleted' "
				+ "AND ecm:isProxy = 0";
		
		private EtherpadClientService service = null;
		
		public ToutapadContentSynchronizationRunner(String repository) {
			super(repository);
		}

		@Override
		public void run() throws ClientException {
			DocumentModelList padsList = this.session.query(LIST_ACTIVE_PADS_QUERY);
			if (null != padsList) {
				for (DocumentModel pad : padsList) {
					String content = getEtherpadClientService().getPADContent(pad, EtherpadClientService.PAD_CONTENT_MIME_TYPE_TEXT);
					ToutapadDocumentHelper.synchronizePad(this.session, pad, content);
				}
				log.info("Synchronized " + padsList.size() + " Toutatice PAD(s) from the repository '" + this.repositoryName + "'");
			}
		}
		
		private EtherpadClientService getEtherpadClientService() throws ClientException {
			if (null == this.service) {
				this.service = Framework.getLocalService(EtherpadClientService.class);
				if (null == this.service) {
					throw new ClientException("Failed to obtain the Etherpad client service");
				}
			}
			
			return this.service;
		}
		
	}

}
