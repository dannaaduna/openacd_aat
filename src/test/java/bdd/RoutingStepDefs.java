package bdd;

import com.ezuce.openuc.tester.setup.SimpleOpenACDSetUp;
import com.ezuce.openuc.tester.setup.model.AgentSecurity;
import cucumber.api.java.*;
import cucumber.api.java.en.*;

import cucumber.runtime.PendingException;
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
    private static final String domain = "openucrpm.ezuce.ph";
    private final Logger logger = LoggerFactory
            .getLogger(RoutingStepDefs.class);

    private boolean isSetUp;
    private static SimpleOpenACDSetUp setup;

    private HashMap<String, TestAgent> agents = new HashMap<String, TestAgent>();
    private HashMap<Integer, TestCaller> callers = new HashMap<Integer, TestCaller>();

    private String firstAgent;
    private String secondAgent;

    static {
        try {
            setup = new SimpleOpenACDSetUp(new File(
                    "/Users/danna/dev/openuc-stable-rpm"),
                    domain,
                    "password",
                    new URL("https://" + domain + "/"));
            setup.setResetSipxEnabled(true);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void do_before() {
        isSetUp = false;
        TestManager.setUp(domain);
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
        agents = null;
        callers = null;
    }

    @Given("^the agents:$")
    public void given_agents(List<ConfigAgent> configAgents) throws Throwable {
        System.out.println("Given these agents");
        for (ConfigAgent a : configAgents) {
            TestAgent agent = new TestAgent(a.login);
            String[] skills = toStrArray(a.agentSkills);

            agents.put(a.name, agent);
            setup.addAgent(Integer.toString(a.login), a.group, AgentSecurity.valueOf(a.securityLevel.toUpperCase()), skills);
        }

    }

    @Given("^the queues:$")
    public void given_queues(List<ConfigQueue> configQueues) throws Throwable {
        for (ConfigQueue q : configQueues) {
            String[] skills = toStrArray(q.queueSkills);
            setup.addClientQueueLine(q.client, q.name, Integer.toString(q.line), q.group, skills);
        }
    }

    @Given("^the callers (\\d+)-(\\d+)$")
    public void given_callers(int callerFrom, int callerTo) throws Throwable {
        for (int i = callerFrom; i <= callerTo; i++) {
            callers.put(i, new TestCaller(i));
            setup.addCaller(Integer.toString(i));
        }
    }

    @When("^(\\w+) and (\\w+) log in and go available$")
    public void two_agents_log_in_and_go_available(String name1, String name2) throws Throwable {
        trySetup();
        agents.get(name1).loginAndGoAvailable();
        agents.get(name2).loginAndGoAvailable();
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

    @Then("^either (\\w+) or (\\w+)'s phone rings$")
    public void either_phone_rings(String name1, String name2) throws Throwable {
        boolean hasRung1 = agents.get(name1).phoneHasRung();
        boolean hasRung2 = agents.get(name2).phoneHasRung();
        assertTrue(hasRung1 ^ hasRung2);
        if (hasRung1) {
            firstAgent = name1;
            secondAgent = name2;
        }
        else {
            firstAgent = name2;
            secondAgent = name1;
        }
    }

    @Then("^the second agent's phone rings$")
    public void second_agent_s_phone_rings() throws Throwable {
        assertTrue(agents.get(secondAgent).phoneHasRung());
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

    @Given("^the agent groups:$")
    public void given_agent_groups(List<ConfigGroup> agentGroups) throws Throwable {
        for (ConfigGroup group : agentGroups) {
            String[] skills = toStrArray(group.skills);

            setup.addAgentGroup(group.name, skills);
        }
    }

    @Given("^the queue groups:$")
    public void given_queue_groups(List<ConfigGroup> queueGroups) throws Throwable {
        for (ConfigGroup group : queueGroups) {
            String[] skills = toStrArray(group.skills);

            setup.addQueueGroup(group.name, skills);
        }
    }

    @When("^the first agent rejects the call$")
    public void first_agent_rejects_the_call() throws Throwable {
        agents.get(firstAgent).reject();
    }

    @When("^(\\w+) rejects the call$")
    public void agent_rejects_the_call(String name) throws Throwable {
        agents.get(name).reject();
    }

    public static class ConfigGroup {
        String name;
        String skills;
    }

    public static class ConfigAgent {
        String name;
        int login;
        String group;
        String agentSkills;
        String securityLevel;
    }

    public static class ConfigQueue {
        String name;
        String client;
        int line;
        String group;
        String queueSkills;
    }

    private void trySetup() throws Exception {
        if (!isSetUp) {
            setup.setUp();
            Thread.sleep(60000);
            isSetUp = true;
        }
    }

    private String[] toStrArray(String commaSepStr) {
        if (!commaSepStr.isEmpty()) {
            return commaSepStr.split(", *");
        }
        else {
            return new String[0];
        }
    }

}
