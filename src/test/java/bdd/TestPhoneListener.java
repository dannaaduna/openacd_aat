package bdd;

import java.util.concurrent.CountDownLatch;
import net.sourceforge.peers.sip.transport.SipResponse;

import com.ezuce.oacdlt.*;

public class TestPhoneListener implements PhoneListener {
	
	private CountDownLatch ringSignal;
	
	public TestPhoneListener() {
		ringSignal = null;	
	}

	public TestPhoneListener(CountDownLatch ringSignal) {
		this.ringSignal = ringSignal;
	}
	
	public void resetRingSignal(CountDownLatch ringSignal) {
		this.ringSignal = ringSignal;
	}
	
	@Override
	public void onIncomingCall(Phone phone) {
		if (ringSignal != null) ringSignal.countDown();
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
