-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data

DELETE FROM horse where id < 0;

INSERT INTO horse (id, name, description, date_of_birth, sex, mother_id)
VALUES (-1, 'Wendy', 'The famous one!', '2012-12-12', 'FEMALE', null),
       (-2, 'Issy', 'Description 1', '2014-03-13', 'FEMALE', -1),
       (-3, 'Carlo', 'Description 2', '2016-04-14', 'MALE', null),
       (-4, 'Alejandro', 'Description 3', '2018-05-15', 'MALE',null),
       (-5, 'Anderson', 'Description 4', '2020-06-16', 'MALE', null),
       (-6, 'Anna', 'Description 5', '2022-07-17', 'FEMALE', null),
       (-7, 'Matteo', 'Description 6', '2012-08-18', 'MALE', null)
;

DELETE FROM owner where id < 0;

INSERT INTO owner (id, first_name, last_name, email)
VALUES (-1, 'Fritz', 'Mayer', 'fritz.mayer@gmail.com'),
       (-2, 'Franz', 'Hinterberger', 'franz.hinterbergere@gmail.com'),
       (-3, 'Martin', 'Hinteregger', 'martin.hinteregger@gmail.com'),
       (-4, 'Max', 'Mustermann', 'max.mustermann@gmail.com')
;