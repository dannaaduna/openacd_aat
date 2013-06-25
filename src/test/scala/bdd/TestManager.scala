package bdd

import java.net.URI
import java.net.InetAddress

import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

import net.sourceforge.peers.JavaConfig
import net.sourceforge.peers.Logger
import net.sourceforge.peers.media.MediaMode
import net.sourceforge.peers.sip.syntaxencoding.SipURI
import net.sourceforge.peers.sip.transport.SipResponse

import org.apache.commons.configuration.Configuration

import com.ezuce.oacdlt._

object TestManager {

	def exec = Executors.newScheduledThreadPool(10)
	val agents : Array[TestAgent] = null

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
	var phone : Phone = null
	var phoneListener : TestPhoneListener = null
	var ringSignal : CountDownLatch = new CountDownLatch(1)
	var isOncall = false

	def login() {
		val password = "password"
		val loginURI = URI.create("http://oacddev.ezuce.com:8936/login")
		val conURI = URI.create("ws://oacddev.ezuce.com:8936/wsock")
		
		val listener = new TestAgentConnectionListener()
		phoneListener = new TestPhoneListener(ringSignal)
		phone = new Phone(TestManager.getBaseConfig(username), new Logger(null),
			phoneListener)

		connection = new AgentWebConnection(username.toString(), password,
			listener, phone, loginURI, conURI, TestManager.exec)
	
		connection.connect()
		connection.getPhone().register()
	}

	def goAvailable() {
		connection.goAvailable()
	}

	def goReleased() {
		connection.goReleased()
	}

	def loginAndGoAvailable() {
		login()
		goAvailable()
	}

	def phoneHasRung() : Boolean = {
		val hasRung = ringSignal.await(2, TimeUnit.SECONDS)
		resetRingSignal()
		hasRung
	}

	def resetRingSignal() {
		ringSignal = new CountDownLatch(1)
		phoneListener.resetRingSignal(ringSignal)
	}

	def answer() {
		isOncall = true
		phone.answer()
	}

	def reject() {
		phone.hangUp()
	}
	
	def endWrapup() {
		isOncall = false
		connection.endWrapup()
	}

	def disconnect() {
		if (isOncall) connection.endWrapup()
		if (connection !=  null) connection.disconnect()
	}

}

class TestCaller(username : Int) {

	var phone : Phone = null;

	def callLine(line : Int) {		
		val phoneListener = new TestPhoneListener()
		phone = new Phone(TestManager.getBaseConfig(username),
			new Logger(null), phoneListener)
		phone.register()

		phone.dial(line.toString())
	}

	def hangUp() {
		phone.hangUp()
	}

}
