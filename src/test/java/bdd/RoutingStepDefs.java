package bdd;

import com.ezuce.openuc.tester.setup.SimpleOpenACDSetUp;
import com.ezuce.openuc.tester.setup.model.AgentSecurity;
import cucumber.api.java.*;
import cucumber.api.java.en.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: danna
 * Date: 6/26/13
 * Time: 2:52 PM
 */
public class RoutingStepDefs {
    private final Logger logger = LoggerFactory
            .getLogger(RoutingStepDefs.class);

    private boolean isSetUp;
    private static SimpleOpenACDSetUp setup;

    private HashMap<String, TestAgent> agents = new HashMap<String, TestAgent>();
    private HashMap<Integer, TestCaller> callers = new HashMap<Integer, TestCaller>();

    static {
        try {
            setup = new SimpleOpenACDSetUp(new File(
                    "/Users/danna/dev/openuc-stable-rpm"),
                    "openucrpm.ezuce.ph",
                    "password",
                    new URL("https://openucrpm.ezuce.ph/"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void do_before() {
        isSetUp = true;
        TestManager.setUp("openucrpm.ezuce.ph");
        logger.info("Running before scenario");
    }

    @After
    public void do_after() {
        logger.info("Running after scenario: resetting setup");
        setup.reset();
        for (TestAgent agent : agents.values()) {
            agent.disconnect();
        }
        for (TestCaller caller : callers.values()) {
            caller.hangUp();
        }
    }

    @Given("^the agents:$")
    public void these_agents(List<ConfigAgent> configAgents) throws Throwable {
        System.out.println("Given these agents");
        for (ConfigAgent a : configAgents) {
            TestAgent agent = new TestAgent(a.login);
            String[] skills = a.skills.split(", *");

            agents.put(a.name, agent);
            setup.addAgent(Integer.toString(a.login), a.group, AgentSecurity.valueOf(a.securityLevel.toUpperCase()), skills);
        }

    }

    @Given("^the queues:$")
    public void these_queues(List<ConfigQueue> configQueues) throws Throwable {
        for (ConfigQueue q : configQueues) {
            String[] skills = q.skills.split(", *");
            setup.addClientQueueLine(q.client, q.name, Integer.toString(q.line), q.group, skills);
        }
    }

    @Given("^the callers (\\d+)-(\\d+)$")
    public void callers_(int callerFrom, int callerTo) throws Throwable {
        for (int i = callerFrom; i <= callerTo; i++) {
            callers.put(i, new TestCaller(i));
            setup.addCaller(Integer.toString(i));
        }
    }

    @When("^(\\w+) logs in$")
    public void agent_logs_in(String name) throws Throwable {
        trySetup();
        agents.get(name).login();
    }

    @When("^(\\w+) logs in and goes available$")
    public void agent_logs_in_and_goes_available(String name) throws Throwable {
        trySetup();
        agents.get(name).loginAndGoAvailable();
    }

    @When("^caller (\\d+) calls line (\\d+)$")
    public void caller_calls_line(int id, int line) throws Throwable {
        trySetup();
        callers.get(id).callLine(line);
    }

    @Then("^(\\w+)'s phone does not ring.*$")
    public void agent_s_phone_does_not_ring(String name) throws Throwable {
        assertFalse(agents.get(name).phoneHasRung());
    }

    @Then("^(\\w+)'s phone rings$")
    public void agent_s_phone_rings(String name) throws Throwable {
        assertTrue(agents.get(name).phoneHasRung());
    }

    @When("^(\\w+) answers the call$")
    public void agent_answers_the_call(String name) throws Throwable {
        trySetup();
        assertTrue(agents.get(name).phoneHasRung());
        agents.get(name).answer();
    }

    @When("^caller (\\d+) hangs up$")
    public void caller_hangs_up(int id) throws Throwable {
        trySetup();
        callers.get(id).hangUp();
    }

    @When("^(\\w+) wraps up$")
    public void agent_wraps_up(String name) throws Throwable {
        trySetup();
        agents.get(name).wrapUp();
    }

    @And("^(\\w+) goes released$")
    public void agent_goes_released(String name) throws Throwable {
        agents.get(name).goReleased();
    }

    @And("^(\\w+) goes available$")
    public void agent_goes_available(String name) throws Throwable {
        agents.get(name).goAvailable();
    }

    public static class ConfigAgent {
        String name;
        int login;
        String group;
        String skills;
        String securityLevel;
    }

    public static class ConfigQueue {
        String name;
        String client;
        int line;
        String group;
        String skills;
    }

    private void trySetup() {
        if (!isSetUp) {
            setup.setUp();
            isSetUp = true;
        }
    }

}
