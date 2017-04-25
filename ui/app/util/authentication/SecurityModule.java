package util.authentication;

import com.google.inject.AbstractModule;

import controllers.routes;

import java.io.File;

import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.play.ApplicationLogoutController;
import org.pac4j.play.CallbackController;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import play.Configuration;
import play.Environment;
import play.cache.CacheApi;
import util.NemakiConfig;

public class SecurityModule extends AbstractModule{
    private final Configuration configuration;
    private final Environment environment;

    public SecurityModule(final Environment environment, final Configuration configuration) {
        this.configuration = configuration;
        this.environment = environment;
    }

	@Override
	protected void configure() {

		String baseUri = "/";

		FormClient formClient = new FormClient(baseUri + "login", new NemakiAuthenticator());
	    formClient.setUsernameParameter("userId");
/*
	    SAML2ClientConfiguration cfg = new SAML2ClientConfiguration("resource:samlKeystore.jks",
	                    "pac4j-demo-passwd", "pac4j-demo-passwd", "resource:openidp-feide.xml");
	    cfg.setMaximumAuthenticationLifetime(3600);
	    cfg.setServiceProviderEntityId("urn:mace:saml:pac4j.org");
	    cfg.setServiceProviderMetadataPath(new File("target", "sp-metadata.xml").getAbsolutePath());
	    SAML2Client saml2Client = new SAML2Client(cfg);
*/
	    Clients clients = new Clients("callback", /* saml2Client, */ formClient);

        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer<>("ROLE_ADMIN"));
        config.setHttpActionAdapter(new NemakiHttpActionAdapter());
        bind(Config.class).toInstance(config);

        // callback
        final CallbackController callbackController = new CallbackController();
        callbackController.setDefaultUrl("/ui/");
        callbackController.setMultiProfile(true);
        bind(CallbackController.class).toInstance(callbackController);

        // logout
        final ApplicationLogoutController logoutController = new ApplicationLogoutController();
        logoutController.setDefaultUrl("/ui/?defaulturlafterlogout");
        bind(ApplicationLogoutController.class).toInstance(logoutController);

	}

}
