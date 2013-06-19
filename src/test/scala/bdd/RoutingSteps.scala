package bdd

import cucumber.api.scala.{ScalaDsl, EN}
import org.scalatest.matchers.ShouldMatchers

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

class Routing extends ScalaDsl with EN with ShouldMatchers {
	
	var agent : AgentWebConnection = null
	var agentPhoneListener : TestPhoneListener = null
	var caller : Phone = null
	val baseConfig = new JavaConfig()
	
	When("""^agent (\d+) logs in$"""){ (agentId:String) =>
		val password = "password"
		val loginURI = URI.create("http://oacddev.ezuce.com:8936/login")
		val conURI = URI.create("ws://oacddev.ezuce.com:8936/wsock")
		val exec = Executors.newScheduledThreadPool(2)
		
		val sipDomain = "ezuce.com"
		val sipProxy = "oacddev.ezuce.com"
		val sipLocalInetAddress = "10.24.7.1"
		baseConfig.setDomain(sipDomain)
		val r = new SipURI(if (sipProxy.startsWith("sip:")) sipProxy
			else "sip:" + sipProxy)
		baseConfig.setOutboundProxy(r)
		baseConfig.setLocalInetAddress(
			InetAddress.getByName(sipLocalInetAddress))
		baseConfig.setPublicInetAddress(baseConfig.getLocalInetAddress())
		baseConfig.setMediaMode(MediaMode.none)
		baseConfig.setUserPart(agentId)
		baseConfig.setPassword("1234")
		
		val listener = new DummyAgentConnectionListener()
		agentPhoneListener = new TestPhoneListener("AGENT")
		val phone = new Phone(baseConfig, new Logger(null), agentPhoneListener)
		
		agent = new AgentWebConnection(agentId, password, listener, phone,
			loginURI, conURI, exec)	  
	}

	When("""^agent (\d+) goes available$"""){ (agentId:String) =>
		agent.getPhone().register()
		agent.connect()
		agent.goAvailable()
	}

	When("""^caller (\d+) calls line (\d+)$"""){ (callerId:String, line:String) =>
		baseConfig.setUserPart(callerId)
		baseConfig.setPassword("1234")
		val callerPhoneListener = new TestPhoneListener("CALLER")
		caller = new Phone(baseConfig, new Logger(null), callerPhoneListener)
		caller.dial(line)
	}
	
	Then("""^agent (\d+)'s phone rings$"""){ (agentId:String) =>
		Thread.sleep(2000)
		assert(agentPhoneListener.getCaller() === caller.getUser())
		caller.hangUp()
	}

	Then("""^agent (\d+)'s phone does not ring$"""){ (agentId:String) =>
		Thread.sleep(2000)
		assert(agentPhoneListener.getCaller() ===  null)
		caller.hangUp()
	}
}
