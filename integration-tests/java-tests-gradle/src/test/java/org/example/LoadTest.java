package org.example;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.*;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class LoadTest {

    private static CloseableHttpClient httpClient;
    private static Connection conn;
    private static DbFunctions db = new DbFunctions();

    @BeforeClass
    public static void setupClass() {
        httpClient = HttpClients.createDefault();
        conn = db.connect_to_db("btq", "postgres", "sealights");
        db.createTable(conn, "gradle");
    }

    @AfterClass
    public static void cleanupClass() throws SQLException {
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
    public void testIndex() throws Exception {
        String myEnvVariable = System.getenv("machine_dns");
        if (myEnvVariable == null) {
            myEnvVariable = "http://34.245.65.231:8081";
        }

        HttpGet request = new HttpGet(myEnvVariable);
        HttpResponse response = httpClient.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testSetCurrency() throws Exception {
        String myEnvVariable = System.getenv("machine_dns");
        if (myEnvVariable == null) {
            myEnvVariable = "http://34.245.65.231:8081";
        }

        String[] currencies = {"EUR", "USD", "JPY", "CAD"};
        String currency = currencies[new Random().nextInt(currencies.length)];

        HttpGet request = new HttpGet(myEnvVariable + "/setCurrency?currency_code=" + currency);
        HttpResponse response = httpClient.execute(request);
        assertEquals(405, response.getStatusLine().getStatusCode());
    }

    @Test
    public void testBrowseProduct() throws Exception {
        String myEnvVariable = System.getenv("machine_dns");
        if (myEnvVariable == null) {
            myEnvVariable = "http://34.245.65.231:8081";
        }

        String[] products = {"0PUK6V6EV0", "1YMWWN1N4O", "2ZYFJ3GM2N", "66VCHSJNUP", "6E92ZMYYFZ", "9SIQT8TOJO",
            "L9ECAV7KIM", "LS4PSXUNUM", "OLJCESPC7Z"};

        String product = products[new Random().nextInt(products.length)];

        HttpGet request = new HttpGet(myEnvVariable + "/product/" + product);
        HttpResponse response = httpClient.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
    }

    @Rule
    public TestWatcher testWatcher = new TestWatcher() {
        @Override
        public void succeeded(Description description) {
            String lab_id = System.getenv("lab_id");
            if (lab_id == null) {
                lab_id = "integ_ahmadbranch_3a1b_ahmadBTQ";
            }
            super.succeeded(description);
            String log = "passed";
            System.out.println(description.getMethodName() + ": " + log);
            db.insert_row(conn, "gradle", lab_id, description.getMethodName(), log);
        }

        @Override
        public void failed(Throwable e, Description description) {
            String lab_id = System.getenv("lab_id");
            if (lab_id == null) {
                lab_id = "integ_ahmadbranch_3a1b_ahmadBTQ";
            }
            super.failed(e, description);
            String log = "failed";
            System.out.println(description.getMethodName() + ": " + log);
            db.insert_row(conn, "gradle", lab_id, description.getMethodName(), log);
        }
    };
}
