Feature: Recipes

  # Recipe: On agents available = 0, remove skill
  Scenario: Call is not routed when agents available > 0
    When Dean logs in and goes available
    And caller 200 calls line 90
    Then Dean's phone does not ring

  Scenario: Call is routed when agents available = 0
    When caller 200 calls line 90
    And Dean logs in and goes available
    Then Dean's phone rings

  # Recipe: On tick interval 5, remove skill
  Scenario: Call is not routed before five seconds
    When caller 200 calls line 91
    And Dean logs in and goes available
    Then Dean's phone does not ring

  Scenario: Call is routed after five seconds
    When caller 200 calls line 91
    And Dean logs in and goes available
    Then Dean's phone rings

  # Recipe: On tick interval 5, add skill
  Scenario: Call is not routed before five seconds
    When caller 200 calls line 92
    And Dean logs in and goes available after 5 seconds
    Then Dean's phone rings

  Scenario: Call is not routed not after 5 seconds
    When caller 200 calls line 93
    And Dean logs in and goes available after 5 seconds
    Then Dean's phone does not ring
