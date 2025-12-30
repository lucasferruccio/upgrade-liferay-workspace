package scheduler.job.sample.listener;

import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.scheduler.*;
import com.liferay.portal.kernel.search.IndexWriterHelperUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.SearchException;

import java.util.Map;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import scheduler.job.sample.listener.configuration.SchedulerJobSampleConfiguration;

@Component(
		configurationPid = "scheduler.job.sample.listener.configuration.SchedulerJobSampleConfiguration",
		service = SchedulerJobConfiguration.class
)
public class SchedulerJobSample implements SchedulerJobConfiguration {

	@Activate
	protected void activate(Map<String, Object> properties) {
		_schedulerJobSampleConfiguration = ConfigurableUtil.createConfigurable(
				SchedulerJobSampleConfiguration.class, properties);
	}

	@Override
	public TriggerConfiguration getTriggerConfiguration() {
		return TriggerConfiguration.createTriggerConfiguration(
				_schedulerJobSampleConfiguration.checkJobInterval(),
				TimeUnit.MINUTE);
	}

	@Override
	public UnsafeRunnable<Exception> getJobExecutorUnsafeRunnable() {
		return () -> _log.info("Doing something cool");
	}

	private void testAutoReplacement() throws SearchException {
		long paramName1 = 1;
		Document paramName2 = null;

		IndexWriterHelperUtil.updateDocument(paramName1, paramName2);
	}

	private static void testSuggestion() throws SearchException {
		long paramName1 = 1;
		Supplier<Document> documentSupplier = () -> null;

		IndexWriterHelperUtil.updateDocument(paramName1, documentSupplier.get());
	}

	private volatile SchedulerJobSampleConfiguration _schedulerJobSampleConfiguration;

	private static final Log _log = LogFactoryUtil.getLog(SchedulerJobSample.class);

}