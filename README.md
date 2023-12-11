# zafin test

## libraries used
- jdk 17
- selenium-java 4.4.0
- selenium-http-jdk-client 4.8.1
- testng 7.8.0
- log4j-core 2.22.0
- log4j-api 2.22.0
- allure-testng 2.25.0

## logging
log42j xml file is located at `src/test/resources/log4j2.xml`

## parallel tests
- All tests are triggered from xml file located at `src/test/testng.xml`
- All tests are executed in parallel.

## reporting
* surefire reports will be located at `target/surefire-reports`
* allure results will be located at `allure-results` and `target/allure-results`
* allure reports will be located at `target/site/allure-maven-plugin/index.html`
* allure properties file located at `src/test/resources/allure.properties`

## test execution

`clean test install allure:report`