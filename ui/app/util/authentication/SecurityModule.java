package util.authentication;

import com.google.inject.AbstractModule;

import controllers.routes;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.pac4j.core.authorization.authorizer.RequireAnyRoleAuthorizer;
import org.pac4j.play.ApplicationLogoutController;
import org.pac4j.play.CallbackController;
import org.pac4j.http.client.indirect.FormClient;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import play.Configuration;
import play.Environment;
import play.Play;
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
		List<Client> clientList = new ArrayList<Client>();
		String baseUri = NemakiConfig.getApplicationBaseUri(configuration);

		FormClient formClient = new FormClient(baseUri + "login", new NemakiAuthenticator());
	    formClient.setUsernameParameter("userId");
	    clientList.add(formClient);

	    if (NemakiConfig.isEnableSamlSSO(configuration)){
		    SAML2ClientConfiguration cfg = new SAML2ClientConfiguration("resource:samlKeystore.jks", "pac4j-demo-passwd", "pac4j-demo-passwd", "resource:idp-metadata.xml");
		    cfg.setMaximumAuthenticationLifetime(3600);
		    cfg.setDestinationBindingType(SAMLConstants.SAML2_POST_BINDING_URI);
		    cfg.setServiceProviderEntityId(baseUri);
		    SAML2Client saml2Client = new SAML2Client(cfg);
		    clientList.add(saml2Client);
	    }
	    Clients clients = new Clients(baseUri + "callback",  clientList);

        final Config config = new Config(clients);
        config.addAuthorizer("admin", new RequireAnyRoleAuthorizer<>("ROLE_ADMIN"));
        config.setHttpActionAdapter(new NemakiHttpActionAdapter());
        bind(Config.class).toInstance(config);


        // callback
        final CallbackController callbackController = new CallbackController();
        callbackController.setDefaultUrl(baseUri);
        callbackController.setMultiProfile(false);
        bind(CallbackController.class).toInstance(callbackController);

        // logout
        final ApplicationLogoutController logoutController = new ApplicationLogoutController();
        logoutController.setDefaultUrl(baseUri + "?defaulturlafterlogout");
        bind(ApplicationLogoutController.class).toInstance(logoutController);

	}

}