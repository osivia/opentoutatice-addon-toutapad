package fr.toutatice.addons.toutapad.ecm.automation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.impl.blob.StringBlob;
import org.nuxeo.runtime.api.Framework;

import fr.toutatice.addons.toutapad.ecm.services.EtherpadClientService;

@Operation(id = ToutapadGetContent.ID, category = Constants.CAT_DOCUMENT, label = "ToutapadGetContent", description = "Fetch the Toutatice PAD content in html format of the document passed-in parameter. "
		+ "A string blob is returned containing the PAD html content.")
public class ToutapadGetContent {
	public static final String ID = "Document.ToutapadGetContent";

	private static final Log log = LogFactory.getLog(ToutapadGetContent.class);

	@OperationMethod
    public Blob run(DocumentModel document) throws Exception {
		String content = null;
		
		try {
			EtherpadClientService service = Framework.getService(EtherpadClientService.class);
			String html = service.getPADContent(document, EtherpadClientService.PAD_CONTENT_MIME_TYPE_HTML);
			Document doc = Jsoup.parse(html);
			Elements item = doc.select("body");
			content = item.html();
		} catch (Exception e) {
			log.warn("Failed to get the PAD content, error: " + e.getMessage());
			throw new NuxeoException(e);
		}
		
		return new StringBlob((null != content) ? content : "", "text/plain");
	}

}
