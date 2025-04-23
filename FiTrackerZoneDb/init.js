db = db.getSiblingDB('fit');
db.createCollection('zones');
db.createCollection('users');

db.createUser({
    user: "exampleUser",
    pwd: "3x@mplePassword",
    roles: [{ role: "readWrite", db: "fit" }]
});