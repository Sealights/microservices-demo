
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


const run = async (lab_id,test_name,result) => {
    console.log("#####")
    await client.query(`
        create table if not exists mocha(
            datetime timestamp without time zone,
            lab_id text,
            test_name text,
            result text

        )
    `);
    await client.query(`
            insert into mocha(lab_id,test_name,result)
            values(now(), '${lab_id}','${test_name}','${result}')
    `);

     const result1 = await client.query('SELECT * from mocha order by datetime desc limit 1');
     const temp = result1[0][0];

    console.log("done", temp);
}

module.exports.default = run;