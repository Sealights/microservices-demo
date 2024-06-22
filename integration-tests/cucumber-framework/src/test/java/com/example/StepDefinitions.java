package com.example;
import io.cucumber.java.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.BeforeClass;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class StepDefinitions {
    private static Connection conn;
    private static DbFunctions db = new DbFunctions();

    private static final int HTTP_TIMEOUT_MS = 10_000; // Increased the timeout to 10 seconds
    private static final int MAX_TOTAL_CONNECTIONS = 100;
    private static final int MAX_CONNECTIONS_PER_ROUTE = 50;
    // private static final int HTTP_TIMEOUT_MS = 10_000;
    static CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(
        RequestConfig.custom()
            .setConnectionRequestTimeout(HTTP_TIMEOUT_MS)
            .setSocketTimeout(HTTP_TIMEOUT_MS)
            .setConnectionRequestTimeout(HTTP_TIMEOUT_MS)
            .build()
    ).build();
    @BeforeStep
    public static void setup() {
        httpClient = HttpClients.createDefault();
        conn = db.connect_to_db("btq", "postgres", "sealights");
        db.createTable(conn, "cucumber");
    }

    @After
    public static void cleanup(Scenario scenario) throws SQLException {
        if (conn != null) {
            conn.close();
        }
        try {
            httpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Before
    public void setUp() {
        httpClient = HttpClients.createDefault();
    }

    @After
    public void afterScenario(Scenario scenario) throws SQLException {
        tearDown(scenario);
        cleanup(scenario);
    }

    private void tearDown(Scenario scenario) {
        if (scenario != null && scenario.isFailed()) {
            handleFailedScenario(scenario);
        } else {
            handlePassedScenario(scenario);
        }
    }

    private static final String BASE_URL_DEFAULT = "http://34.245.65.231:8081";
    private static final String BASE_URL = Optional.ofNullable(System.getenv("machine_dns")).orElse(BASE_URL_DEFAULT);

    private String[] products = {
        "0PUK6V6EV0", "1YMWWN1N4O", "2ZYFJ3GM2N",
        "66VCHSJNUP", "6E92ZMYYFZ", "9SIQT8TOJO",
        "L9ECAV7KIM", "LS4PSXUNUM", "OLJCESPC7Z"
    };
    @Given("There are {int} users")
    public void thereAreUsers(int numUsers) throws Exception {
        for (int i = 0; i < numUsers; i++) {
            testSession();
        }
    }
    @When("All users start their sessions")
    public void allUsersStartTheirSessions() {
        try {
            // Adding a 15-second sleep before the condition check
            Thread.sleep(15000); // 15000 milliseconds = 15 seconds
        } catch (InterruptedException e) {
            // Handle the InterruptedException
            e.printStackTrace();
        }

        // Since we are not using threads, this step can be removed.
    }
    @Then("All sessions should complete successfully")
    public void allSessionsShouldCompleteSuccessfully() {
        // Post-execution checks, if any.
    }
    @Given("A product list")
    public void aProductList() {
        // Assuming products are already initialized at class level.
    }
    @When("A user browses products")
    public void aUserBrowsesProducts() throws Exception {
        // Uncommented code for actual testing
        for (String product : products) {
            CloseableHttpResponse response = httpClient.execute(new HttpGet(BASE_URL + "/product/" + product));
            response.close();
            try {
                // Adding a 15-second sleep before the condition check
                Thread.sleep(15000); // 15000 milliseconds = 15 seconds
            } catch (InterruptedException e) {
                // Handle the InterruptedException
                e.printStackTrace();
            }

            if (response.getStatusLine().getStatusCode() != 200) {
                throw new Exception("Failed to load the product: " + product);
            }
        }
    }

    @Then("All products should be accessible")
    public void allProductsShouldBeAccessible() {
        // Assuming if the previous step passes, all products are accessible.
    }
    private void testSession() throws Exception {
        CloseableHttpResponse response = httpClient.execute(new HttpGet(BASE_URL + "/"));
        response.close();
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to load the homepage");
        }
    }

    @When("A user views their cart")
    public void aUserViewsTheirCart() throws Exception {
        CloseableHttpResponse response = httpClient.execute(new HttpGet(BASE_URL + "/cart"));
        response.close();
        try {
            // Adding a 15-second sleep before the condition check
            Thread.sleep(15000); // 15000 milliseconds = 15 seconds
        } catch (InterruptedException e) {
            // Handle the InterruptedException
            e.printStackTrace();
        }

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to view the cart");
        }
    }

    @Then("The cart page should be accessible")
    public void theCartPageShouldBeAccessible() {
        // Assuming if the previous step passes, the cart is accessible.
    }

    @When("A user adds products to their cart")
    public void aUserAddsProductsToTheirCart() throws Exception {
        for (String product : products) {
            HttpPost httpPost = new HttpPost(BASE_URL + "/cart");
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setEntity(new StringEntity("product_id=" + product + "&quantity=1"));
            CloseableHttpResponse response = httpClient.execute(httpPost);
            response.close();
            System.out.println(response.getStatusLine());

            try {
                // Adding a 15-second sleep before the condition check
                Thread.sleep(15000); // 15000 milliseconds = 15 seconds
            } catch (InterruptedException e) {
                // Handle the InterruptedException
                e.printStackTrace();
            }
            if (response.getStatusLine().getStatusCode() != 302) {
                throw new Exception("Failed to add the product to the cart: " + product);
            }
        }
    }

    @Then("All products should be added successfully")
    public void allProductsShouldBeAddedSuccessfully() {
        // Assuming if the previous step passes, all products are added successfully.
    }

    @When("A user accesses site assets")
    public void aUserAccessesSiteAssets() throws Exception {
        CloseableHttpResponse responseIcon = httpClient.execute(new HttpGet(BASE_URL + "/static/favicon.ico"));
        responseIcon.close();
        CloseableHttpResponse responseImg = httpClient.execute(new HttpGet(BASE_URL + "/static/img/products/hairdryer.jpg"));
        responseImg.close();

        try {
            // Adding a 15-second sleep before the condition check
            Thread.sleep(15000); // 15000 milliseconds = 15 seconds
        } catch (InterruptedException e) {
            // Handle the InterruptedException
            e.printStackTrace();
        }

        if (responseIcon.getStatusLine().getStatusCode() != 200 || responseImg.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to load site assets");
        }
    }

    @Then("The assets should be accessible")
    public void theAssetsShouldBeAccessible() {
        // Assuming if the previous step passes, assets are accessible.
    }

    @When("A user checks out with products")
    public void aUserChecksOutWithProducts() throws Exception {
        // This is a simplification. Normally, you'd POST checkout data.
        HttpPost httpPost = new HttpPost(BASE_URL + "/cart/checkout");
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");

        httpPost.setEntity(new StringEntity("email=someone%40example.com&street_address=1600+Amphitheatre+Parkway&zip_code=94043&city=Mountain+View&state=CA&country=United+States&credit_card_number=4432-8015-6152-0454&credit_card_expiration_month=1&credit_card_expiration_year=2025&credit_card_cvv=672"));
        CloseableHttpResponse response = httpClient.execute(httpPost);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        response.getEntity().writeTo(baos);
        System.out.println(baos);
        response.close();
        //add 15 sec
        try {
            // Adding a 15-second sleep before the condition check
            Thread.sleep(15000); // 15000 milliseconds = 15 seconds
        } catch (InterruptedException e) {
            // Handle the InterruptedException
            e.printStackTrace();
        }

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to checkout cart "+ response.getStatusLine().getStatusCode());
        }
    }

    @Then("The checkout should be successful")
    public void theCheckoutShouldBeSuccessful() {
        // Assuming if the previous step passes, checkout was successful.
    }

    @When("A user accesses the Sealights page")
    public void aUserAccessesTheSealightsPage() throws Exception {
        CloseableHttpResponse response = httpClient.execute(new HttpGet(BASE_URL + "/sealights"));
        response.close();
        if (response.getStatusLine().getStatusCode() != 200) {
            throw new Exception("Failed to access the Sealights page");
        }
    }

    @Then("The Sealights page should be accessible")
    public void theSealightsPageShouldBeAccessible() {
        // Assuming if the previous step passes, the Sealights page is accessible.
    }
}
