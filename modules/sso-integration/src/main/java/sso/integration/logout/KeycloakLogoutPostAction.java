package sso.integration.logout;

import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.liferay.portal.kernel.json.JSONFactoryUtil.createJSONObject;

@Component(
		immediate = true,
		property = "key=logout.events.post",
		service = LifecycleAction.class
)
public class KeycloakLogoutPostAction implements LifecycleAction {

	private static final Log LOG = LogFactoryUtil.getLog(KeycloakLogoutPostAction.class);

	@Override
	public void processLifecycleEvent(LifecycleEvent lifecycleEvent) {
		try {
			//Get OpenId configurations
			List<OAuthClientEntry> oAuthClientEntries = _oAuthClientEntryLocalService.
					getAuthServerWellKnownURISuffixOAuthClientEntries(
							CompanyThreadLocal.getCompanyId(), "openid-configuration");

			if (ListUtil.isEmpty(oAuthClientEntries)) {
				LOG.warn("No OpenID Connect Providers found.");
				return;
			}

			OAuthClientEntry oAuthClientEntry = oAuthClientEntries.get(0);

			OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
					_oAuthClientASLocalMetadataLocalService.getOAuthClientASLocalMetadata(
							oAuthClientEntry.getAuthServerWellKnownURI());

			JSONObject oidcJsonObject = createJSONObject(oAuthClientASLocalMetadata.getMetadataJSON());

			String logoutEndpoint = StringUtil.replaceLast(
					oidcJsonObject.getString("authorization_endpoint"), "/auth", "/logout");

			String logoutUrl = logoutEndpoint + "?redirect_uri=" + getRedirectUrl(lifecycleEvent.getRequest());

			lifecycleEvent.getResponse().sendRedirect(logoutUrl);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String getRedirectUrl(HttpServletRequest request) {
		return _portal.getPortalURL(request) + "/";
	}

	@Reference
	private Portal _portal;

	@Reference
	private OAuthClientASLocalMetadataLocalService _oAuthClientASLocalMetadataLocalService;

	@Reference
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;
}