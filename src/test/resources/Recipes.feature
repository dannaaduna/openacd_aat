Feature: Recipes
<<<<<<< HEAD

  Given the agents:
    | name      | login  | group        | skills              | security level |
    | Shirley   | 1200   | recipe_test  | haskell, erlang     | agent          |
    | Britta    | 1201   | recipe_test  | erlang              | agent          |
    | Starburns | 1202   | recipe_test  | haskell             | agent          |

  Given the queues:
    | name        | client  | line  | group         | queue skills      |
    | haskerl     | client1 | 80    | recipe_test   | haskell, erlang   |
    | just_erl    | client1 | 81    | recipe_test   | erlang            |
    | just_hask   | client1 | 82    | recipe_test   | haskell           |

  Given the recipes:
    | queue    | criterion             | action                |
    | haskerl  | agents available is 0 | remove skill erlang   |
    | haskerl  | tick interval is 5    | remove skill erlang   |
    | just_erl | tick interval is 5    | add skill haskell     |

  Given the callers 300-305

  # Recipe: On agents available = 0, remove skill
  Scenario: Call is not routed when agents available > 0
    When Britta logs in and goes available
    And caller 300 calls line 80
    Then Britta's phone does not ring

  Scenario: Call is routed when agents available = 0
=======
  
  Background:
    Given the agents:
      | name      | login  | group        | agent skills        | security level |
      | Shirley   | 1200   | recipe_test  | haskell             | agent          |
      | Britta    | 1201   | recipe_test  | erlang              | agent          |
      | Pierce    | 1202   | recipe_test  | scala               | agent          |

    Given the queues and recipes:
      | queue       | client  | line  | group         | queue skills      | criteria              | action                  | 
      | haskerl     | client1 | 80    | recipe_test   | haskell, erlang   | Agents Eligible is 0  | Remove Skills: haskell  |
      | scalin      | client1 | 81    | recipe_test   | scala, kotlin     | Tick Interval is 5    | Remove Skills: kotlin   |
      | just_hask   | client1 | 82    | recipe_test   | haskell           | Tick Interval is 5    | Add Skills: erlang      |
  
    Given the callers 300-305

  # Recipe: On agents available = 0, remove skill
  Scenario: Call is not routed when initial agents eligible > 0
  	When Britta logs in and goes available
    And caller 300 calls line 80
    Then Britta's phone does not ring

  Scenario: Call is routed when initial agents eligible = 0
>>>>>>> Clean-up test, add recipe
    When caller 301 calls line 80
    And Britta logs in and goes available
    Then Britta's phone rings

  # Recipe: On tick interval 5, remove skill
  Scenario: Call is not routed before five seconds
<<<<<<< HEAD
    When caller 300 calls line 80
    And Britta logs in
    Then Britta's phone does not ring

  Scenario: Call is routed after five seconds
    When caller 300 calls line 80
    And Britta logs in after 5 seconds
    Then Britta's phone rings

  # Recipe: On tick interval 5, add skill
  Scenario: Call is routed before five seconds
    When caller 300 calls line 81
    And Br logs in
    Then Britta's phone rings

  Scenario: Call is not routed not after 5 seconds
    When caller 300 calls line 80
    And Britta logs in after 5 seconds
    Then Britta's phone does not ring







#  Scenario: Remove skill when agents available is 0
#    Given Danna logs in and goes available
#    When caller 200 calls line 90
#    Then Danna's phone rings

#
#  Scenario: Remove skill when tick interval is 3
#    When caller 200 calls line 98
#    And Danna logs in and goes available
#    And caller 200 waits in queue for at least 3 seconds
#    Then Danna's phone rings

#  Scenario:
#    When caller 200 calls line 99
#    And Danna logs in and goes available
#    And caller 200 waits in queue for at least 3 seconds
#    Then Danna's does not ring
#
#  Scenario: Add skill when tick interval is 3
#    When caller 200 calls line 98
#    And Danna logs in and goes available
#    And caller 200 waits in queue for at least 3 seconds
#    Then Danna's phone rings
#
#  Scenario:
#    When caller 200 calls line 99
#    And Danna logs in and goes available
#    And caller 200 waits in queue for at least 3 seconds
#    Then Danna's does not ring

=======
    When caller 302 calls line 81
    And Pierce logs in and goes available
    Then Pierce's phone does not ring

  Scenario: Call is routed after 5 seconds
    When caller 303 calls line 81
    And Pierce logs in and goes available after ~5 seconds
    Then Pierce's phone does not ring

  # Recipe: On tick interval 5, add skill
  Scenario: Call is routed before five seconds
    When caller 304 calls line 82
    And Shirley logs in and goes available
    Then Shirley's phone rings

  Scenario: Call is not routed not after 5 seconds
    When caller 305 calls line 82
    And Shirley logs in and goes available after ~5 seconds
    Then Shirley's phone does not ring
>>>>>>> Clean-up test, add recipe
