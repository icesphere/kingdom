# kingdom

## Developing the code

I recommend using IntelliJ Community Edition

### Run the code
Open terminal and cd to the project base directory and run:

`mvn spring-boot:run`

or if you want to debug - create a remote connection in IntelliJ to port 5005 and run: 

`mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"`

Once the code is running if you do a build in IntelliJ, Spring will automatically reload your changes
