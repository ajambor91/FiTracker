CREATE USER exampleuser WITH PASSWORD 'YOUR STRONG PASSWORD';
CREATE DATABASE fit;



GRANT CONNECT ON DATABASE fit TO exampleuser;
GRANT ALL PRIVILEGES ON DATABASE fit to exampleuser;
ALTER DATABASE fit OWNER TO exampleuser;
