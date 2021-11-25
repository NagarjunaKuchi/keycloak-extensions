package info.mosip.keycloak.provider;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.logging.Logger;
import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormActionFactory;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;

public class CustomRegistrationProfile implements FormAction,FormActionFactory {

	public static final String PROVIDER_ID = "registration-profile-action";
	Logger logger = Logger.getLogger(CustomRegistrationProfile.class);
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FormAction create(KeycloakSession session) {	
		logger.info("Inside the create" + session);
		return new CustomRegistrationProfile();
	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {		
		
	}

	@Override
	public String getId() {
		logger.info("Inside the getId" + PROVIDER_ID);
		return PROVIDER_ID;
	}

	@Override
	public String getDisplayType() {
		return "PMS Profile Validation";
	}

	@Override
	public String getReferenceCategory() {
		return null;
	}

	@Override
	public boolean isConfigurable() {
		// TODO Auto-generated method stub
		return true;
	}

	private static AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.DISABLED
    };
	
	@Override
	public Requirement[] getRequirementChoices() {	
		return REQUIREMENT_CHOICES;
	}

	@Override
	public boolean isUserSetupAllowed() {
		return true;
	}

	@Override
	public String getHelpText() {
		return "Validates email, first name, and last name attributes and stores them in user data.";
	}

	@Override
	public List<ProviderConfigProperty> getConfigProperties() {
		return null;
	}
	
	@Override
	public void buildPage(FormContext context, LoginFormsProvider form) {
		//
	}

	@Override
	public void validate(ValidationContext context) {
		context.success();
	}

	@Override
	public void success(FormContext context) {
		logger.info("Entering the success" + PROVIDER_ID);		
		UserModel user = context.getUser();		
		MultivaluedMap<String, String> formData =   context.getHttpRequest().getDecodedFormParameters();
        RoleModel role = context.getRealm().getRole(formData.getFirst("user.attributes.partnerType"));
        logger.info("leaving the success" + role.getId());
        user.grantRole(role);
	}

	@Override
	public boolean requiresUser() {
		return false;
	}

	@Override
	public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
		return false;
	}

	@Override
	public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(org.keycloak.Config.Scope config) {
		// TODO Auto-generated method stub
		
	}

}
