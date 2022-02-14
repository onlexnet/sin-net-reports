package sinnet;

import org.springframework.beans.factory.annotation.Value;

public interface DbConnectionConfig {

  @Value("${spring.datasource.url}")
  void setDatasourceUrl(String value);

  @Value("${app.db.host}")
  void setDbHost(String value);

  @Value("${app.db.name}")
  void setDbName(String value);

  @Value("${spring.datasource.username}")
  void setUsername(String value);

  @Value("${spring.datasource.password}")
  void setPassword(String value);
}
