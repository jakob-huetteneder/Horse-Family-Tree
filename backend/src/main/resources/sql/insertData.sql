-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data

DELETE FROM owner where id < 0;

INSERT INTO owner (id, first_name, last_name, email)
VALUES (-1, 'Fritz', 'Mayer', 'fritz.mayer@gmail.com'),
       (-2, 'Franz', 'Hinterberger', 'franz.hinterbergere@gmail.com'),
       (-3, 'Martin', 'Hinteregger', 'martin.hinteregger@gmail.com'),
       (-4, 'Max', 'Mustermann', 'max.mustermann@gmail.com'),
       (-5, 'Tina', 'Mustermann', 'tina.mustermann@gmail.com'),
       (-6, 'Angela', 'Mustermann', 'angela.mustermann@gmail.com'),
       (-7, 'Sasuke', 'Uchiha', 'sasuke.uchiha@gmail.com'),
       (-8, 'Mikasa', 'Ackermann', 'mikasa.ackermann@gmail.com'),
       (-9, 'Eren', 'Jaeger', 'eren.jaeger@gmail.com'),
       (-10, 'Naruto', 'Uzumaki', 'naruto.uzumaki@gmail.com')
;

DELETE FROM horse where id < 0;

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id)
VALUES (-1, 'Wendy', 'The famous one!', '2012-12-12', 'FEMALE', -1, null, null),
       (-2, 'Issy', 'Description 1', '2014-03-13', 'FEMALE', -3, -1, null),
       (-3, 'Carlo', 'Description 2', '2016-04-14', 'MALE', -4, null, null),
       (-10, 'Lilli', 'Description 9', '2019-08-18', 'FEMALE', -9, null, -3),
       (-9, 'Andreas', 'Description 8', '2017-08-18', 'MALE', -8, null, -3),
       (-4, 'Alejandro', 'Description 3', '2018-05-15', 'MALE',-2, null, -9),
       (-7, 'Matteo', 'Description 6', '2019-10-18', 'MALE', -6, null, -4),
       (-5, 'Anderson', 'Description 4', '2020-06-16', 'MALE', -10, -1, -7),
       (-8, 'Thomas', 'Description 7', '2021-08-18', 'MALE', -7, -2, -5),
       (-6, 'Anna', 'Description 5', '2022-07-17', 'FEMALE', -5, -10, -8)
;

