# DROPPING TABLES ###########################################
DROP TABLE IF EXISTS tasks_checklists;
DROP TABLE IF EXISTS checklist_items;
DROP TABLE IF EXISTS checklists;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS attachments;
DROP TABLE IF EXISTS tasks_labels;
DROP TABLE IF EXISTS labels;
DROP TABLE IF EXISTS tasks_users;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS project_users;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS gcms;
DROP TABLE IF EXISTS users;

# CREATING TABLES ###########################################
CREATE TABLE users (
  id INT(10) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  auth_token VARCHAR(255) UNIQUE,
  email VARCHAR(255) UNIQUE,
  password VARCHAR(255),
  first_name VARCHAR(30),
  last_name VARCHAR(30),
  avatar_url VARCHAR(128),
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);

CREATE TABLE gcms (
  id INT(10) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id INT(10) UNSIGNED,
  gcm VARCHAR(255),

  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE projects (
  id INT(10) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description TEXT,
  color INT(10),
  author_id INT(10) UNSIGNED,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,

  FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE project_users (
  project_id INT(10) UNSIGNED,
  user_id INT(10) UNSIGNED,

  PRIMARY KEY (project_id, user_id),
  FOREIGN KEY (project_id) REFERENCES projects(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE tasks (
  id INT(10) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  parent_id INT(10) UNSIGNED,
  name VARCHAR(30),
  description TEXT,
  state ENUM('new', 'assigned', 'in progress', 'done', 'tested'),
  due_date DATETIME,
  expected_hours INT(10) UNSIGNED,
  author INT(10) UNSIGNED,
  worked_time INT(10) UNSIGNED,
  priotity INT(10),
  progress INT(10),
  project_id INT(10) UNSIGNED,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,

  FOREIGN KEY (parent_id) REFERENCES tasks(id),
  FOREIGN KEY (author) REFERENCES users(id),
  FOREIGN KEY (project_id) REFERENCES projects(id)
);

CREATE TABLE tasks_users (
  task_id INT(10) UNSIGNED,
  user_id INT(10) UNSIGNED,

  PRIMARY KEY (task_id, user_id),
  FOREIGN KEY (task_id) REFERENCES tasks(id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE labels (
  id INT(10) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE tasks_labels (
  task_id INT(10) UNSIGNED,
  label_id INT(10) UNSIGNED,

  PRIMARY KEY (task_id, label_id),
  FOREIGN KEY (task_id) REFERENCES tasks(id),
  FOREIGN KEY (label_id) REFERENCES labels(id)
);

CREATE TABLE attachments (
  id INT(10) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  url VARCHAR(128),
  author INT(10) UNSIGNED,
  task_id INT(10) UNSIGNED,
  description TEXT,
  mime_type INT(10),
  created_at TIMESTAMP,
  updated_at TIMESTAMP,

  FOREIGN KEY (author) REFERENCES users(id),
  FOREIGN KEY (task_id) REFERENCES tasks(id)
);

CREATE TABLE comments (
  id INT(10) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  author INT(10) UNSIGNED,
  task_id INT(10) UNSIGNED,
  description TEXT,
  created_at TIMESTAMP,
  updated_at TIMESTAMP,

  FOREIGN KEY (author) REFERENCES users(id),
  FOREIGN KEY (task_id) REFERENCES tasks(id)
);

CREATE TABLE checklists (
  id INT(10) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255)
);

CREATE TABLE tasks_checklists (
  task_id INT(10) UNSIGNED,
  checklist_id INT(10) UNSIGNED,

  PRIMARY KEY (task_id, checklist_id),
  FOREIGN KEY (task_id) REFERENCES tasks(id),
  FOREIGN KEY (checklist_id) REFERENCES checklists(id)
);

CREATE TABLE checklist_items (
  id INT(10) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  description TEXT,
  is_done BOOL,
  checklist_id INT(10) UNSIGNED,

  FOREIGN KEY (checklist_id) REFERENCES checklists(id)
);

#############################################################
COMMIT;