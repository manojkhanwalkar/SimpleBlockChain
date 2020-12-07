package service;


import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;


public class BCApplication extends Application<BCConfiguration> {
    public static void main(String[] args) throws Exception {
        new BCApplication().run(args);
    }

    @Override
    public String getName() {
        return "Slacker Application";
    }

    @Override
    public void initialize(Bootstrap<BCConfiguration> bootstrap) {


    }

    @Override
    public void run(BCConfiguration configuration,
                    Environment environment) {
        final BCResource resource = new BCResource(
                configuration.getTemplate(),
                configuration.getDefaultName()
        );


        environment.jersey().register(resource);


}

}