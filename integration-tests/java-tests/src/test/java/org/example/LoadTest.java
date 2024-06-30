package org.example;
import java.sql.Connection;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.*;
import org.junit.rules.TestWatcher;
import java.lang.Thread;
import org.junit.runner.Description;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;
import static org.junit.Assert.assertEquals;

public class LoadTest {

    private static CloseableHttpClient httpClient;

    private static Connection conn;
    private static DbFunctions db = new DbFunctions();
    @BeforeClass
    public static void setup() {

        httpClient = HttpClients.createDefault();
         conn = db.connect_to_db("btq", "postgres", "sealights");
         db.createTable(conn,"withouttestng");
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


    //String myEnvVariable = System.getenv("machine_dns");
    @Test
    public void testIndex() throws Exception {
        Thread.sleep(15000);
        String myEnvVariable = System.getenv("machine_dns");
        if (myEnvVariable == null) {
            myEnvVariable = "http://34.245.65.231:8081"; // Set a default URL when machine_dns is not set
        }
        HttpGet request = new HttpGet(myEnvVariable);
        HttpResponse response = httpClient.execute(request);
        Thread.sleep(15000);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testSetCurrency() throws Exception {
        Thread.sleep(15000);
        String myEnvVariable = System.getenv("machine_dns");
        if (myEnvVariable == null) {
            myEnvVariable = "http://34.245.65.231:8081"; // Set a default URL when machine_dns is not set
        } // Declare and initialize myEnvVariable
        String[] currencies = {"EUR", "USD", "JPY", "CAD"};
        String currency = currencies[new Random().nextInt(currencies.length)];

        HttpGet request = new HttpGet(myEnvVariable + "/setCurrency?currency_code=" + currency);
        HttpResponse response = httpClient.execute(request);
        assertEquals(405, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testSealightsPage() throws IOException {
        String myEnvVariable = System.getenv("machine_dns");
        HttpGet request = new HttpGet(myEnvVariable + "/sealights");
        HttpResponse response = httpClient.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testBrowseProduct() throws Exception {
        Thread.sleep(15000);
        String myEnvVariable = System.getenv("machine_dns");
        if (myEnvVariable == null) {
            myEnvVariable = "http://34.245.65.231:8081"; // Set a default URL when machine_dns is not set
        } // Declare and initialize myEnvVariable
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
        Thread.sleep(15000);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Rule
    public TestWatcher testWatcher = new TestWatcher() {
        @Override
        public void succeeded(Description description) {
            String lab_id = System.getenv("lab_id");
            if (lab_id == null) {
                lab_id = "integ_ahmadbranch_3a1b_ahmadBTQ"; // Set a default URL when machine_dns is not set
            }
            super.succeeded(description);
            String log = "passed";
            System.out.println(description.getMethodName() + ": " + log);
            System.out.println(lab_id);
            db.insert_row(conn,"withouttestng",lab_id,description.getMethodName(),log);



        }

        @Override
        public void failed(Throwable e, Description description) {
            String lab_id = System.getenv("lab_id");
            if (lab_id == null) {
                lab_id = "integ_ahmadbranch_3a1b_ahmadBTQ"; // Set a default URL when machine_dns is not set
            }
            super.failed(e, description);
            String log = "failed";
            System.out.println(description.getMethodName() + ": " + log);
            System.out.println(lab_id);
            db.insert_row(conn,"withouttestng",lab_id,description.getMethodName(),log);

        }
    };
}
