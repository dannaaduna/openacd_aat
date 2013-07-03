package bdd;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import net.sourceforge.peers.sip.transport.SipResponse;

import com.ezuce.oacdlt.AgentConnection;
import com.ezuce.oacdlt.AgentConnectionListener;
import com.ezuce.oacdlt.AgentWebConnection;
import com.ezuce.oacdlt.DummyAgentConnectionListener;
import com.ezuce.oacdlt.Phone;
import com.ezuce.oacdlt.PhoneListener;

class TestAgent {

	int username;
	AgentConnection connection;
	Phone phone;
	PhoneListener phoneListener;
	CountDownLatch ringSignal;
	boolean isOncall = false;

	public TestAgent(int username) {
		this.username = username;
		ringSignal = new CountDownLatch(1);

	}

	public void login() {
		String password = "password";

		phoneListener = new PhoneListener() {
			@Override
			public void onIncomingCall(Phone phone) {
				ringSignal.countDown();
			}

			@Override
			public void onRemoteHangup(Phone phone) {

			}

			@Override
			public void onPickup(Phone phone) {

			}

			@Override
			public void onError(Phone phone, SipResponse sipResponse) {

			}
		};
		phone = new Phone(TestManager.getInstance().getBaseConfig(username),
				phoneListener);

		AgentConnectionListener listener = new DummyAgentConnectionListener();
		// connection = new AgentWsockConnection(Integer.toString(username),
		// password, listener, phone, TestManager.loginUri, TestManager.connUri,
		// TestManager.exec);
		connection = new AgentWebConnection(Integer.toString(username),
				password, listener, phone, TestManager.getInstance()
						.getLoginUri().toString());
		connection.connect();
		connection.getPhone().register();

	}

	public void goAvailable() {
		connection.goAvailable();
	}

	public void loginAndGoAvailable() {
		login();
		goAvailable();
	}

	public void goReleased() {
		connection.goReleased();
	}

	public boolean phoneHasRung() {
		boolean hasRung;
		try {
			hasRung = ringSignal.await(2, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			hasRung = false;
		}
		resetRingSignal();
		return hasRung;
	}

	public void answer() {
		isOncall = true;
		phone.answer();
	}

	public void endCall() {
		phone.hangUp();
	}

	public void wrapUp() {
		isOncall = false;
		connection.endWrapup();
	}

	public void disconnect() {
		if (isOncall)
			connection.endWrapup();
		if (connection != null)
			connection.disconnect();
	}

	private void resetRingSignal() {
		ringSignal = new CountDownLatch(1);
	}
}