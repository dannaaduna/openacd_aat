Feature: Routing

  Background:
    Given the agents:
      | name  | login | group | skills              | security level |
      | Danna | 1079  | dev   | eng, fil, erl       | supervisor     |
      | Dean  | 1100  | admin | eng, fil, erl       | supervisor     |
      | Jeff  | 1101  | comm  | eng, fil, erl       | agent          |
      | Annie | 1102  | comm  | eng, fil, erl       | agent          |
      | Abed  | 1103  | comm  | eng, fil, erl       | agent          |
      | Troy  | 1104  | comm  | eng, fil, erl       | agent          |
      | Chang | 1105  | comm  | eng, fil, erl, span | agent          |
   Given the queues:
      | name  | client  | line  | group | skills |
      | q1    | cl1     | 98    | qg1   | erl    |
   Given the callers 200-206

   Scenario: Call is not routed to released agent
    When Dean logs in
    And Dean goes released
    When caller 200 calls line 98
    Then Dean's phone does not ring

   Scenario: Call is routed to available agent
    When Jeff logs in
    And Jeff goes available
    When caller 201 calls line 98
    Then Jeff's phone rings

  Scenario: Call is not routed to released agent
    When Dean logs in
    When caller 200 calls line 98
    Then Dean's phone does not ring # since Dean should be released

   Scenario: Two consecutive calls are routed to available agent
     When Annie logs in and goes available
     And caller 202 calls line 98
     And Annie answers the call
     And caller 202 hangs up
     And Annie wraps up
     And caller 203 calls line 98
     Then Annie's phone rings

  Scenario: No more calls are routed after agent goes released while on call
    When Abed logs in and goes available
    And caller 204 calls line 98
    And Abed answers the call
    And Abed goes released
    And caller 204 hangs up
    And caller 205 calls line 98
    Then Abed's phone does not ring

