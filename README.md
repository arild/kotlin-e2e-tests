## Spring Boot End-To-End Testing
Demo invoicing application showing end-to-end testing with: [Spring Boot](https://spring.io/projects/spring-boot), [Testcontainers](https://www.testcontainers.org/), [Kotlin](https://kotlinlang.org/) and [KoTest](https://kotest.io/). Application receives data over Kafka and triggers an invoice export over REST.

### How to run
`./gradlew test` to run tests

### Tests
* `example1/InvoiceExportTest.kt`: Pure end-to-end test sending messages on kafka and triggering invoice export via REST
* `example2/InvoiceExportDatabaseTest.kt`: End-to-end using repository to assert on data in database
* `example3/InvoiceExportTimeTest.kt`: End-to-end test controlling time during the test
* `example4/InvoiceExportTest.kt`: End-to-end test verifying rollback of transaction
* `model/OrderRepositoryTest.kt`: Database integration test for repository (no kafka container)
