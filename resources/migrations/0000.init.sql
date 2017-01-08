CREATE TABLE liber (
  id uuid PRIMARY KEY,
  name text,
  email text,
  password text,
  created_at timestamp DEFAULT CURRENT_DATE);

INSERT INTO liber (id, email, password, name) VALUES ('588034cb-4c84-4f36-abcf-fa511aba0de3', 'josemanuel.rosamoncayo@gmail.com', 'password1234', 'Jose Manuel');
INSERT INTO liber (id, email, password, name) VALUES ('e02627de-2482-42ed-ab72-81ef4d376855', 'david.barragan@kaleidos.net', 'password1234', 'David');
INSERT INTO liber (id, email, password, name) VALUES ('2adb5684-4b20-45df-84f2-c4971dc42921', 'xavier.julian@kaleidos.net', 'password1234', 'Xaviju');
INSERT INTO liber (id, email, password, name) VALUES ('ee502a6b-2e9f-4b54-babf-97ada2981e9d', 'libreforge.user@gmail.com', 'password1234', 'Libre McMillan');

CREATE TABLE liber_course(
  liber uuid,
  course uuid,
  PRIMARY KEY (liber, course)
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
  description text,
  title text,
  course uuid,
  created_at timestamp DEFAULT CURRENT_DATE,
  finished_at timestamp,
  entry_order int,
  created_by uuid);

CREATE TABLE resource (
  id uuid PRIMARY KEY,
  title text,
  type text,
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
