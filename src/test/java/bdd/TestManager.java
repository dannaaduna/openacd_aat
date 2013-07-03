package bdd;

import java.net.InetAddress;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import net.sourceforge.peers.JavaConfig;
import net.sourceforge.peers.media.MediaMode;
import net.sourceforge.peers.sip.syntaxencoding.SipURI;

/**
 * Created with IntelliJ IDEA. User: danna Date: 6/26/13 Time: 5:46 PM
 */
public class TestManager {

	private final static TestManager INSTANCE = new TestManager();

	public static TestManager getInstance() {
		return INSTANCE;
	}


	public ScheduledExecutorService exec = Executors
			.newScheduledThreadPool(10);
	private String sipxDomain;
	private URI loginUri;

	
	public void setUp(String domain) {
		this.sipxDomain = domain;
		this.loginUri = URI.create("http://" + domain + "/openacd/login");
	}

	public JavaConfig getBaseConfig(int username) {
		JavaConfig baseConfig = new JavaConfig();
		try {
			String sipDomain = sipxDomain;
			String sipProxy = sipxDomain;
			SipURI sipProxyUri = new SipURI(
					sipProxy.startsWith("sip:") ? sipProxy : "sip:" + sipProxy);
			String sipLocalInetAddress = "10.24.7.1";

			baseConfig.setDomain(sipDomain);
			baseConfig.setOutboundProxy(sipProxyUri);
			baseConfig.setLocalInetAddress(InetAddress
					.getByName(sipLocalInetAddress));
			baseConfig.setPublicInetAddress(baseConfig.getLocalInetAddress());
			baseConfig.setMediaMode(MediaMode.none);
			baseConfig.setUserPart(Integer.toString(username));
			baseConfig.setPassword("1234");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return baseConfig;
	}

	public TestAgent createAgent(int username) {
		return new TestAgent(username);
	}
	
	public URI getLoginUri() {
		return loginUri;
	}

}