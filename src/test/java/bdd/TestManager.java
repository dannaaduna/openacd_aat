package bdd;

import com.ezuce.oacdlt.*;
import net.sourceforge.peers.JavaConfig;
import net.sourceforge.peers.Logger;
import net.sourceforge.peers.media.MediaMode;
import net.sourceforge.peers.sip.syntaxencoding.SipURI;
import net.sourceforge.peers.sip.syntaxencoding.SipUriSyntaxException;
import net.sourceforge.peers.sip.transport.SipResponse;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: danna
 * Date: 6/26/13
 * Time: 5:46 PM
 */
public class TestManager {

    public static ScheduledExecutorService exec = Executors.newScheduledThreadPool(10);

    public static JavaConfig getBaseConfig(int username) {
        JavaConfig baseConfig = new JavaConfig();
        try {

            String sipDomain = "ezuce.com";
            String sipProxy = "oacddev.ezuce.com";
            SipURI sipProxyUri = new SipURI(sipProxy.startsWith("sip:") ? sipProxy : "sip:" + sipProxy);
            String sipLocalInetAddress = "10.24.7.1";

            baseConfig.setDomain(sipDomain);
            baseConfig.setOutboundProxy(sipProxyUri);
            baseConfig.setLocalInetAddress(InetAddress.getByName(sipLocalInetAddress));
            baseConfig.setPublicInetAddress(baseConfig.getLocalInetAddress());
            baseConfig.setMediaMode(MediaMode.none);
            baseConfig.setUserPart(Integer.toString(username));
            baseConfig.setPassword("1234");
        }
        catch (UnknownHostException e) {

        }
        catch (SipUriSyntaxException e) {

        }
        return baseConfig;
    }

    public static TestAgent createAgent(int username) {
        return new TestAgent(username);
    }

}

class TestAgent {

    int username;
    AgentWebConnection connection;
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
        URI loginUri = URI.create("http://oacddev.ezuce.com:8936/login");
        URI connURI = URI.create("ws://oacddev.ezuce.com:8936/wsock");

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
        Logger logger = new Logger(null);
        phone = new Phone(TestManager.getBaseConfig(username), logger, phoneListener);

        AgentConnectionListener listener = new DummyAgentConnectionListener();
        connection = new AgentWebConnection(Integer.toString(username), password, listener, phone, loginUri, connURI, TestManager.exec);

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
        }
        catch (InterruptedException e) {
            hasRung = false;
        }
        resetRingSignal();
        return hasRung;
    }

    public void answer() {
        isOncall = true;
        phone.answer();
    }

    public void reject() {
        phone.hangUp();
    }

    public void wrapUp() {
        isOncall = false;
        connection.endWrapup();
    }

    public void disconnect() {
        if (isOncall) connection.endWrapup();
        if (connection !=  null) connection.disconnect();
    }

    private void resetRingSignal() {
        ringSignal = new CountDownLatch(1);
    }
}

class TestCaller {

    Phone phone;
    int username;
    boolean onCall = false;

    public TestCaller(int username) {
        this.username = username;
    }

    public void callLine(int line) {
        PhoneListener phoneListener = new PhoneListener() {
            @Override
            public void onIncomingCall(Phone phone) {

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
        Logger logger = new Logger(null);
        phone = new Phone(TestManager.getBaseConfig(username), logger, phoneListener);
        phone.register();
        onCall = true;
        phone.dial(Integer.toString(line));

    }

    public void hangUp() {
        if (onCall) phone.hangUp();
        onCall = false;
    }

}