package bdd

import cucumber.api.scala.{ScalaDsl, EN}
import org.scalatest.matchers.ShouldMatchers

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

	When("""^agent (\d+) goes released$"""){ (agentId:Int) =>
		agent.goReleased()
	}

	When("""^caller (\d+) calls line (\d+)$"""){ (callerId:Int, line:Int) =>
		caller = TestManager.createCaller(callerId)
		caller.callLine(line)
	}

	When("""^caller (\d+) hangs up$"""){ (callerId:Int) =>
		caller.hangUp()
		agent.endWrapup()
	}

	Then("""^agent (\d+)`s phone does not ring$"""){ (agentId:Int) =>
		Thread.sleep(2000)
		assert(agent.phoneHasRung() === false)
	}

	Then("""^agent (\d+)`s phone rings$"""){ (agentId:Int) =>
		Thread.sleep(2000)
		assert(agent.phoneHasRung())
	}

	After() {
		caller.hangUp()
		agent.disconnect()

		agent = null
		caller = null
	}
}
