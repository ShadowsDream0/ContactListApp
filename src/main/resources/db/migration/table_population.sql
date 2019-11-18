--Populate persons table
INSERT INTO persons (first_name, last_name, gender, birthday, city, email)
    VALUES ('Foo', 'Barson', 'male', '1990-06-12', 'Los Angeles', 'foo.barson@gmail.com');
INSERT INTO persons (first_name, last_name, gender, birthday, city, email)
    VALUES ('Bar', 'MCBaz', 'female', '1989-03-30', 'New York', 'bar.mcbaz@gmail.com');
INSERT INTO persons (first_name, last_name, gender, birthday, city, email)
    VALUES ('Baz', 'Fooey', 'transgender', '1988-02-29', 'New York', 'baz.fooey@gmail.com');
INSERT INTO persons (first_name, last_name, gender, birthday, city, email)
    VALUES ('Foo', 'Bazey', 'male', '1989-03-30', 'New York', 'foo.bazey@gmail.com');
INSERT INTO persons (first_name, last_name, gender, birthday, city, email)
    VALUES ('Bar', 'MCBaz', 'female', '1989-03-30', 'New York', 'bar.mcbaz1@gmail.com');
INSERT INTO persons (first_name, last_name, gender, birthday, city, email)
    VALUES ('Bar', 'Fooer', 'male', '1991-12-01', 'San Francisco', 'bar.fooer@gmail.com');
INSERT INTO persons (first_name, last_name, gender, birthday, city, email)
    VALUES ('Baz', 'Barer', 'female', '1987-02-28', 'Washington', 'baz.barer@gmail.com');
INSERT INTO persons (first_name, last_name, gender, birthday, city, email)
    VALUES ('Foobar', 'Bazfooey', 'transgender', '1986-11-21', 'San Francisco', 'foobar.bazfooey@gmail.com');
INSERT INTO persons (first_name, last_name, gender, birthday, city, email)
    VALUES ('Bazfoo', 'MCBarfoo', 'female', '1989-03-30', 'New York', 'bazfoo.mcbarfoo@gmail.com');
INSERT INTO persons (first_name, last_name, gender, birthday, city, email)
    VALUES ('Barbaz', 'Foobazey', 'male', '1985-10-01', 'New York', 'barbaz.foobazey@gmail.com');

--Populate phones table
INSERT INTO phones (phone_number, phone_type, person_id)
    VALUES ('+1-202-555-0142', 'home', (SELECT id FROM persons WHERE email='foo.barson@gmail.com'));
INSERT INTO phones (phone_number, phone_type, person_id)
    VALUES ('+1-202-555-0142', 'home', (SELECT id FROM persons WHERE email='bar.mcbaz@gmail.com'));
INSERT INTO phones (phone_number, phone_type, person_id)
    VALUES ('+1-202-555-0108', 'work', (SELECT id FROM persons WHERE email='baz.fooey@gmail.com'));
INSERT INTO phones (phone_number, phone_type, person_id)
    VALUES ('+1-202-555-0110', 'home', (SELECT id FROM persons WHERE email='baz.fooey@gmail.com'));
INSERT INTO phones (phone_number, phone_type, person_id)
    VALUES ('+1-202-555-0187', 'work', (SELECT id FROM persons WHERE email='foo.bazey@gmail.com'));
INSERT INTO phones (phone_number, phone_type, person_id)
    VALUES ('+1-202-555-0178', 'home', (SELECT id FROM persons WHERE email='bar.mcbaz1@gmail.com'));
INSERT INTO phones (phone_number, phone_type, person_id)
    VALUES ('+1-202-555-0178', 'home', (SELECT id FROM persons WHERE email='bar.fooer@gmail.com'));
INSERT INTO phones (phone_number, phone_type, person_id)
    VALUES ('+1-202-555-0152', 'home', (SELECT id FROM persons WHERE email='baz.barer@gmail.com'));
INSERT INTO phones (phone_number, phone_type, person_id)
    VALUES ('+1-202-555-0183', 'work', (SELECT id FROM persons WHERE email='baz.barer@gmail.com'));
INSERT INTO phones (phone_number, phone_type, person_id)
    VALUES ('+1-202-555-0143', 'work', (SELECT id FROM persons WHERE email='baz.barer@gmail.com'));
INSERT INTO phones (phone_number, phone_type, person_id)
    VALUES ('+1-202-555-0127', 'work', (SELECT id FROM persons WHERE email='foobar.bazfooey@gmail.com'));
INSERT INTO phones (phone_number, phone_type, person_id)
    VALUES ('+1-617-555-0177', 'home', (SELECT id FROM persons WHERE email='barbaz.foobazey@gmail.com'));
