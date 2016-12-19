CREATE TABLE liber (
  id uuid,
  name text,
  email text,
  password text,
  created_at timestamp);

INSERT INTO liber (id, email, password, name) VALUES ('588034cb-4c84-4f36-abcf-fa511aba0de3', 'john.doe@gmail.com', 'password1234', 'John Doe');
INSERT INTO liber (id, email, password, name) VALUES ('e02627de-2482-42ed-ab72-81ef4d376855', 'alexa.doe@gmail.com', 'password1234', 'Alexa');
INSERT INTO liber (id, email, password, name) VALUES ('2adb5684-4b20-45df-84f2-c4971dc42921', 'preston.doe@gmail.com', 'password1234', 'Preston McMillan');

CREATE TABLE course (
  id uuid,
  title text,
  created_at timestamp,
  created_by uuid);

CREATE TABLE theme (
  id uuid,
  title text,
  course uuid,
  created_at timestamp,
  created_by uuid);

CREATE TABLE resource (
  id uuid,
  title text,
  description text,
  theme uuid,
  created_at timestamp,
  created_by uuid);

CREATE TABLE comment (
  id uuid,
  content text,
  theme uuid,
  course uuid,
  created_at timestamp,
  created_by uuid);
