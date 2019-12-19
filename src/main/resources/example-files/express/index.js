const express = require('express');

const PORT = process.env.PORT || 3333;

const app = express();

app.use(express.json());

app.get('/', (req, res) => {
    res.status(200).send({
        version: '0.0.1',
        message: 'ok'
    })
});

app.listen(PORT, () => {
    console.log(`App listening on port ${PORT}.`);
});
