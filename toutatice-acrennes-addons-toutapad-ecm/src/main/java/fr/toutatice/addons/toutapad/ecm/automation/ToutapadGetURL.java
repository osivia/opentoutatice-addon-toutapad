package fr.toutatice.addons.toutapad.ecm.automation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.addons.toutapad.ecm.services.EtherpadClientService;

@Operation(id = ToutapadGetURL.ID, category = Constants.CAT_DOCUMENT, label = "ToutapadGetURL", description = "Fetch the Toutatice PAD URL of the document passed_in parameter. The input document is returned.")
public class ToutapadGetURL {
	public static final String ID = "Document.ToutapadGetURL";

	private static final Log log = LogFactory.getLog(ToutapadGetURL.class);

	@Param(name = "AccessType", required = false, values = { SecurityConstants.WRITE, SecurityConstants.RESTRICTED_READ })
	protected String AccessType = SecurityConstants.RESTRICTED_READ;

	@OperationMethod
    public Blob run(DocumentModel document) throws Exception {
		String URL = null;
		
		try {
			EtherpadClientService service = Framework.getService(EtherpadClientService.class);
			if (null == AccessType || SecurityConstants.RESTRICTED_READ.equals(AccessType)) {
				URL = service.getPADReadOnlyURL(document);
			} else if (SecurityConstants.WRITE.equals(AccessType)) {
				URL = service.getPADURL(document);
			}
		} catch (Exception e) {
			log.warn("Failed to get the Toutapad URL, error: " + e.getMessage());
			throw new ClientException(e);
		}
		
		return new StringBlob((null != URL) ? URL : "", "text/plain");
	}

}
