-- Run as postgres superuser (e.g. `psql -U postgres`)

-- 1. Create database
CREATE DATABASE "projectForm";

-- 2. Connect to it
\c projectForm

-- 3. Create table
CREATE TABLE formDetails (
    id                      SERIAL PRIMARY KEY,
    firstName               VARCHAR(50)  NOT NULL,
    lastName                VARCHAR(50)  NOT NULL,
    dob                     DATE         NOT NULL,
    gender                  VARCHAR(10)  NOT NULL,
    highestqualification    VARCHAR(100) NOT NULL,
    year_of_passing         INT          NOT NULL,
    mobilenumber            VARCHAR(10)  NOT NULL,
    created_at              TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- 4. Quick check
SELECT * FROM formDetails;
