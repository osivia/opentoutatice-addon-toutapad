package fr.toutatice.addons.toutapad.ecm.services;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

@XObject(value = "EtherpadServer")
public class EtherpadClientServiceDescriptor {
	@XNode("@name")
	private String name;

	@XNode("@default")
	protected boolean defaultServer;
	
	@XNode("@enabled")
	protected boolean enabled;

	@XNode("serverURL")
	private String serverURL;

	@XNode("prefixURL")
	private String prefixURL;

	@XNode("apiKey")
	private String apiKey;

	@XNode("welcomeMessage")
	private String welcomeMessage;

	@XNode("synchronizationCron")
	private String synchronizationCron;

	@XNode("contributionFormUrl")
	private String contributionFormUrl;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDefaultServer() {
		return defaultServer;
	}

	public void setDefaultServer(boolean defaultServer) {
		this.defaultServer = defaultServer;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	public String getPrefixURL() {
		return prefixURL;
	}

	public void setPrefixURL(String prefixURL) {
		this.prefixURL = prefixURL;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getWelcomeMessage() {
		return welcomeMessage;
	}

	public void setWelcomeMessage(String welcomeMessage) {
		this.welcomeMessage = welcomeMessage;
	}

	public String getSynchronizationCron() {
		return synchronizationCron;
	}

	public void setSynchronizationCron(String synchronizationCron) {
		this.synchronizationCron = synchronizationCron;
	}

	public String getContributionFormUrl() {
		return contributionFormUrl;
	}

	public void setContributionFormUrl(String contributionFormUrl) {
		this.contributionFormUrl = contributionFormUrl;
	}
	
}
