package fr.toutatice.addons.toutapad.ecm.automation;

import junit.framework.Assert;

import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.automation.client.OperationRequest;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.model.FileBlob;
import org.nuxeo.ecm.automation.client.model.PathRef;

public class ToutapadGetContentMain {

	public static void main(String[] args) throws Exception {
		HttpAutomationClient client = new HttpAutomationClient("http://localhost:8181/nuxeo/site/automation");

		try {
			Session session = client.getSession("nxberhaut", "BERHAUT");
			Assert.assertNotNull(session);

			OperationRequest request = session.newRequest(ToutapadGetContent.ID);
			PathRef input = new PathRef("/default-domain/espace-de-travail/dossiers-de-pads/ccccccccccc");
			request.setInput(input);
			
			Object response = request.execute();
			Assert.assertTrue(null != response);
			String content = FileUtils.readFile(((FileBlob) response).getFile());
			System.out.println(content);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (null != client) {
				client.shutdown();
			}
		}
	}

}
