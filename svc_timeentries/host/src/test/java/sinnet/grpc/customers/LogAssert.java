package sinnet.grpc.customers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

public class LogAssert extends AbstractAssert<LogAssert, LogAssert.LogObserver> {

  protected LogAssert(LogObserver actual) {
      super(actual, LogAssert.class);
  }

  
  public static LogAssert assertThat(LogObserver appender) {
    return new LogAssert(appender);
  }

  public static LogObserver ofHibernate() {
    return new LogObserver("org.hibernate");
  }

  public LogAssert selectOperationsSizeIs(int expected) {
    isNotNull();
    Assertions.assertThat(actual.appender.selectOps).hasSize(expected);
    return this;
  }  

  LogObserver ofPackage(String packageName) {
    return new LogObserver(packageName);
  }

  static class LogObserver {
    private final HibernateSqlAppender appender;

    LogObserver(String loggerName) {
      this.appender = new HibernateSqlAppender();

      var logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(loggerName);
      logger.addAppender(appender);
      logger.setLevel(Level.DEBUG);

      appender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
      appender.start();
    }

  }

  static class HibernateSqlAppender extends AppenderBase<ILoggingEvent> {

    public List<ILoggingEvent> list = new ArrayList<>();
    private List<java.time.LocalDateTime> insertOps = new ArrayList<>();
    private List<java.time.LocalDateTime> selectOps = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent e) {
      var f = e.getFormattedMessage();
      if (f.startsWith("insert into ")) {
        insertOps.add(java.time.LocalDateTime.now());
      } else if (f.startsWith("select ")) {
        selectOps.add(java.time.LocalDateTime.now());
      } else {
        // ignored for now
      }
      list.add(e);
    }    
    
    public void reset() {
        this.list.clear();
    }

    public boolean contains(String string, Level level) {
        return this.list.stream()
          .anyMatch(event -> event.toString().contains(string)
            && event.getLevel().equals(level));
    }

    public int countEventsForLogger(String loggerName) {
        return (int) this.list.stream()
          .filter(event -> event.getLoggerName().contains(loggerName))
          .count();
    }

    public List<ILoggingEvent> search(String string) {
        return this.list.stream()
          .filter(event -> event.toString().contains(string))
          .collect(Collectors.toList());
    }

    public List<ILoggingEvent> search(String string, Level level) {
        return this.list.stream()
          .filter(event -> event.toString().contains(string)
            && event.getLevel().equals(level))
          .collect(Collectors.toList());
    }

    public int getSize() {
        return this.list.size();
    }

    public List<ILoggingEvent> getLoggedEvents() {
        return Collections.unmodifiableList(this.list);
    }
  }
}
