CREATE ROLE uservice_dbuser WITH LOGIN PASSWORD 'uservice_dbuser';
CREATE SCHEMA uservice_projects AUTHORIZATION uservice_dbuser;
