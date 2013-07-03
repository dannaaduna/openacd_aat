package bdd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ezuce.openuc.tester.setup.SimpleOpenACDSetUp;
import com.ezuce.openuc.tester.setup.model.AgentSecurity;
import com.ezuce.openuc.tester.setup.model.Recipe;
import com.ezuce.openuc.tester.setup.model.RecipeActionType;
import com.ezuce.openuc.tester.setup.model.RecipeCriterion;
import com.ezuce.openuc.tester.setup.model.RecipeCriterionComparator;
import com.ezuce.openuc.tester.setup.model.RecipeCriterionType;
import com.ezuce.openuc.tester.setup.model.RecipeRunFrequency;
import com.ezuce.openuc.tester.setup.model.RecipesBuilder;

import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * Created with IntelliJ IDEA. User: danna Date: 6/26/13 Time: 2:52 PM
 */
public class RoutingStepDefs {
	private static final int SETUP_WAIT_MS = 60000;
	private static final String domain = "openucrpm.ezuce.ph";
	private final Logger logger = LoggerFactory
			.getLogger(RoutingStepDefs.class);

	private boolean isSetUp;
	private static SimpleOpenACDSetUp setup;

	private HashMap<String, TestAgent> agents = new HashMap<String, TestAgent>();
	private HashMap<Integer, TestCaller> callers = new HashMap<Integer, TestCaller>();

