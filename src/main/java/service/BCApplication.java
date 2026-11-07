package service;


import io.dropwizard.Application;
import io.dropwizard.configuration.EnvironmentVariableSubstitutor;
import io.dropwizard.configuration.SubstitutingSourceProvider;
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
        // resolve ${ENV_VAR} references in the YAML (e.g. keystore path/password); fail if one is unset
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(), new EnvironmentVariableSubstitutor(true)));

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