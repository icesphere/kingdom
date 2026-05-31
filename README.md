# kingdom

## Developing the code

I recommend using IntelliJ Community Edition

### Run the code
Open terminal and cd to the project base directory and run:

`./gradlew bootRun`

or if you want to debug - create a remote connection in IntelliJ to port 5005 and run:

`./gradlew bootRun --debug-jvm`

Once the code is running if you do a build in IntelliJ, Spring will automatically reload your changes

### Play the game
Open your web browser to localhost:8080

For other devices, look up your local ip address and have them connect to that local ip address on port 8080

### Start the game on a different computer
Open terminal and cd to the project base directory and run:

`./gradlew bootJar`

This will create a jar file in the `build/libs` directory

The jar file contains everything needed to play the game, so simply double click on the jar file and wait for it to load up and then you can play the game the same as if you had started it up from the commandline

## Deploying to Render

This project includes `Dockerfile.render` and `render.yaml` for Render. The Dockerfile uses a non-default name so Railway does not auto-detect it and can continue using the existing Railway build configuration.

To deploy with the Blueprint:

1. Push this repository to GitHub.
2. In Render, create a new Blueprint from the repository.
3. Render will use `render.yaml` to create a Docker web service on the free plan.

To deploy manually instead:

1. In Render, create a new Web Service from the repository.
2. Set the runtime/language to Docker.
3. Set the Dockerfile path to `./Dockerfile.render`.
4. Set the health check path to `/ping.html`.
