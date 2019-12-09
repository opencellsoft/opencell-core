Feature: Standard Benchmarking job

    A job that rates the performance of the system.
    Several presets are available to handle different scenarios.
    The job returns detailed performance data and a global rating.

    Background: System is installed
        Given   The job is part of the standard Opencell install
          And   The "perf offer" is defined with a monthly recurring charge and a usage charge (#109)
          And   The system is available for testing (not being heavily used)
          And   There are no pending CDR file in .../import/metering/input
          And   There are no OPEN EDR, WalletOperation, RatedTransaction

    Scenario: Run a the perf job with custom parameters
        Given   No preset configuration is chosen
          And   Parameter "nbAccounts" is filled with a valid value (1+)
          And   Parameter "nbSubscriptions" is filled with a valid value (1+)
          And   Parameter "nbAccessPoints" is filled with a valid value (1+)
          And   Parameter "nbCDRs" is filled with a valid value (0+)
          And   Parameter "nbMonths" is filled with a valid value (1+)
        When    The job is Run
        Then    The job creates "nbAccounts"
          And   The job creates "nbSubscriptions" subscriptions to the "perf offer" per account
          And   The job creates "nbAccessPoints" access point per subscription
          And   The job activates the subscriptions with "rateUntilDate" set to "subscriptionDate"
          And   The job executes the benchmarking process (#110) with "nbCDRs" per access point "nbMonths" times starting with this year's first month
          And   The job keeps times for all steps
          And   The job stores the results in the database
          And   The job computes an overall performance rating
          And   The job cleans up everything it has created (cleanup time is also recorded)

    Scenario Outline: Run a preset configuration
        Given   A configuration preset <preset> is selected
        When    The job is Run
        Then    The job creates <nbAccounts>
          And   The job creates <nbSubscriptions> subscriptions to the <perf offer> per account
          And   The job creates <nbAccessPoints> access point per subscription
          And   The job activates the subscriptions with <rateUntilDate> set to <subscriptionDate>
          And   The job executes the benchmarking process (#110) with <nbCDRs> per access point <nbMonths> times starting with this year's first month
          And   The job keeps times for all steps
          And   The job stores the results in the database
          And   The job computes an overall performance rating
          And   The job cleans up everything it has created (cleanup time is also recorded)

        Examples:
            | preset   | nbAccounts | nbSubscriptions | nbAccessPoints | nbCDRs | nbMonths |
            | default  | 1000       | 1               | 1              | 1000   | 1        |
            | retail   | 100000     | 1               | 1              | 10     | 1        |
            | operator | 100        | 100             | 100            | 100    | 10       |

    Scenario: Produce a report from a job
        Given   On a perf job execution detail page
          And   No report has been produced yet
        When    I click on "generate report"
        Then    A PDF report is generated compiling the job run information

