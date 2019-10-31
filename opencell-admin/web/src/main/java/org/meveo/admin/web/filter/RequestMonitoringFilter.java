package org.meveo.admin.web.filter;

import java.io.IOException;
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
import org.eclipse.microprofile.metrics.MetricRegistry;
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
                String name = uri.replaceAll(contextPath, "").replaceAll(".jsf", "");
                if (!Strings.isNullOrEmpty(name) && metricsConfigCache.containsKey(name)) {
                    Map<String, String> params = metricsConfigCache.getConfiguration(name);
                    if (params.containsKey(req.getMethod())) {
                        String metric = params.get(req.getMethod());
                        log.info("Register {} metrics for {} in {}", metric, req.getMethod(), name);
                        registerMetricForMethod(name, metric, millis);
                    }
                } else {
                    log.info("Name {} in uri {} not found ", name, uri);
                }
            }
        } catch (Exception e) {
            log.error("Error when registering metrics {} ", e.getMessage());
        }
    }

    private void registerMetricForMethod(String name, String metric, long start) {
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
            long end = System.currentTimeMillis();
            Timer timer = registry.timer(name);
            timer.update(end - start, TimeUnit.MILLISECONDS);
        } else {
            log.debug("unknown metric {} , must from list [counter, gauge, histogram, meter, timer]", metric);
        }
    }
}