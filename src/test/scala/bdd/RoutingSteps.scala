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
	
	var agent : TestAgent = null
	var caller : TestCaller = null

	When("""^agent (\d+) logs in$"""){ (agentId:Int) =>
		agent = TestManager.createAgent(agentId)
		agent.login()
	}

	When("""^agent (\d+) logs in and goes available$"""){ (agentId:Int) =>
		agent = TestManager.createAgent(agentId)
		agent.loginAndGoAvailable()
	}

	When("""^caller (\d+) calls line (\d+)$"""){ (callerId:Int, line:Int) =>
		caller = TestManager.createCaller(callerId)
		caller.callLine(line)
	}
	
	Then("""^agent (\d+)'s phone does not ring$"""){ (agentId:Int) =>
		Thread.sleep(2000)
		assert(agent.phoneHasRung() === false)
		caller.hangUp()
	}

	Then("""^agent (\d+)'s phone rings$"""){ (agentId:Int) =>
		Thread.sleep(2000)

		assert(agent.phoneHasRung())
		caller.hangUp()
	}
}
