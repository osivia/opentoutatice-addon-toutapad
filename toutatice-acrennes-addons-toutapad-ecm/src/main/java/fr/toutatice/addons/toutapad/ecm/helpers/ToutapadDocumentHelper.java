package fr.toutatice.addons.toutapad.ecm.helpers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.versioning.VersioningService;

import fr.toutatice.ecm.platform.core.helper.ToutaticeSilentProcessRunnerHelper;

public class ToutapadDocumentHelper {

	private static final Log log = LogFactory.getLog(ToutapadDocumentHelper.class);

	private static final List<Class<?>> FILTERED_SERVICES_LIST = new ArrayList<Class<?>>() {
		private static final long serialVersionUID = 1L;
		
		{
			add(EventService.class);
			add(VersioningService.class);
		}
	};

	private ToutapadDocumentHelper() {
		// static class, cannot be instantiated
	}

	/**
	 * Synchronize the Toutatice Pad document with the remote PAD content.
	 * 
	 * @param session the current 
	 * @param pad the Toutatice Pad document to update
	 * @param content the content to synchronize
	 * @return void
	 * @throws ClientException 
	 */
	public static void synchronizePad(CoreSession session, DocumentModel pad, String content) throws ClientException {
		// synchronize the current Toutatice pad document metadata with the Etherpad pad content
		if (null != content) {
			ToutaticeSilentProcessRunnerHelper runner = new PadSynchronizationRunner(session, pad, content);
			runner.silentRun(true, FILTERED_SERVICES_LIST);
		}
	}

	private static class PadSynchronizationRunner extends ToutaticeSilentProcessRunnerHelper {
		private DocumentModel document;
		private String content;

		public PadSynchronizationRunner(CoreSession session, DocumentModel document, String content) {
			super(session);
			this.document = document;
			this.content = content;
		}

		@Override
		public void run() throws ClientException {
			this.document.setPropertyValue("note:note", this.content);
			this.session.saveDocument(this.document);
			log.debug("Synchronized successfully the pad '(" + this.document.getId() + ") " + this.document.getTitle() + "'");
		}
		
	}

}
