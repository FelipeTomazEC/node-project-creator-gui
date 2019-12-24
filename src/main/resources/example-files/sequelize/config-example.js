require('dotenv').config()
/**
 * IMPORTANT
 * You have to set the parameters used in this file at your .env file.
 */

module.exports = {
    development: {
        url: process.env.DEV_DATABASE_URL,
        dialect: 'postgres',  //Replace to the dialect of the database that you're using in this project.
    },
    test: {
        url: process.env.TEST_DATABASE_URL,
        dialect: 'postgres', //Replace to the dialect of the database that you're using in this project.
    },
    production: {
        url: process.env.DATABASE_URL,
        dialect: 'postgres', //Replace to the dialect of the database that you're using in this project.
    },
}