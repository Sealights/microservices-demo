package org.example;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.HttpResponse;
import org.junit.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class LoadTest {

    private static CloseableHttpClient httpClient;
    private static Connection conn;
    private static DbFunctions db = new DbFunctions();

    @BeforeClass
    public static void setup() {
        httpClient = HttpClients.createDefault();
        conn = db.connect_to_db("btq", "postgres", "sealights");
        db.createTable(conn, "supporttestng");
    }

    @AfterClass
    public static void cleanup() throws SQLException {
        conn.close();
    }

    @Before
    public void setUp() {
        httpClient = HttpClients.createDefault();
    }

    @After
    public void tearDown() throws IOException {
        httpClient.close();
    }

    @Test
    public void testBrowseProduct() throws Exception {
        Thread.sleep(15000);
        String myEnvVariable = System.getenv("machine_dns");
        if (myEnvVariable == null) {
            myEnvVariable = "http://34.245.65.231:8081"; // Set a default URL when machine_dns is not set
        }
        String[] products = {
            "0PUK6V6EV0",
            "1YMWWN1N4O",
            "2ZYFJ3GM2N",
            "66VCHSJNUP",
            "6E92ZMYYFZ",
            "9SIQT8TOJO",
            "L9ECAV7KIM",
            "LS4PSXUNUM",
            "OLJCESPC7Z"
        };
        String product = products[new Random().nextInt(products.length)];
        HttpGet request = new HttpGet(myEnvVariable + "/product/" + product);
        HttpResponse response = httpClient.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testAddToCart() throws Exception {
        Thread.sleep(15000);
        String myEnvVariable = System.getenv("machine_dns");
        if (myEnvVariable == null) {
            myEnvVariable = "http://34.245.65.231:8081"; // Set a default URL when machine_dns is not set
        }
        String[] products = {
            "0PUK6V6EV0",
            "1YMWWN1N4O",
            "2ZYFJ3GM2N",
            "66VCHSJNUP",
            "6E92ZMYYFZ",
            "9SIQT8TOJO",
            "L9ECAV7KIM",
            "LS4PSXUNUM",
            "OLJCESPC7Z"
        };

        String product = products[new Random().nextInt(products.length)];

        HttpGet request = new HttpGet(myEnvVariable + "/product/" + product);
        HttpResponse response = httpClient.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());

        // Assuming you add the logic to add the product to the cart here
    }

    @Test
    public void testCheckout() throws Exception {
        Thread.sleep(15000);
        String myEnvVariable = System.getenv("machine_dns");
        if (myEnvVariable == null) {
            myEnvVariable = "http://34.245.65.231:8081"; // Set a default URL when machine_dns is not set
        }
        // Assuming you add the logic to perform checkout here
    }

    @Test
    public void testNonExistentRoute() throws Exception {
        Thread.sleep(15000);
        String myEnvVariable = System.getenv("machine_dns");
        if (myEnvVariable == null) {
            myEnvVariable = "http://34.245.65.231:8081"; // Set a default URL when machine_dns is not set
        }
        String nonExistentRoute = "/nonexistent-route"; // Define a non-existent route

        // Create an HttpClient that follows up to 5 redirects
        RequestConfig requestConfig = RequestConfig.custom().setRedirectsEnabled(true).setMaxRedirects(5).build();
        CloseableHttpClient redirectHttpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();

        HttpGet request = new HttpGet(myEnvVariable + nonExistentRoute);
        HttpResponse response = redirectHttpClient.execute(request);

        // Check if the response is a redirect (HTTP status code 3xx)
        int statusCode = response.getStatusLine().getStatusCode();
        assertFalse("Expected a non-redirect response (status code 3xx)", statusCode >= 300 && statusCode < 400);

        assertEquals(404, statusCode); // Assert the expected status code (404 for a non-existent route)

        // Close the HttpClient
        redirectHttpClient.close();
    }

    @Rule
    public TestWatcher testWatcher = new TestWatcher() {
        @Override
        protected void succeeded(Description description) {
            String lab_id = System.getenv("lab_id");
            if (lab_id == null) {
                lab_id = "integ_ahmadbranch_3a1b_ahmadBTQ"; // Set a default URL when machine_dns is not set
            }
            super.succeeded(description);
            String log = "passed";
            System.out.println(description.getMethodName() + ": " + log);
            db.insert_row(conn, "supporttestng", lab_id, description.getMethodName(), log);
        }

        @Override
        protected void failed(Throwable e, Description description) {
            String lab_id = System.getenv("lab_id");
            if (lab_id == null) {
                lab_id = "integ_ahmadbranch_3a1b_ahmadBTQ"; // Set a default URL when machine_dns is not set
            }
            super.failed(e, description);
            String log = "failed";
            System.out.println(description.getMethodName() + ": " + log);
            db.insert_row(conn, "supporttestng", lab_id, description.getMethodName(), log);
        }
    };

}
