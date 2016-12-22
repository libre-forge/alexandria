CREATE TABLE liber (
  id uuid PRIMARY KEY,
  name text,
  email text,
  password text,
  created_at timestamp DEFAULT CURRENT_DATE);

INSERT INTO liber (id, email, password, name) VALUES ('588034cb-4c84-4f36-abcf-fa511aba0de3', 'john.doe@gmail.com', 'password1234', 'John Doe');
INSERT INTO liber (id, email, password, name) VALUES ('e02627de-2482-42ed-ab72-81ef4d376855', 'alexa.doe@gmail.com', 'password1234', 'Alexa');
INSERT INTO liber (id, email, password, name) VALUES ('2adb5684-4b20-45df-84f2-c4971dc42921', 'preston.doe@gmail.com', 'password1234', 'Preston McMillan');
INSERT INTO liber (id, email, password, name) VALUES ('ee502a6b-2e9f-4b54-babf-97ada2981e9d', 'libreforge.user@gmail.com', 'password1234', 'Libre McMillan');

CREATE TABLE liber_course(
  liber uuid,
  course uuid
);

CREATE TABLE course (
  id uuid PRIMARY KEY,
  title text,
  pitch text,
  description text,
  status text DEFAULT 'active',
  member_limit int DEFAULT 0,
  created_at timestamp DEFAULT CURRENT_DATE,
  created_by uuid);

CREATE TABLE subject (
  id uuid PRIMARY KEY,
  title text,
  course uuid,
  created_at timestamp DEFAULT CURRENT_DATE,
  finished_at timestamp;
  sorder int,
  created_by uuid);

CREATE TABLE resource (
  id uuid PRIMARY KEY,
  title text,
  mime text,
  uri text,
  description text,
  subject uuid,
  created_at timestamp DEFAULT CURRENT_DATE,
  created_by uuid);

CREATE TABLE comment (
  id uuid PRIMARY KEY,
  content text,
  subject uuid,
  course uuid,
  created_at timestamp DEFAULT CURRENT_DATE,
  created_by uuid);
