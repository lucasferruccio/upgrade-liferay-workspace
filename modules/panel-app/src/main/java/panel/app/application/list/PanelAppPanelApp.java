package panel.app.application.list;

import panel.app.constants.PanelAppPanelCategoryKeys;
import panel.app.constants.PanelAppPortletKeys;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.portal.kernel.model.Portlet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + PanelAppPanelCategoryKeys.CONTROL_PANEL_CATEGORY
	},
	service = PanelApp.class
)
public class PanelAppPanelApp extends BasePanelApp {

	@Override
	public String getPortletId() {
		return PanelAppPortletKeys.PANELAPP;
	}

	@Reference(
		target = "(javax.portlet.name=" + PanelAppPortletKeys.PANELAPP + ")"
	)
	private Portlet _portlet;

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

}