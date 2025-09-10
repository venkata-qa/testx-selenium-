
package com.testx.web.api.selenium.restassured.qe.ui.webdriver;
import com.testx.web.api.selenium.restassured.qe.ui.custom_exceptions.HeadlessNotSupportedException;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import java.util.HashMap;
import java.util.Locale;
import static com.testx.web.api.selenium.restassured.qe.ui.config.ConfigurationManager.getConfiguration;
import static java.lang.Boolean.TRUE;


public enum WebDriverFactory {

    CHROME {
        @Override
        public WebDriver createDriver() {
            String launchBrowserViaExe = getConfiguration().launchBrowserViaExe();
            boolean windowType = getOperatingSytem().equals("win");
            if(Boolean.parseBoolean(launchBrowserViaExe) && !windowType){
                System.setProperty("webdriver.chrome.driver","src/test/resources/Driver/chromedriver_arm64");
            }
            else if(Boolean.parseBoolean(launchBrowserViaExe) && windowType){
                System.setProperty("webdriver.chrome.driver","src/test/resources/Driver/chromedriver.exe");
            }
            else {
                WebDriverManager.getInstance(DriverManagerType.CHROME).setup();
            }

            return new ChromeDriver(getOptions());
        }

        @Override
        public ChromeOptions getOptions() {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments(START_MAXIMIZED);
            chromeOptions.addArguments("--disable-infobars");
            chromeOptions.addArguments("--disable-notifications");
            chromeOptions.addArguments("--remote-allow-origins=*");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-extensions");
            chromeOptions.addArguments("--disable-dev-shm-usage");
            chromeOptions.addArguments("--disable-gpu");
            String downloadPath=System.getProperty("user.dir");
            if (getConfiguration().headless()) {
                chromeOptions.addArguments("--headless=new");
            }
            if(getOperatingSytem().equals("win")) {
                downloadPath = downloadPath+"/src/test/resources/external_downloads";
            }
            else
            {
                 downloadPath = downloadPath+"src/test/resources/external_downloads";
            }
            System.out.println("Download Path is"+downloadPath);
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put("download.default_directory", downloadPath);
            chromeOptions.setExperimentalOption("prefs", chromePrefs);

            return chromeOptions;
        }
    }, FIREFOX {
        @Override
        public WebDriver createDriver() {
            WebDriverManager.getInstance(DriverManagerType.FIREFOX).setup();
            return new FirefoxDriver(getOptions());
        }

        @Override
        public FirefoxOptions getOptions() {
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            firefoxOptions.addArguments(START_MAXIMIZED);
            if (getConfiguration().headless()) {
                firefoxOptions.addArguments("--headless=new");
            }


            return firefoxOptions;
        }
    }, EDGE {
        @Override
        public WebDriver createDriver() {
            String launchBrowserViaExe = getConfiguration().launchBrowserViaExe();
            boolean windowType = getOperatingSytem().equals("win");
            if(Boolean.parseBoolean(launchBrowserViaExe) && !windowType){
                System.setProperty("webdriver.edge.driver","src/test/resources/Driver/msedgedriver");
            }
            else if(Boolean.parseBoolean(launchBrowserViaExe) && windowType){
                System.setProperty("webdriver.edge.driver","src/test/resources/Driver/msedgedriver.exe");
            }
            else {
                WebDriverManager.getInstance(DriverManagerType.EDGE).setup();
            }
            return new EdgeDriver(getOptions());
        }

        @Override
        public EdgeOptions getOptions() {
            EdgeOptions edgeOptions = new EdgeOptions();
            edgeOptions.addArguments(START_MAXIMIZED);
            edgeOptions.addArguments("--disable-infobars");
            edgeOptions.addArguments("--disable-notifications");
            edgeOptions.addArguments("--remote-allow-origins=*");
            edgeOptions.addArguments("--no-sandbox");
            edgeOptions.addArguments("--disable-extensions");
            edgeOptions.addArguments("--disable-dev-shm-usage");
            edgeOptions.addArguments("--disable-gpu");
            if (getConfiguration().headless()) {
                edgeOptions.addArguments("--headless=new");
            }
            String downloadPath=System.getProperty("user.dir");
            if (getOperatingSytem().equals("win")) {
                downloadPath = downloadPath + "/src/test/resources/external_downloads";
            } else {
                downloadPath = downloadPath + "src/test/resources/external_downloads";
            }
            System.out.println("Download Path is"+downloadPath);
            HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
            chromePrefs.put("profile.default_content_settings.popups", 0);
            chromePrefs.put("download.default_directory", downloadPath);
            edgeOptions.setExperimentalOption("prefs", chromePrefs);

            return edgeOptions;
        }
    }, SAFARI {
        @Override
        public WebDriver createDriver() {
            WebDriverManager.getInstance(DriverManagerType.SAFARI).setup();

            return new SafariDriver(getOptions());
        }

        @Override
        public SafariOptions getOptions() {
            SafariOptions safariOptions = new SafariOptions();
            safariOptions.setAutomaticInspection(false);

            if (TRUE.equals(getConfiguration().headless()))
                throw new HeadlessNotSupportedException(safariOptions.getBrowserName());

            return safariOptions;
        }
    }
    , IE {
        @Override
        public WebDriver createDriver() {
            WebDriverManager.getInstance(DriverManagerType.IEXPLORER).setup();
            return new InternetExplorerDriver(getOptions());
        }

        @Override
        public InternetExplorerOptions getOptions() {
            InternetExplorerOptions internetExplorerOptions = new InternetExplorerOptions();
            internetExplorerOptions.ignoreZoomSettings();
            internetExplorerOptions.takeFullPageScreenshot();
            internetExplorerOptions.introduceFlakinessByIgnoringSecurityDomains();

            if (TRUE.equals(getConfiguration().headless()))
                throw new HeadlessNotSupportedException(internetExplorerOptions.getBrowserName());
            return internetExplorerOptions;
        }
    };

    private static final String START_MAXIMIZED = "--start-maximized";

    public abstract WebDriver createDriver();

    public abstract AbstractDriverOptions<?> getOptions();


    public static String getOperatingSytem()
    {
        String os= System.getProperty("os.name", "generic").toLowerCase(Locale.ROOT);


        if(os.contains("mac"))
        {
            return "mac";
        }
        else if(os.contains("win"))
        {
            return "win";
        }
        else
        {
            return "linux";
        }


    }

}
