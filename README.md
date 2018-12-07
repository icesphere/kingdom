# kingdom

## Developing the code

I recommend using IntelliJ Community Edition

### Run the code
Open terminal and cd to the project base directory and run:

`mvn spring-boot:run`

or if you want to debug - create a remote connection in IntelliJ to port 5005 and run: 

`mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"`

Once the code is running if you do a build in IntelliJ, Spring will automatically reload your changes

### Play the game
Open your web browser to localhost:8080

For other devices, look up your local ip address and have them connect to that local ip address on port 8080

### Start the game on a different computer
Open terminal and cd to the project base directory and run:

`mvn install`

This will create a jar file in the target directory

The jar file contains everything needed to play the game, so simply double click on the jar file and wait for it to load up and then you can play the game the same as if you had started it up from the commandline

