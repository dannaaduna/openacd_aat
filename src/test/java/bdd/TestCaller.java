package bdd;

import net.sourceforge.peers.sip.transport.SipResponse;

import com.ezuce.oacdlt.Phone;
import com.ezuce.oacdlt.PhoneListener;

class TestCaller {
	
	private Phone phone;
	private int username;
	private boolean onCall = false;

	public TestCaller(int username) {
		this.username = username;
	}

	public void callLine(int line) {
		phone = new Phone(TestManager.getInstance().getBaseConfig(username),
				new TestCallerPhoneListener());
		phone.register();
		onCall = true;
		phone.dial(Integer.toString(line));

	}

	public void hangUp() {
		if (onCall) {
			phone.hangUp();
			phone.close();
			phone = null;
		}
		onCall = false;
	}

	class TestCallerPhoneListener implements PhoneListener {

		@Override
		public void onIncomingCall(Phone phone) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRemoteHangup(Phone phone) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPickup(Phone phone) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onError(Phone phone, SipResponse sipResponse) {
			// TODO Auto-generated method stub

		}

	}
}