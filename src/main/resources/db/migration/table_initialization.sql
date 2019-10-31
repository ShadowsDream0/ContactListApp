BEGIN TRANSACTION;

    DROP TABLE IF EXISTS phones;
    DROP TABLE IF EXISTS persons;
    DROP TYPE IF EXISTS GENDER;
    DROP TYPE IF EXISTS PHONE_TYPE;

    CREATE TYPE GENDER AS enum('male', 'female', 'transgender');
    CREATE TYPE PHONE_TYPE AS enum('mobile', 'desktop');

    CREATE TABLE persons (
        id SERIAL,
        first_name VARCHAR(20) NOT NULL,
        last_name VARCHAR(20) NOT NULL,
        gender GENDER NOT NULL,
        birthday DATE NOT NULL,
        city VARCHAR(30) NOT NULL,
        email VARCHAR(255) NOT NULL,
        CONSTRAINT persons_PK PRIMARY KEY (id),
        CONSTRAINT persons_AK UNIQUE (email)
    );

    CREATE TABLE phones (
        id SERIAL,
        phone_number VARCHAR(15) NOT NULL,
        phone_type PHONE_TYPE NOT NULL,
        person_id BIGINT NOT NULL,
        CONSTRAINT phones_PK PRIMARY KEY (id),
        CONSTRAINT phones_AK UNIQUE (phone_number, person_id),
        CONSTRAINT phones_FK FOREIGN KEY (person_id) REFERENCES persons(id)
    );

END TRANSACTION;