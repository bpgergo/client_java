package io.prometheus.client.exporter;

import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.ProtobufFormat;
import io.prometheus.client.exporter.common.TextFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.logging.Logger;

public class MetricsServlet extends HttpServlet {
  private static final Logger LOGGER = Logger.getLogger(MetricsServlet.class.getName());

  private CollectorRegistry registry;

  /**
   * Construct a MetricsServlet for the default registry.
   */
  public MetricsServlet() {
    this(CollectorRegistry.defaultRegistry);
  }

  /**
   * Construct a MetricsServlet for the given registry.
   */
  public MetricsServlet(CollectorRegistry registry) {
    this.registry = registry;
  }

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
      throws ServletException, IOException {
    String header = req.getHeader("Accept");
    LOGGER.fine("header accept:" + header);
    if (header.equals(ProtobufFormat.CONTENT_TYPE_PROTOOBUF)) {
      resp.setContentType(ProtobufFormat.CONTENT_TYPE_PROTOOBUF);
      OutputStream out = resp.getOutputStream();
      Integer length = ProtobufFormat.write(out, this.registry.metricFamilySamples());
      if (length != null && length > 0){
        resp.setContentLength(length);
        LOGGER.fine("content lenght:" + length);
      }
      out.flush();
      out.close();
    } else {
      resp.setContentType(TextFormat.CONTENT_TYPE_004);
      PrintWriter writer = resp.getWriter();
      TextFormat.write004(writer, this.registry.metricFamilySamples());
      writer.flush();
      writer.close();
    }
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
        throws ServletException, IOException {
    doGet(req, resp);
  }

}
