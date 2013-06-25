Feature: Call routing

  In order to start processing calls
  As an agent
  I want to be able to log in and have calls routed to me

  Scenario: Call is not routed to released agent
    When agent 1100 logs in
    When caller 200 calls line 98
    Then agent 1100`s phone does not ring

  Scenario: Call is routed to available agent
    When agent 1101 logs in and goes available
    And caller 201 calls line 98
    Then agent 1101`s phone rings

  Scenario: Two consecutive calls are routed to available agent
    When agent 1102 logs in and goes available
    And caller 202 calls line 98
    And agent 1102 answers the call
    And caller 202 hangs up
    And agent 1102 wraps up
    And caller 203 calls line 98
    Then agent 1102`s phone rings

  Scenario: No more calls are routed after agent goes released while on call
    When agent 1103 logs in and goes available
    And caller 204 calls line 98
    And agent 1103 answers the call
    And agent 1103 goes released
    And caller 204 hangs up
    And caller 205 calls line 98
    Then agent 1103`s phone does not ring

  Scenario: Rejected call is routed to another agent
    When agents 1104 and 1105 log in and go available
    And caller 206 calls line 98
    Then either agent 1104 or 1105`s phone rings
    When the first agent rejects the call
    Then the second agent`s phone rings
