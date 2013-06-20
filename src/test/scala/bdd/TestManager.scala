package bdd

import java.net._
import net.sourceforge.peers._

import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService

import net.sourceforge.peers.JavaConfig
import net.sourceforge.peers.media.MediaMode
import net.sourceforge.peers.sip.syntaxencoding.SipURI
import net.sourceforge.peers.sip.transport.SipResponse

import org.apache.commons.configuration.Configuration

import com.ezuce.oacdlt._

object TestManager {

	def exec = Executors.newScheduledThreadPool(10)

	def getBaseConfig(username : Int) : JavaConfig = {
		val baseConfig = new JavaConfig()

		val sipDomain = "ezuce.com"
		val sipProxy = "oacddev.ezuce.com"
		val sipLocalInetAddress = "10.24.7.1"
		val r = new SipURI(if (sipProxy.startsWith("sip:")) sipProxy
			else "sip:" + sipProxy)

		baseConfig.setDomain(sipDomain)
		baseConfig.setOutboundProxy(r)
		baseConfig.setLocalInetAddress(
			InetAddress.getByName(sipLocalInetAddress))
		baseConfig.setPublicInetAddress(baseConfig.getLocalInetAddress())
		baseConfig.setMediaMode(MediaMode.none)
		baseConfig.setUserPart(username.toString())
		baseConfig.setPassword("1234")

		baseConfig
	}

	def createAgent(username : Int) : TestAgent = {
		new TestAgent(username)
	}

	def createCaller(username : Int) : TestCaller = {
		new TestCaller(username)
	}

}

class TestAgent(username : Int) {
	var connection : AgentWebConnection = null
	var phoneListener : TestPhoneListener = null

	def login() {
		val password = "password"
		val loginURI = URI.create("http://oacddev.ezuce.com:8936/login")
		val conURI = URI.create("ws://oacddev.ezuce.com:8936/wsock")
		
		val listener = new DummyAgentConnectionListener()
		phoneListener = new TestPhoneListener("AGENT")
		val phone = new Phone(TestManager.getBaseConfig(username), new Logger(null), phoneListener)

		connection = new AgentWebConnection(username.toString(), password, listener, phone, loginURI, conURI, TestManager.exec)
	
		connection.connect()
		connection.getPhone().register()
	}

	def loginAndGoAvailable() {
		val password = "password"
		val loginURI = URI.create("http://oacddev.ezuce.com:8936/login")
		val conURI = URI.create("ws://oacddev.ezuce.com:8936/wsock")
		
		val listener = new DummyAgentConnectionListener()
		phoneListener = new TestPhoneListener("AGENT")
		val phone = new Phone(TestManager.getBaseConfig(username), new Logger(null), phoneListener)

		connection = new AgentWebConnection(username.toString(), password, listener, phone, loginURI, conURI, TestManager.exec)
	
		connection.connect()
		connection.getPhone().register()
		connection.goAvailable()
	}

	def phoneHasRung() : Boolean = {
		phoneListener.hasRung()
	}


}

class TestCaller(username : Int) {

	var phone : Phone = null;

	def callLine(line : Int) {		
		val phoneListener = new TestPhoneListener("CALLER")
		phone = new Phone(TestManager.getBaseConfig(username), new Logger(null), phoneListener)

		phone.dial(line.toString())
	}

	def hangUp() {
		phone.hangUp()
	}
}
