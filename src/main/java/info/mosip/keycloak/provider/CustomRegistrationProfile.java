package info.mosip.keycloak.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.ws.rs.core.MultivaluedMap;

import org.keycloak.authentication.FormAction;
import org.keycloak.authentication.FormActionFactory;
import org.keycloak.authentication.FormContext;
import org.keycloak.authentication.ValidationContext;
import org.keycloak.events.Details;
import org.keycloak.events.Errors;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.FormMessage;
import org.keycloak.provider.ProviderConfigProperty;

//import jakarta.ws.rs.core.MultivaluedMap;

public class CustomRegistrationProfile implements FormAction, FormActionFactory {

	public static final String PROVIDER_ID = "registration-profile-action";
	private static final Pattern EMAIL_PATTERN = Pattern
			.compile("[a-zA-Z0-9!#$%&'*+/=?^_`{|}~.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*");
//	Logger logger = Logger.getLogger(CustomRegistrationProfile.class);

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public FormAction create(KeycloakSession session) {
		//logger.info("Inside the create" + session);
		return new CustomRegistrationProfile();
	}

	@Override
	public void postInit(KeycloakSessionFactory factory) {

	}

	@Override
	public String getId() {
		//logger.info("Inside the getId" + PROVIDER_ID);
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
			AuthenticationExecutionModel.Requirement.REQUIRED, AuthenticationExecutionModel.Requirement.DISABLED };

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
		//logger.info("Inside user validation");
		MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
		List<FormMessage> errors = new ArrayList<>();

		context.getEvent().detail(Details.REGISTER_METHOD, "form");
		String eventError = Errors.INVALID_REGISTRATION;

		if (formData.getFirst("user.attributes.partnerType").isBlank()) {
			errors.add(new FormMessage("user.attributes.partnerType", "Please specify partnerType"));
		}

		if (formData.getFirst("user.attributes.phoneNumber").isBlank()) {
			errors.add(new FormMessage("user.attributes.phoneNumber", "Please specify phoneNumber"));
		}

		if (formData.getFirst("user.attributes.organizationName").isBlank()) {
			errors.add(new FormMessage("user.attributes.organizationName", "Please specify organizationName"));
		}

		if (formData.getFirst("user.attributes.address").isBlank()) {
			errors.add(new FormMessage("user.attributes.address", "Please specify address"));
		}
		
		if (formData.getFirst("username").isBlank()) {
			errors.add(new FormMessage("username", "Please specify username"));
		}	
		

		if (formData.getFirst("email").isBlank()) {
			errors.add(new FormMessage("email", "Please specify email"));
		} else if (!EMAIL_PATTERN.matcher(formData.getFirst("email")).matches()) {
			errors.add(new FormMessage("email", "Please specify valid email"));
		}else if (context.getSession().users().getUserByEmail(formData.getFirst("email"), context.getRealm()) != null) {
			errors.add(new FormMessage("email", "email already exists"));
		} 

		if (isInputStringContainsSpaces(formData.getFirst("username"))) {
			errors.add(new FormMessage("user.attributes.username", "Username should not contain spaces"));
		}		

		if (errors.size() > 0) {
			context.error(eventError);
			context.validationError(formData, errors);
			return;

		} else {
			context.success();
		}
	}

	/**
	 * 
	 * @param inputString
	 * @return
	 */
	private boolean isInputStringContainsSpaces(String inputString) {
		if (inputString.matches(".*\\s.*")) {
			return true;
		}
		return false;
	}

	@Override
	public void success(FormContext context) {
		//logger.info("Entering the success" + PROVIDER_ID);
		UserModel user = context.getUser();
		MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
		RoleModel role = context.getRealm().getRole(formData.getFirst("user.attributes.partnerType"));
		//logger.info("leaving the success" + role.getId());
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