	static {
		try {
			setup = new SimpleOpenACDSetUp(new File(
					"/Users/jvliwanag/tmpvm/openuc-4.6-stable-rpm"), domain,
					"password", new URL("https://" + domain + "/"));

//			setup.setResetSipxEnabled(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Before
	public void do_before() {
		isSetUp = false;
//		 isSetUp = true;
		TestManager.getInstance().setUp(domain); // TODO should only be done
													// once
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
		for (ConfigAgent a : configAgents) {
			TestAgent agent = new TestAgent(a.login);
			String[] skills = toStrArray(a.agentSkills);

			agents.put(a.name, agent);
			setup.addAgent(Integer.toString(a.login), a.group,
					AgentSecurity.valueOf(a.securityLevel.toUpperCase()),
					skills);
		}

	}

	@Given("^the queues:$")
	public void given_queues(List<ConfigQueue> configQueues) throws Throwable {
		for (ConfigQueue q : configQueues) {
			String[] skills = toStrArray(q.queueSkills);
			setup.addClientQueueLine(q.client, q.name,
					Integer.toString(q.line), q.group, skills);
		}
	}

	public static class ConfigQueueRecipe {
		String queue;
		String client;
		int line;
		String group;
		String queueSkills;

		String criteria;
		String action;
	}

	@Given("^the queues and recipes:$")
	public void given_recipes(List<ConfigQueueRecipe> configQRecipes)
			throws Throwable {
		Pattern c = Pattern
				.compile("(.*) (is|is greater than|is less than) (.*)");
		Pattern a = Pattern.compile("(.*): (.*)");

		for (ConfigQueueRecipe q : configQRecipes) {
			 String[] skills = toStrArray(q.queueSkills);

			String criteriaStr = q.criteria;
			String actionStr = q.action;
			Matcher m = c.matcher(criteriaStr);
			m.matches();

			RecipeCriterionType criterionType = null;
			RecipeCriterionComparator comp = null;
			String value = m.group(3);
			switch (m.group(1)) {
			case "Agents Eligible":
				criterionType = RecipeCriterionType.AGENTS_AVAILABLE;
				// TODO convert to AGENTS_ELIGIBLE
				break;
			case "Tick Interval":
				criterionType = RecipeCriterionType.TICK_INTERVAL;
				break;
			}
			switch (m.group(2)) {
			case "is":
				comp = RecipeCriterionComparator.IS_EQ;
				break;
			}

			RecipeCriterion criterion = new RecipeCriterion(criterionType,
					comp, value);
			ArrayList<RecipeCriterion> criteria = new ArrayList<RecipeCriterion>();
			criteria.add(criterion);

			m = a.matcher(actionStr);
			m.matches();

			RecipeActionType actionType = null;
			RecipeRunFrequency runFreq = RecipeRunFrequency.RUN_ONCE;
			// TODO set in config
			String[] target = new String[1];
			target[0] = m.group(2);
			// TODO allow array of targets
			switch (m.group(1)) {
			case "Remove Skills":
				actionType = RecipeActionType.REMOVE_SKILLS;
			case "Add Skills":
				actionType = RecipeActionType.ADD_SKILLS;
			}
			Recipe recipe = new Recipe(criteria, actionType, runFreq, target);
			ArrayList<Recipe> recipes = new ArrayList<Recipe>();
			recipes.add(recipe);

			setup.addClientQueueLine(q.client, q.queue,
					Integer.toString(q.line), q.group, recipes, skills);

		}
	}

	@Given("^the callers (\\d+)-(\\d+)$")
	public void given_callers(int callerFrom, int callerTo) throws Throwable {
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

	@When("^(\\w+) logs in and goes available after ~(\\d+) seconds$")
	public void agent_goes_available_after(String name, int delay)
			throws Throwable {
		trySetup();
		int delayMs = (delay + 1) * 1000;
		logger.info("Waiting for {} before logging in agent", delayMs);
		Thread.sleep(delayMs);
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

	@When("^(\\w+) rejects the call$")
	public void agent_rejects_the_call(String name) throws Throwable {
		trySetup();
		// assertTrue(agents.get(name).phoneHasRung());
		agents.get(name).endCall();
	}

	@When("^caller (\\d+) hangs up$")
	public void caller_hangs_up(int id) throws Throwable {
		trySetup();
		Thread.sleep(2000);
		callers.get(id).hangUp();
	}

	@When("^(\\w+) hangs up$")
	public void agent_hangs_up(String name) throws Throwable {
		trySetup();
		Thread.sleep(2000);
		agents.get(name).endCall();
	}

	@When("^(\\w+) wraps up$")
	public void agent_wraps_up(String name) throws Throwable {
		trySetup();

		agents.get(name).wrapUp();
	}

	@When("^(\\w+) tries to wrap up$")
	public void agent_tries_to_wrap_up(String name) throws Throwable {
		trySetup();

		// TODO SHOULD NOT CATCH THIS!!!
		try {
			agents.get(name).wrapUp();
		} catch (Exception e) {
			logger.error("ERROR: ASSUMED WRAPPED UP!");
		}
	}

	@When("^(\\w+) goes released$")
	public void agent_goes_released(String name) throws Throwable {
		trySetup();

		agents.get(name).goReleased();
	}

	@When("^(\\w+) goes available$")
	public void agent_goes_available(String name) throws Throwable {
		trySetup();

		agents.get(name).goAvailable();
	}

	@Given("^the agent groups:$")
	public void given_agent_groups(List<ConfigGroup> agentGroups)
			throws Throwable {
		for (ConfigGroup group : agentGroups) {
			String[] skills = toStrArray(group.skills);

			setup.addAgentGroup(group.name, skills);
		}
	}

	@Given("^the queue groups:$")
	public void given_queue_groups(List<ConfigGroup> queueGroups)
			throws Throwable {
		for (ConfigGroup group : queueGroups) {
			String[] skills = toStrArray(group.skills);

			setup.addQueueGroup(group.name, skills);
		}
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
			if (setup.setUp()) {
				logger.info("Waiting {}ms for everything to load",
						SETUP_WAIT_MS);
				Thread.sleep(SETUP_WAIT_MS);
			}
			isSetUp = true;
		}
	}

	private String[] toStrArray(String commaSepStr) {
		if (!commaSepStr.isEmpty()) {
			return commaSepStr.split(", *");
		} else {
			return new String[0];
		}
	}
}
