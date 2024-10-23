package hirs.utils;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * This class exposes methods to get the banner properties file. Properties are read from
 * /etc/hirs/banner.properties if it exists. If no value for a property exists in the file,
 * no change will be applied for that property.
 */
@Log4j2
public class BannerConfiguration {

    private static final Path BANNER_PROPERTIES_PATH = FileSystems.getDefault()
            .getPath("/opt/tomcat/webapps/HIRS_AttestationCAPortal",
                    "WEB-INF", "classes", "banner.properties");

    private static final String BANNER_COLOR = "banner.color";
    private static final String BANNER_STRING = "banner.string";
    private static final String BANNER_DYNAMIC = "banner.dynamic";
    private static final String LEFT_CONTENT = "left.content";
    private static final String RIGHT_CONTENT = "right.content";
    private final ArrayList<String> leftContent = new ArrayList<>();
    private final ArrayList<String> rightContent = new ArrayList<>();
    @Getter
    private String bannerColor = "";
    @Getter
    private String bannerString = "";
    @Getter
    private String bannerDynamic = "";

    /**
     * Banner Configuration default constructor.
     * Verify if the file exist, if it does it will get all the
     * properties values and save them on the class.
     *
     * @throws IOException the banner level for the website.
     */
    public BannerConfiguration() throws IOException {
        if (!Files.exists(BANNER_PROPERTIES_PATH)) {
            log.info(String.format(
                    "No file found at %s. Banner will not display.",
                    BANNER_PROPERTIES_PATH
            ));
            return;
        }

        try (InputStream loggingIs = new FileInputStream(BANNER_PROPERTIES_PATH.toFile())) {
            Properties bannerProps = new Properties();
            bannerProps.load(loggingIs);
            setBannerProperties(bannerProps);
        } catch (IOException e) {
            throw new IOException("Could not apply banner configuration", e);
        }
    }

    /**
     * This method applies any dynamically configuration found in the properties file,
     * if it exists.
     *
     * @param bannerProps banner props
     */
    private void setBannerProperties(final Properties bannerProps) {

        bannerColor = bannerProps.getProperty(BANNER_COLOR, "").toLowerCase();
        bannerString = bannerProps.getProperty(BANNER_STRING, "").toUpperCase();
        bannerDynamic = bannerProps.getProperty(BANNER_DYNAMIC, "").toUpperCase();

        // We don't need these any more
        bannerProps.remove(BANNER_COLOR);
        bannerProps.remove(BANNER_STRING);
        bannerProps.remove(BANNER_DYNAMIC);

        //Get property list and sort it
        ArrayList<String> propertyList = new ArrayList<>(bannerProps.stringPropertyNames());
        Collections.sort(propertyList);

        // Set banner information from the property file
        for (String prop : propertyList) {
            if (prop.startsWith(LEFT_CONTENT)) {
                leftContent.add(bannerProps.getProperty(prop));
            } else if (prop.startsWith(RIGHT_CONTENT)) {
                rightContent.add(bannerProps.getProperty(prop));
            }
        }
    }

    /**
     * Return if the banner was set.
     *
     * @return if the banner was set.
     */
    public Boolean getHasBanner() {
        return !bannerColor.isEmpty() || !bannerString.isEmpty();
    }

    /**
     * Return the left content.
     *
     * @return the left content
     */
    public List<String> getLeftContent() {
        return Collections.unmodifiableList(leftContent);
    }

    /**
     * Return the right content.
     *
     * @return the right content
     */
    public List<String> getRightContent() {
        return Collections.unmodifiableList(rightContent);
    }
}

