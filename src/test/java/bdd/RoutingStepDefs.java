package bdd;

import cucumber.annotation.After;
import cucumber.annotation.Before;
import cucumber.annotation.en.And;
import cucumber.annotation.en.Given;
import cucumber.annotation.en.Then;
import cucumber.annotation.en.When;
import cucumber.runtime.PendingException;
import com.ezuce.oacdlt.*;
import cucumber.table.DataTable;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: danna
 * Date: 6/26/13
 * Time: 2:52 PM
 */
public class RoutingStepDefs {
    HashMap<String, TestAgent> agents = new HashMap<String, TestAgent>();
    HashMap<Integer, TestCaller> callers = new HashMap<Integer, TestCaller>();

    @Given("^these agents:$")
    public void these_agents(List<ConfigAgent> configAgents) throws Throwable {
        for (ConfigAgent a : configAgents) {
            System.out.println("configuring " + a.name + " " + a.login);
            TestAgent agent = new TestAgent(a.login);
            agents.put(a.name, agent);
        }
    }

    @Given("^callers (\\d+)-(\\d+)$")
    public void callers_(int callerFrom, int callerTo) throws Throwable {
        for (int i = callerFrom; i <= callerTo; i++) {
            System.out.println("configuring caller " + i);
            callers.put(i, new TestCaller(i));
        }
    }

    @When("^(\\w+) logs in$")
    public void agent_logs_in(String name) throws Throwable {
        agents.get(name).login();
    }

    @When("^(\\w+) logs in and goes available$")
    public void agent_logs_in_and_goes_available(String name) throws Throwable {
        agents.get(name).loginAndGoAvailable();
    }

    @When("^caller (\\d+) calls line (\\d+)$")
    public void caller_calls_line(int id, int line) throws Throwable {
        callers.get(id).callLine(line);
    }

    @Then("^(\\w+)'s phone does not ring$")
    public void agent_s_phone_does_not_ring(String name) throws Throwable {
        assertFalse(agents.get(name).phoneHasRung());
    }

    @Then("^(\\w+)'s phone rings$")
    public void agent_s_phone_rings(String name) throws Throwable {
        assertTrue(agents.get(name).phoneHasRung());
    }

    @After
    public void do_after() {
        for (TestAgent agent : agents.values()) {
            agent.disconnect();
        }
        for (TestCaller caller : callers.values()) {
            caller.hangUp();
        }
    }

    @When("^(\\w+) answers the call$")
    public void agent_answers_the_call(String name) throws Throwable {
        assertTrue(agents.get(name).phoneHasRung());
        agents.get(name).answer();
    }

    @When("^caller (\\d+) hangs up$")
    public void caller_hangs_up(int id) throws Throwable {
        callers.get(id).hangUp();
    }

    @When("^(\\w+) wraps up$")
    public void agent_wraps_up(String name) throws Throwable {
        agents.get(name).wrapUp();
    }

    @And("^(\\w+) goes released$")
    public void agent_goes_released(String name) throws Throwable {
        agents.get(name).goReleased();
    }

    public static class ConfigAgent {
        String name;
        int login;

    }
}
