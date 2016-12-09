package fr.toutatice.addons.toutapad.ecm.services;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.etherpad_lite_client.EPLiteClient;
import org.etherpad_lite_client.EPLiteException;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoPrincipal;
import org.nuxeo.ecm.core.scheduler.Schedule;
import org.nuxeo.ecm.core.scheduler.SchedulerService;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.toutatice.ecm.platform.core.constants.ToutaticeNuxeoStudioConst;

public class EtherpadClientServiceImpl extends DefaultComponent implements EtherpadClientService {

	private static final Log log = LogFactory.getLog(EtherpadClientServiceImpl.class);
	private static final String EXTENSION_POINT_SERVER = "EtherpadServers";

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
			log.debug("Toutapad: register contribution extension point = " + contributor.getName().getName());
			EtherpadClientServiceDescriptor desc = (EtherpadClientServiceDescriptor) contribution;
			if (desc.isEnabled()) {
				this.descriptors.put(desc.getName(), desc);
				this.serverName = desc.getName();
				String cron = desc.getSynchronizationCron();
				if (StringUtils.isNotBlank(cron)) {
					SchedulerService scheduler = Framework.getService(SchedulerService.class);
					if (null != scheduler) {
						scheduler.registerSchedule(new ToutapadSchedule(desc.getName(), cron));
					}
				} else {
					log.warn("No synchronization cron is defined. It won't be any synchronization of Toutatice PADs together with the remote server PADs.");
				}
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
	
//	private String documentToGroupdId(DocumentModel document) {
//		// Récupérer le webid
//		String webid  = document.getProperty(ToutaticeNuxeoStudioConst.CST_DOC_SCHEMA_TOUTATICE_WEBID).toString();
//		
//		HashMap<String, String> mapres = getClient().createGroupIfNotExistsFor(webid);
//		String groupId =  mapres.get("groupID");
//
//		return groupId;
//	}
//	
	
//	private String documentToPadId(DocumentModel document) {
//		// Récupérer le webid
//		String webid  = document.getProperty(ToutaticeNuxeoStudioConst.CST_DOC_SCHEMA_TOUTATICE_WEBID).toString();
//		
//		HashMap<String, String> mapres = getClient().createGroupIfNotExistsFor(webid);
//		String groupId =  mapres.get("groupID");
//
//		return groupId + "$" + webid;
//	}

	
	private EtherpadObject documentToPad(DocumentModel document) {
		String padId  = document.getPropertyValue(ToutaticeNuxeoStudioConst.CST_DOC_SCHEMA_TOUTATICE_WEBID).toString();
		
		HashMap<String, String> mapres = getClient().createGroupIfNotExistsFor(padId);
		String groupId =  mapres.get("groupID");
		
		return new EtherpadObject(groupId, padId);
	}
	
	public void createPAD(DocumentModel document) throws ClientException {
		
		EtherpadObject pad = documentToPad(document);
		
		// initialiser le message d'accueil
		try {
			String welcomeMessage = getDescriptor().getWelcomeMessage();
			welcomeMessage = URLEncoder.encode(welcomeMessage, "UTF-8");

			if(pad != null) {
				getClient().createGroupPad(pad.getGroupId(), pad.getPadId(), welcomeMessage);
			}
			
		} catch (UnsupportedEncodingException e) {
			log.error("Failed to set the default welcome message as defined whitin the configuration file");
		}
	}

	
	/* (non-Javadoc)
	 * @see fr.toutatice.addons.toutapad.ecm.services.EtherpadClientService#copyPAD(org.nuxeo.ecm.core.api.DocumentModel, org.nuxeo.ecm.core.api.DocumentModel)
	 */
	@Override
	public void copyPAD(DocumentModel from, DocumentModel to)
			throws ClientException {
		
		EtherpadObject fromPad = documentToPad(from);
		EtherpadObject toPad = documentToPad(to);
		
		HashMap<String, String> mapres = getClient().getText(fromPad.getGroupAndPadId());
		String text =  mapres.get("text");
		
		getClient().createGroupPad(toPad.getGroupId(), toPad.getPadId(), text);
		
	}
	
	public void deletePAD(DocumentModel document) throws ClientException {
		
		EtherpadObject pad = documentToPad(document);
				
		getClient().deletePad(pad.getGroupAndPadId());
		
		getClient().deleteGroup(pad.getGroupId());
		
		
	}

	public boolean isPADViewConnectedMode() throws ClientException {
		return getDescriptor().isConnectedView();
	}

	@SuppressWarnings("unchecked")
	public String getPADContent(DocumentModel document, String mimetype) throws ClientException {
		
		// TODO fetch content of a pad ?
		EtherpadObject pad = documentToPad(document);
		
		String content = "";
		
		if (mimetype.equals(EtherpadClientService.PAD_CONTENT_MIME_TYPE_HTML)) {
			Map<String, String>  map = getClient().getHTML(pad.getGroupAndPadId());
			content = map.get("html");
		} else if (mimetype.equals(EtherpadClientService.PAD_CONTENT_MIME_TYPE_TEXT)) {
			Map<String, String> map = getClient().getText(pad.getGroupAndPadId());
			content = map.get("text");
		} else {
			log.debug("Not support mimetype: " + mimetype);
		}
		
		return content;
	}

	public String getPADURL(DocumentModel document, boolean authentified) throws EPLiteException {
		
		EtherpadObject pad = documentToPad(document);

		String padUrl = getDescriptor().getServerURL() + getDescriptor().getPrefixURL() + pad.getGroupAndPadId();
		padUrl = padUrl.concat(EtherpadClientService.URL_WRITE_PARAMETERS);
		if (authentified) {
			NuxeoPrincipal principal = (NuxeoPrincipal) document.getCoreSession().getPrincipal();
			padUrl = padUrl.concat(String.format(EtherpadClientService.URL_IDENTIFICATION_PARAMETER, principal.getFirstName() + " " + principal.getLastName()));
		}
		return padUrl;
	}

	public String getPADReadOnlyURL(DocumentModel document)	throws EPLiteException {
		
		EtherpadObject pad = documentToPad(document);
		
		@SuppressWarnings("rawtypes")
		HashMap map = getClient().getReadOnlyID(pad.getGroupAndPadId());
		String roID = (String) map.get("readOnlyID");
		String padUrl = getDescriptor().getServerURL() + getDescriptor().getPrefixURL() + roID;
		return padUrl + EtherpadClientService.URL_READ_ONLY_PARAMETERS;
	}
	
//	public String getPADPublicURL(DocumentModel document) throws ClientException {
//		String padAnonymousUrl = getPADURL(document, false);
//		String publicFormUrl = getDescriptor().getContributionFormUrl();
//		String url = null;
//		
//		try {
//			url = (StringUtils.isNotBlank(publicFormUrl) ? publicFormUrl + URLEncoder.encode(padAnonymousUrl, "UTF-8") : padAnonymousUrl);
//		} catch (UnsupportedEncodingException e) {
//			throw new ClientException("Failed to obtain the public PAD URL, error: ", e);
//		}
//		
//		return url;
//	}
	
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

	private class ToutapadSchedule implements Schedule {
		
		private static final long serialVersionUID = -4131912394687143207L;
		
		String id;
		String cron;
		
		public ToutapadSchedule(String id, String cron) {
			this.id = id;
			this.cron = cron;
		}

		public String getId() {
			return this.id;
		}

		public String getEventId() {
			return "toutaticePadEventSynchronize";
		}

		public String getEventCategory() {
			return null;
		}

		public String getCronExpression() {
			return this.cron;
		}

		public String getUsername() {
			// TODO variabiliser
			return "admin";
		}

		@Override
		public boolean isEnabled() {
			return true;
		}
		
	}

	/* (non-Javadoc)
	 * @see fr.toutatice.addons.toutapad.ecm.services.EtherpadClientService#grantAccess(org.nuxeo.ecm.core.api.CoreSession, org.nuxeo.ecm.core.api.DocumentModel)
	 */
	@Override
	public String grantAccess(CoreSession session, DocumentModel document) throws EPLiteException {
				
		
		NuxeoPrincipal principal = (NuxeoPrincipal) session.getPrincipal();
		HashMap<String, String> mapres = client.createAuthorIfNotExistsFor(principal.getName(), principal.getFirstName() + " " + principal.getLastName());
		String authorId = mapres.get("authorID");
		
		EtherpadObject pad = documentToPad(document);
		
	
		Calendar validUntil = Calendar.getInstance();
		validUntil.add(Calendar.HOUR, 2);
		
		
		mapres = client.createSession(pad.getGroupId(), authorId, validUntil.getTimeInMillis());
		String sessionId = mapres.get("sessionID");
		
		return sessionId;
		

	}



}
