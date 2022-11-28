package sinnet;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import sinnet.db.PostgresDbExtension;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ExtendWith(PostgresDbExtension.class)
public class RunCucumberTest {
}
