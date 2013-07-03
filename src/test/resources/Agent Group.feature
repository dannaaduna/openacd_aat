Feature: Agent Group

  Background: 
    Given the agent groups:
      | name  | skills  |
      | fun   | haskell |
    
    Given the agents:
      | name  | login | security level | group | agent skills |
      | Dean  | 1100  | agent          | fun   |              |
      | Jeff  | 1101  | agent          | fun   | erlang       |

    Given the queue groups:
      | name  | skills  |
      | fun   | haskell |

    Given the queues:
      | name     | client  | line | group | queue skills |
      | darcs    | client1 | 91   | fun   |              |

    # Calls to line 91/queue darcs will require the skill haskell, which is
    # all agents from agent group fun possess
    
    Given the callers 212-213
    
  Scenario: Agent inherits agent group skills
    When Dean logs in and goes available
    And caller 212 calls line 91
    Then Dean's phone rings
 
  Scenario: Agent with own skills inherits agent group skills
    When Jeff logs in and goes available
    And caller 213 calls line 91
    Then Jeff's phone rings