# kingdom

## Developing the code

I recommend using IntelliJ Community Edition

### Run the code
Open terminal and cd to the project base directory and run:

`./mvnw spring-boot:run`

or if you want to debug - create a remote connection in IntelliJ to port 5005 and run: 

`./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"`

Once the code is running if you do a build in IntelliJ, Spring will automatically reload your changes

### Play the game
Open your web browser to localhost:8080

For other devices, look up your local ip address and have them connect to that local ip address on port 8080

### Start the game on a different computer
Open terminal and cd to the project base directory and run:

`./mvnw install`

This will create a jar file in the target directory

The jar file contains everything needed to play the game, so simply double click on the jar file and wait for it to load up and then you can play the game the same as if you had started it up from the commandline

## Android app

Open the `androidApp` directory in Android Studio, or open the repository root and import the Gradle project from `androidApp`.

The Android app expects the web service to be running at `http://10.0.2.2:8080` when using the Android emulator. Start the service first with the `Kingdom Web Service - Run` run configuration or with:

`./mvnw spring-boot:run`

Build the debug APK from the repository root with:

`cd androidApp && ./gradlew :app:assembleDebug`

Deploy the debug app to a connected emulator or device with:

`cd androidApp && ./gradlew :app:installDebug`

Shared Android Studio run configurations are in `.run`:

`Kingdom Web Service - Run`

`Kingdom Web Service - Package`

`Kingdom Android - Assemble Debug`

`Kingdom Android - Install Debug`
