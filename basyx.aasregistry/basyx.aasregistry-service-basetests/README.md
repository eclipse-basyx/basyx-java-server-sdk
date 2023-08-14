# Basyx Asset Administration Registry Service Base Tests

This project offers test utility classes that you can use in your storage or integration tests.

We use an extra project here instead of *test-jar* maven artifact generation, as it is the [prefered way](https://maven.apache.org/plugins/maven-jar-plugin/examples/create-test-jar.html) of providing test artifacts.

Have a look at the mongoDb-storage project or the release projects to see how the abstract test classes defined here can be used. The classes provide good test coverage. You can extend them without writing additional test cases for your storage.

 Don't forget to start a docker daemon before running the tests and to run the docker-compose file of the parent folder.



