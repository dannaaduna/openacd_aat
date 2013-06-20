Feature: Call routing

  In order to start processing calls
  As an agent
  I want to be able to log in and have calls routed to me

  Scenario: Call is routed to available agent
    When agent 1079 logs in and goes available
    And caller 200 calls line 98
    Then agent 1079's phone rings

  Scenario: Call is not routed to released agent
    When agent 1079 logs in
    When caller 200 calls line 98
    Then agent 1079's phone does not ring
