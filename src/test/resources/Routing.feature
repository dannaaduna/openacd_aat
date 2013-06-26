Feature: Routing

  Background:
    Given these agents:
      | name  | login | skills  |
      | Danna | 1100  | fil     |
      | Jeff  | 1101  | fil     |
      | Annie | 1102  | fil     |
      | Abed  | 1103  | fil     |
      | Troy  | 1104  | fil     |
      | Chang | 1105  | fil     |
   Given callers 200-206

   Scenario: Call is not routed to released agent
    When Danna logs in
    When caller 200 calls line 98
    Then Danna's phone does not ring

   Scenario: Call is routed to available agent
    When Jeff logs in and goes available
    When caller 201 calls line 98
    Then Jeff's phone rings

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
    Then Abed's phone rings

