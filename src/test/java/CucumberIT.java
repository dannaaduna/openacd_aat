import org.junit.runner.RunWith;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@Cucumber.Options(format = { "junit:target/cucumber-junit-report.xml" }, features = { "." }, strict = true)

public class CucumberIT {

}