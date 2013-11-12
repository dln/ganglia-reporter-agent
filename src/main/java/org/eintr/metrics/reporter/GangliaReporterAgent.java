package org.eintr.metrics.reporter;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.HashSet;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricPredicate;
import com.yammer.metrics.reporting.GangliaReporter;


public class GangliaReporterAgent {

    private static final String GANGLIA_HOST = "localhost";
    private static final String GANGLIA_PREFIX = "";
    private static final int GANGLIA_PORT = 8649;
    private static final long REFRESH_PERIOD = 10;
    // Check TimeUnit javadoc for other possible values here like TimeUnit.MINUTES, etc
    private static final TimeUnit REFRESH_PERIOD_UNIT = TimeUnit.SECONDS;

    static MetricPredicate CASS_PREDICATE = new MetricPredicate() {
        // List of CFs to ignore
        private HashSet<String> ignore_cfs = new HashSet<String>(Arrays.asList(
            "system",
            "system_auth",
            "system_traces"
        ));

        @Override
        public boolean matches(MetricName name, Metric metric) {
            if (name.getType().equals("Connection") ||
                    (name.getType().equals("ColumnFamily") &&
                        ignore_cfs.contains(name.getScope().split("\\.")[0])) ||
                    (name.getType().equals("Streaming") && name.hasScope())) {
                return false;
            }
            return true;
        }
    };

    public static void premain(String agentArgs) throws Exception {
        GangliaReporter.enable(Metrics.defaultRegistry(), REFRESH_PERIOD, REFRESH_PERIOD_UNIT, GANGLIA_HOST, GANGLIA_PORT, GANGLIA_PREFIX, CASS_PREDICATE);
    }
}
