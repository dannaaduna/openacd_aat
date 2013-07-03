Feature: Queue Group

  Background: 
    Given the agent groups:
      | name  | skills  |
      | multi | cpp     |
    
    Given the agents:
      | name  | login | security level | group | agent skills |
      | Troy  | 1104  | agent          | multi |              |
      | Chang | 1105  | agent          | multi | ruby         |
    
    Given the queue groups:
      | multi | cpp     |
    
    Given the queues:
      | name     | client  | line | group | queue skills |
      | mongo    | client1 | 92   | multi |              |
      | cucumber | client1 | 93   | multi | ruby         |
    
    # Calls to line 92/queue mongo and line 93 will require the skill cpp,
    # which is inherited from the queue group multi
    
    Given the callers 214-215
    
  Scenario: Queue inherits queue group skill requirements
    When Troy logs in and goes available
    And caller 214 calls line 92
    Then Troy's phone rings
    
  Scenario: Queue requires additional skill requirements
  	When Troy logs in and goes available
    And caller 214 calls line 93
    Then Troy's phone does not ring
    When Chang logs in and goes available
    And caller 215 calls line 93
    Then Chang's phone rings
  