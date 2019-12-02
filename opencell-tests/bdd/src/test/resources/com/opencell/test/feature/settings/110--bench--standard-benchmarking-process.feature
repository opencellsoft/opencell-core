Feature: Becnhmarking process

    The step used for a bench run.
    Followed by the benchmark job (#108).
    From CDR production to invoicing.

    Scenario: Step for a complete benchmark
        Given   The benchmark job has setup the benchmark offer
          And   The benchmark job has created the accounts, subscriptions, access points as mentionned in the job description
          And   The benchmark job is passed the "MONTH" on which to run the process
          And   All jobs are configured with verboseReport off
          And   All jobs are configured with maximum parallelization
        When    The process is executed by the benchmark job
        Then    The benchmark job generates the CDR files for current month according the job configuration
          And   The benchmark job runs the MediationJob
          And   The benchmark job runs the UsageRatingJob for ratingGroup "OC_BENCHMARK", 
          And   The benchmark job runs the RecurringRatingJob for ratingGroup "OC_BENCHMARK"
          And   The benchmark job runs the RatedTransactionJob
          And   The benchmark job creates an automatic "BillingRun" for "OC_BENCHMARK" billing cycle
          And   The benchmark job runs the InvoicingJob (computes invoices) 
          And   The benchmark job validates the "BillingRun"
          And   The benchmark job runs the InvoicingJob (finalize invoices)
          And   Every step execution information has been added to the benchmark job report 
