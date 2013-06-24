Feature: Call routing

  In order to start processing calls
  As an agent
  I want to be able to log in and have calls routed to me

  Scenario: Call is not routed to released agent
    When agent 1079 logs in
    When caller 200 calls line 98
    Then agent 1079`s phone does not ring

  Scenario: Call is routed to available agent
    When agent 1079 logs in and goes available
    And caller 200 calls line 98
    Then agent 1079`s phone rings

  Scenario: Two consecutive calls are routed to available agent
    When agent 1079 logs in and goes available
    And caller 200 calls line 98
    And caller 200 hangs up
    And caller 201 calls line 98
    Then agent 1079`s phone rings

  Scenario: No more calls are routed after agent goes released while on call
    When agent 1079 logs in and goes available
    And caller 200 calls line 98
    And agent 1079 goes released
    And caller 200 hangs up
    And caller 201 calls line 98
    Then agent 1079`s phone does not ring
