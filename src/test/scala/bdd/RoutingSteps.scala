package bdd

import org.scalatest.matchers._

import cucumber.api.scala.{ScalaDsl, EN}
import org.scalatest.matchers.ShouldMatchers
import scala.collection.mutable.{Map => MutableMap}

class Routing extends ScalaDsl with EN with ShouldMatchers {

	var agent : TestAgent = null
	var caller : TestCaller = null
	val agents = MutableMap[Int, TestAgent]()
	var firstAgent : TestAgent = null
	var secondAgent : TestAgent = null

	Before() {
		for (i <- 1100 to 1105) {
			agents += i -> TestManager.createAgent(i)
		}
	}

	When("""^agent (\d+) logs in$"""){ (agentId:Int) =>
		agents(agentId).login()
	}

	When("""^agent (\d+) logs in and goes available$"""){ (agentId:Int) =>
		agents(agentId).loginAndGoAvailable()
	}

	When("""^agents (\d+) and (\d+) log in and go available$"""){ 
	 (id1:Int, id2:Int) =>
		agents(id1).loginAndGoAvailable()
		agents(id2).loginAndGoAvailable()
	}

	When("""^agent (\d+) goes released$"""){ (agentId:Int) =>
		agents(agentId).goReleased()
	}

	When("""^caller (\d+) calls line (\d+)$"""){ (callerId:Int, line:Int) =>
		caller = TestManager.createCaller(callerId)
		caller.callLine(line)
	}

	When("""^caller (\d+) hangs up$"""){ (callerId:Int) =>
		caller.hangUp()
	}

	When("""^agent (\d+) wraps up$"""){ (agentId:Int) =>
		agents(agentId).endWrapup()
	}

	When("""^agent (\d+) answers the call$"""){ (agentId:Int) =>
		assert(agents(agentId).phoneHasRung())
		agents(agentId).answer()
	}

	When("""^the first agent rejects the call$"""){ () =>
		firstAgent.reject()
	}

	Then("""^agent (\d+)`s phone does not ring$"""){ (agentId:Int) =>
		assert(agents(agentId).phoneHasRung() === false)
	}

	Then("""^the second agent`s phone rings$"""){ () =>
		assert(secondAgent.phoneHasRung())
	}

	Then("""^either agent (\d+) or (\d+)`s phone rings$"""){
	 (id1:Int, id2:Int) =>
	 	val hasRung1 = agents(id1).phoneHasRung()
	 	val hasRung2 = agents(id2).phoneHasRung()
	 	assert(hasRung1 ^ hasRung2)
	 	if (hasRung1) {
	 		firstAgent = agents(id1)
	 		secondAgent = agents(id2)
	 	}
	 	else {
	 		firstAgent = agents(id2)
	 		secondAgent = agents(id1)
	 	}
	}

	Then("""^agent (\d+)`s phone rings$"""){ (agentId:Int) =>
		assert(agents(agentId).phoneHasRung())
	}

	After() {
		caller.hangUp()
		for (i <- 1100 to 1105) {
			agents(i).disconnect()
		}
	}
}
