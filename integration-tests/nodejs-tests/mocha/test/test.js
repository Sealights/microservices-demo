const chai = require('chai');
const axios = require('axios');
const { expect } = chai;
const Sequelize = require("sequelize")


const client = new Sequelize.Sequelize({
    host: 'btq-automation.colnnovkdyzx.us-west-2.rds.amazonaws.com',
    username: 'postgres',
    port: 5432,
    password: 'sealights',
    database: 'btq',
    dialect: "postgres",
    dialectOptions: {
        ssl: {
            require: true, // This will help you. But you will see nwe error
            rejectUnauthorized: false // This line will fix new error
        }
    },
});
//npm i sequelize 

const run = async (lab_id,test_name,result) => {
  try {
    console.log("#####")
    await client.query(`
        create table if not exists mocha(
            lab_id text,
            test_name text,
            result text

        )
    `);
    await client.query(`
            insert into mocha(lab_id,test_name,result)
            values('${lab_id}','${test_name}','${result}')
    `);

    //  const result1 = await client.query('SELECT * from mocha order by datetime desc limit 1');
    //  const temp = result1[0][0];

    // console.log("done", temp);
         } catch (err) {
           console.log("ERROR", err)
        }
        
}

const BASE_URL = process.env.machine_dns || 'http://34.245.65.231:8081';
const Lab_id = process.env.lab_id || 'integ_ahmadbranch_3a1b_ahmadBTQ';

describe('Api Tests', function () {
  let session;

  before(function () {
    session = axios.create({ baseURL: BASE_URL });
  });

  this.timeout(50000);  // Increase the timeout for slow tests

  it('should return 200 for index page', async function () {
    await new Promise(resolve => setTimeout(resolve, 20000));
    const response = await session.get('/');
    expect(response.status).to.equal(200);
  });

  it('should be able to set different currencies', async function () {
    await new Promise(resolve => setTimeout(resolve, 20000));
    const currencies = ['EUR', 'USD', 'JPY', 'CAD'];
    for (const currency of currencies) {
      const response = await session.post('/setCurrency', { currency_code: currency });
      expect(response.status).to.equal(200);
    }
  });

  it('should return 200 for browsing products', async function () {
    await new Promise(resolve => setTimeout(resolve, 20000));
    const products = [
      '0PUK6V6EV0',
      '1YMWWN1N4O',
      '2ZYFJ3GM2N',
      '66VCHSJNUP',
      '6E92ZMYYFZ',
      '9SIQT8TOJO',
      'L9ECAV7KIM',
      'LS4PSXUNUM',
      'OLJCESPC7Z'
    ];

    for (const product_id of products) {
      const response = await session.get(`/product/${product_id}`);
      expect(response.status).to.equal(200);
    }
  });

  it('should return 404 for a non-existent route', async function () {
    await new Promise(resolve => setTimeout(resolve, 20000));
    try {
      await session.get('/nonexistent-route');
    } catch (error) {
      expect(error.response.status).to.equal(404);
    }
  });

  it('should return 400 for invalid request data', async function () {
    await new Promise(resolve => setTimeout(resolve, 20000));
    try {
      await session.post('/setCurrency', { invalid_key: 'invalid_value' });
    } catch (error) {
      expect(error.response.status).to.equal(400);
    }

  });

  const tests = [];
  afterEach(function () {
    tests.push({
      title: this.currentTest.title, // the title of the test
      state: this.currentTest.state // 'passes', 'failed' etc...
    })
  });

  after(async () => {

    tests.forEach(async (test) => {
      console.log(`Test: ${test.title}, State: ${test.state}`);
      // Send to DynamoDB code
      await run(Lab_id, test.title, test.state);

    });
    //await PG.close();
  });

});
