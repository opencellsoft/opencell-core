package org.meveo.admin.web.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;

import org.eclipse.microprofile.metrics.Histogram;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.MetadataBuilder;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.Timer;
import org.eclipse.microprofile.metrics.annotation.RegistryType;
import org.meveo.cache.MetricsConfigurationCacheContainerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebFilter(urlPatterns = "/*")
public class RequestMonitoringFilter extends HttpFilter {

    private Logger log = LoggerFactory.getLogger(RequestMonitoringFilter.class);

    @Inject
    @RegistryType(type = MetricRegistry.Type.APPLICATION)
    MetricRegistry registry;

    @Inject
    MetricsConfigurationCacheContainerProvider metricsConfigCache;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        long millis = System.currentTimeMillis();
        chain.doFilter(req, res);
        registerMetricsForRequest(req, millis);
    }

    private void registerMetricsForRequest(HttpServletRequest req, long millis) {
        try {
            if (req.getRequestURI() != null) {
                String uri = req.getRequestURI();
                String contextPath = req.getContextPath();
                String name = uri.replaceAll(contextPath, "");
                if (!Strings.isNullOrEmpty(name)) {
                    String nameWithOutExtension = name.replaceAll(".jsf", "");
                    Map<String, Map<String, String>> params = new HashMap<>();
                    if (metricsConfigCache.containsKey(nameWithOutExtension)) {
                        params = metricsConfigCache.getConfiguration(nameWithOutExtension);
                    } else if (name.startsWith("/pages") && name.endsWith(".jsf") && metricsConfigCache.containsKey("*.jsf")) {
                        params = metricsConfigCache.getConfiguration("*.jsf");
                    }
                    if (params.containsKey(req.getMethod())) {
                        Map<String, String> metrics = params.get(req.getMethod());
                        String metric_type = metrics.get("metric_type");
                        String unit = metrics.get("metric_unit");

                        log.info("Register {} metrics for {} in {}", metric_type, req.getMethod(), name);
                        registerMetricForMethod(name, metric_type, millis, unit);
                    }
                } else {
                    log.debug("Name {} in uri {} not found ", name, uri);
                }
            }
        } catch (Exception e) {
            log.error("Error when registering metrics {} ", e.getMessage());
        }
    }

    private void registerMetricForMethod(String name, String metric, long start, String unit) {
        name = metric + name.replace("/", "_");
        if ("counter".equalsIgnoreCase(metric)) {
            registry.counter(name).inc();
        } else if ("gauge".equalsIgnoreCase(metric)) {
            registry.concurrentGauge(name).inc();
        } else if ("histogram".equalsIgnoreCase(metric)) {
            Histogram histogram = registry.histogram(name);
            long count = histogram.getCount();
            histogram.update(count + 1);
        } else if ("meter".equalsIgnoreCase(metric)) {
            registry.meter(name).mark();
        } else if ("timer".equalsIgnoreCase(metric)) {
            createTimerMetrics(name, start, unit);
        } else {
            log.debug("unknown metric {} , must from list [counter, gauge, histogram, meter, timer]", metric);
        }
    }

    private void createTimerMetrics(String name, long start, String unit) {
        long end = System.currentTimeMillis();
        long duration = end - start;
        Metadata metadata = new MetadataBuilder()
                .withName(name)
                .withUnit(unit)
                .build();
        Timer timer = registry.timer(metadata);
        timer.update(duration, TimeUnit.MILLISECONDS);
    }
}