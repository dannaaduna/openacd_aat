package bdd;

import net.sourceforge.peers.sip.transport.SipResponse;

import com.ezuce.oacdlt.*;

public class TestPhoneListener implements PhoneListener {
	
	private String type = null;
	private boolean hasRung = false;
	
	public TestPhoneListener(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}
	
	public boolean hasRung() {
		boolean response = hasRung;
		hasRung = false;
		return response;
	}

	@Override
	public void onIncomingCall(Phone phone) {
		phone.answer();
		hasRung = true;
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
	
}
