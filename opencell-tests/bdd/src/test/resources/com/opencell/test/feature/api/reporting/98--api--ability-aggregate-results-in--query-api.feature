@ignore
Feature: Allow to use "group by" in HQL query sent by API
    In order to be able to easily display aggregated data
    As an API user (typically the portal)
    I want to be able to group results returned by the /query API

    Background:
        Given the use of valid credentials
        And the use of valid URL
        And the Opencell server is running

    Scenario: with group by
        Given "groupBy" query parameter is set
        And the query is otherwise valid for "group by"
        When I call the GET /query API
        Then API execution is OK
        And the result is aggregated according to the "groupBy" parameter

    Scenario: without group by
        Given "groupBy" query parameter is not set
        And the query is otherwise valid for no "group by"
        When I call the GET /query API
        Then API execution is OK
        And the result is returned with no aggregation
