package sso.integration.login;

import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectAuthenticationHandler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;

@Component(
	immediate = true,
	property = {
		"servlet-context-name=",
		"servlet-filter-name=Login Filter",
		"url-pattern=/c/portal/login"
	},
	service = Filter.class
)
public class LoginFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
		throws IOException, ServletException {

		try {
			HttpServletRequest request = (HttpServletRequest) servletRequest;
			HttpServletResponse response = (HttpServletResponse) servletResponse;

			//Get OpenId configurations
			List<OAuthClientEntry> oAuthClientEntries = oAuthClientEntryLocalService.
					getAuthServerWellKnownURISuffixOAuthClientEntries(
							CompanyThreadLocal.getCompanyId(), "openid-configuration");

			if (ListUtil.isEmpty(oAuthClientEntries)) {
				_log.warn("No OpenID Connect Providers found.");
				return;
			}

			OAuthClientEntry oAuthClientEntry = oAuthClientEntries.get(0);

			// Request Provider's authentication
			openIdConnectAuthenticationHandler.requestAuthentication(
					oAuthClientEntry.getOAuthClientEntryId(), request, response);
		}
		catch (Exception e) {
			_log.error("Error in KeycloakLoginFilter: " + e.getMessage(), e);
		}
		finally {
			filterChain.doFilter(servletRequest, servletResponse);
		}
	}

	@Override
	public void destroy() {
	}

	@Reference
	private OAuthClientEntryLocalService oAuthClientEntryLocalService;
	@Reference
	private OpenIdConnectAuthenticationHandler openIdConnectAuthenticationHandler;

	private static final Log _log = LogFactoryUtil.getLog(LoginFilter.class);
}