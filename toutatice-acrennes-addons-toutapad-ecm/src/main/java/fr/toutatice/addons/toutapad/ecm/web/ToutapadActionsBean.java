package fr.toutatice.addons.toutapad.ecm.web;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.addons.toutapad.ecm.helpers.ToutapadDocumentHelper;
import fr.toutatice.addons.toutapad.ecm.services.EtherpadClientService;

@Name("toutapadActions")
@Scope(CONVERSATION)
public class ToutapadActionsBean implements ToutapadActions, Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ToutapadActionsBean.class);

    @In(create = true)
    protected transient NavigationContext navigationContext;

    protected transient EtherpadClientService padService;

	public String getPADURL() {
		String URL = "";
		
		try {
			DocumentModel currentDoc = navigationContext.getCurrentDocument();
			URL = getPADClientService().getPADURL(currentDoc, true);
		} catch (Exception e) {
			log.error("Failed to get the current document pad URL, error: " + e.getMessage());
		}
		
		return URL;
	}

	public String getPADReadOnlyURL() {
		String URL = "";
		
		try {
			DocumentModel currentDoc = navigationContext.getCurrentDocument();
			URL = getPADClientService().getPADReadOnlyURL(currentDoc);
		} catch (Exception e) {
			log.error("Failed to get the current document pad read only URL, error: " + e.getMessage());
		}
		
		return URL;
	}

	public String getPADPublicURL() {
		String URL = "";
		
		try {
			DocumentModel currentDoc = navigationContext.getCurrentDocument();
			URL = getPADClientService().getPADPublicURL(currentDoc);
		} catch (Exception e) {
			log.error("Failed to get the current document pad public URL, error: " + e.getMessage());
		}
		
		return URL;
	}

	public String getPADContent(String mimetype) {
		String content = "";
		
		try {
			DocumentModel currentDoc = navigationContext.getCurrentDocument();
			content = getPADClientService().getPADContent(currentDoc, mimetype);
			
			// synchronize the current Toutatice pad document metadata with the Etherpad pad content
			ToutapadDocumentHelper.synchronizePad(currentDoc.getCoreSession(), currentDoc, content);
		} catch (Exception e) {
			log.error("Failed to get the current document pad content, error: " + e.getMessage());
		}
		
		return content;
	}
	
	public boolean isPADAvailable() {
		boolean status = false;
		
		try {
			DocumentModel currentDoc = navigationContext.getCurrentDocument();
			getPADClientService().getPADContent(currentDoc, EtherpadClientService.PAD_CONTENT_MIME_TYPE_TEXT);
			status = true;
		} catch (Exception e) {
			log.error("Error status of the Etherpad server, error: " + e.getMessage());
		}
		
		return status;
	}
		
    private EtherpadClientService getPADClientService() throws ClientException {
    	try {
    		if (null == this.padService) {
    			this.padService = Framework.getService(EtherpadClientService.class);
    		}
    	} catch (Exception e) {
			throw new ClientException("Failed to obtain the Etherpad client service, error: " + e.getMessage());
    	}
    	
        return this.padService;
    }

}
