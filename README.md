## Spring Boot End-To-End Testing
Demo invoicing application showing end-to-end testing with: [Spring Boot](https://spring.io/projects/spring-boot), [Testcontainers](https://www.testcontainers.org/), [Kotlin](https://kotlinlang.org/) and [KoTest](https://kotest.io/). Application receives data over Kafka, data is stored in Postgres, and invoice export is triggered via REST.

### How to run
`./gradlew test` to run tests

### Tests
* `example1/InvoiceExportTest.kt`: Pure end-to-end test sending messages on kafka and triggering invoice export via REST
* `example2/InvoiceExportDatabaseTest.kt`: End-to-end using repository to assert on data in database
* `example3/InvoiceExportTimeTest.kt`: End-to-end test controlling time during the test
* `example4/InvoiceExportTest.kt`: End-to-end test verifying rollback of transaction
* `model/OrderRepositoryTest.kt`: Database integration test for repository (no kafka container)

### Resources
* Testing of microservices at Spotify: https://engineering.atspotify.com/2018/01/testing-of-microservices
* The Practical Test Pyramid: https://martinfowler.com/articles/practical-test-pyramid.html
* Excellent book on software testing: [Growing Object-Oriented Software, Guided by Tests](https://www.amazon.com/Growing-Object-Oriented-Software-Guided-Tests/dp/0321503627)
* Slides related to demo application: https://docs.google.com/presentation/d/1P6uKXtLEjXz7LV2tzBbR07TJKaG3o0nIV0hxulwCnz4
