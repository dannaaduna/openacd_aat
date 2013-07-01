Feature: Routing

  Background:
    Given the agent groups:
      | name  | skills  |
      | fun   | haskell |
      | oop   | java    |
      | multi | cpp     |

    Given the agents:
      | name  | login | security level | group  | agent skills |
      | Dean  | 1100  | agent          | fun    |              |
      | Jeff  | 1101  | agent          | fun    | erlang       |
      | Annie | 1102  | agent          | fun    | erlang       |
      | Abed  | 1103  | agent          | fun    | erlang       |

      | Troy  | 1104  | agent          | multi  |              |
      | Chang | 1105  | agent          | multi  |              |

    # Dean is unqualified to take calls requiring the skill erlang
    # Jeff, Annie and Abed are all qualified to take calls requiring the skill erlang

    Given the queue groups:
      | name  | skills  |
      | fun   | haskell |
      | oop   | java    |
      | multi | cpp     |

    Given the queues:
      | name      | client  | line  | group | queue skills  |
      | ejabberd  | client1 | 90    | fun   | erlang        |
      | darcs     | client1 | 91    | fun   | haskell       |

      | mongo     | client1 | 92    | multi | cpp           |
      | cucumber  | client1 | 93    | multi | ruby          |

    # Calls to line 90/queue ejabberd will require the skill erlang
    # Calls to line 91/queue darcs will require the skill haskell

   Given the callers 200-220

   # Agent is unqualified

   Scenario: Call is not routed to released and unqualified agent
    When Dean logs in
    And Dean goes released
    When caller 200 calls line 90
    Then Dean's phone does not ring

   Scenario: Call is not routed to available but unqualified agent
    When Dean logs in
    And Dean goes available
    When caller 201 calls line 90
    Then Dean's phone does not ring

   # Agent is qualified

   Scenario: Call is not routed to qualified but released agent
    When Jeff logs in
    And Jeff goes released
    When caller 202 calls line 90
    Then Jeff's phone does not ring

   Scenario: Call is not routed to qualified but released (by default) agent
    When Jeff logs in
    When caller 203 calls line 90
    Then Jeff's phone does not ring # since Jeff is released by default

   Scenario: Call is routed to qualified and available agent
    When Jeff logs in
    And Jeff goes available
    When caller 204 calls line 90
    Then Jeff's phone rings

   # Multi-agent/multi-call

   Scenario: Two consecutive calls are routed to available agent
     When Annie logs in and goes available
     And caller 205 calls line 90
     And Annie answers the call
     And caller 205 hangs up
     And Annie wraps up
     And caller 206 calls line 90
     Then Annie's phone rings

  Scenario: No more calls are routed after agent goes released while on call
    When Abed logs in and goes available
    And caller 207 calls line 90
    And Abed answers the call
    And Abed goes released
    And caller 207 hangs up
    And Abed wraps up
    And caller 208 calls line 90
    Then Abed's phone does not ring

  Scenario: Rejected call is routed to another qualified agent
    When Jeff logs in and goes available
    And caller 211 calls line 90
    Then Jeff's phone rings
    When Jeff rejects the call
    And Annie logs in and goes available
    Then Annie's phone rings

#  Inheritance of group skills

  Scenario: Agent inherits agent group skills
    When Dean logs in and goes available
    And caller 209 calls line 91
    Then Dean's phone rings

  Scenario: Queue inherits queue group skill requirements
    When Troy logs in and goes available
    And caller 210 calls line 92
    Then Troy's phone rings

