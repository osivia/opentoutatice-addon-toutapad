package fr.toutatice.addons.toutapad.ecm.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.plexus.util.StringUtils;
import org.etherpad_lite_client.EPLiteClient;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

public class EtherpadClientServiceImpl extends DefaultComponent implements EtherpadClientService {

	private static final Log log = LogFactory.getLog(EtherpadClientServiceImpl.class);
	private static final String EXTENSION_POINT_SERVER = "EtherpadServers";
	private static final String URL_READ_ONLY_PARAMETERS = "?showControls=false&amp;showChat=false&amp;showLineNumbers=false&amp;useMonospaceFont=false";

	private Map<String, EtherpadClientServiceDescriptor> descriptors;
	private EPLiteClient client;
	private String serverName;
	
	@Override
	public void activate(ComponentContext context) throws Exception {
		super.activate(context);
		if (null == this.descriptors) {
			this.descriptors = new HashMap<String, EtherpadClientServiceDescriptor>();
		}
	}
	
	@Override
	public void deactivate(ComponentContext context) throws Exception {
		super.deactivate(context);
		if (null != this.descriptors) {
			descriptors.clear();
		}
	}
	
	@Override
	public void applicationStarted(ComponentContext context) throws Exception {
		super.applicationStarted(context);
		initializeEtherpadClient();
	}

	@Override
	public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor)
			throws Exception {
		if (EtherpadClientServiceImpl.EXTENSION_POINT_SERVER.equals(extensionPoint)) {
			log.debug("Toutapad: register contribution extension point = " + extensionPoint);
			EtherpadClientServiceDescriptor desc = (EtherpadClientServiceDescriptor) contribution;
			if (desc.isEnabled()) {
				this.descriptors.put(desc.getName(), desc);
				this.serverName = desc.getName();
			}
		}
	}

	@Override
	public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor)
			throws Exception {
		if (EtherpadClientServiceImpl.EXTENSION_POINT_SERVER.equals(extensionPoint)) {
			log.debug("Toutapad: unregister contribution extension point = " + extensionPoint);
			EtherpadClientServiceDescriptor desc = (EtherpadClientServiceDescriptor) contribution;
			this.descriptors.remove(desc.getName());
		}
	}

	public void createPAD(DocumentModel document) throws ClientException {
		// créer le PAD
		getClient().createPad(document.getId());
		
//		// initialiser le message d'accueil
//		String welcomeMessage = getDescriptor().getWelcomeMessage();
//		if (StringUtils.isNotBlank(welcomeMessage)) {
//			getClient().setText(document.getId(), welcomeMessage);
//		}
	}

	public void deletePAD(DocumentModel document) throws ClientException {
		getClient().deletePad(document.getId());
	}

	@SuppressWarnings("unchecked")
	public String getPADContent(DocumentModel document, String mimetype) throws ClientException {
		String content = "";
		
		if (mimetype.equals(EtherpadClientService.PAD_CONTENT_MIME_TYPE_HTML)) {
			Map<String, String>  map = getClient().getHTML(document.getId());
			content = map.get("html");
		} else if (mimetype.equals(EtherpadClientService.PAD_CONTENT_MIME_TYPE_TEXT)) {
			Map<String, String>  map = getClient().getText(document.getId());
			content = map.get("text");
		} else {
			log.debug("Not support mimetype: " + mimetype);
		}
		
		return content;
	}

	public String getPADURL(DocumentModel document) throws ClientException {
		return getDescriptor().getServerURL() + getDescriptor().getPrefixURL() + document.getId();
	}

	public String getPADReadOnlyURL(DocumentModel document)	throws ClientException {
		HashMap map = getClient().getReadOnlyID(document.getId());
		String roID = (String) map.get("readOnlyID");
		return getDescriptor().getServerURL() + getDescriptor().getPrefixURL() + roID + URL_READ_ONLY_PARAMETERS;
	}
	
	private EtherpadClientServiceDescriptor getDescriptor() throws ClientException {
		if (StringUtils.isNotBlank(this.serverName)) {
			return this.descriptors.get(this.serverName);
		} else {
			throw new ClientException("No Etherpad server is defined and enabled from extension point '" + EtherpadClientServiceImpl.EXTENSION_POINT_SERVER + "'");
		}
	}
	
	private void initializeEtherpadClient() throws ClientException {
		try {
			this.client = new EPLiteClient(getDescriptor().getServerURL(), getDescriptor().getApiKey());
		} catch (Exception e) {
			throw new ClientException("Failed to instanciate Etherpad client, error: " + e.getMessage());
		}
	}
	
	private EPLiteClient getClient() throws ClientException {
		if (null == this.client) {
			initializeEtherpadClient();
		}
		return this.client;
	}

}
