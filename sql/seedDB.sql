# USERS #################################################################################################
INSERT INTO users (auth_token, email, password, first_name, last_name, avatar_url, created_at, updated_at)
    VALUES (
        'user1_token',
        'user1@mail.com',
        '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5',
        'Irvin',
        'Mocny',
        'http://avatar.url/',
        '2016-04-06 01:21:27',
        '2016-04-06 01:21:27'
    );
INSERT INTO users (auth_token, email, password, first_name, last_name, avatar_url, created_at, updated_at)
    VALUES (
        'user2_token',
        'user2@mail.com',
        '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5',
        'Irvin',
        'Mocny',
        'http://avatar.url/',
        '2016-04-06 01:21:27',
        '2016-04-06 01:21:27'
    );
INSERT INTO users (auth_token, email, password, first_name, last_name, avatar_url, created_at, updated_at)
    VALUES (
        'user3_token',
        'user3@mail.com',
        '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5',
        'Irvin',
        'Mocny',
        'http://avatar.url/',
        '2016-04-06 01:21:27',
        '2016-04-06 01:21:27'
    );

# PROJECTS #################################################################################################
INSERT INTO projects (name, description, color, author_id, created_at, updated_at)
  VALUES (
      'project1',
      'project1',
      '1',
      '1',
      '2016-04-06 01:21:27',
      '2016-04-06 01:21:27'
  );
INSERT INTO projects (name, description, color, author_id, created_at, updated_at)
  VALUES (
    'project2',
    'project2',
    '2',
    '2',
    '2016-04-06 01:21:27',
    '2016-04-06 01:21:27'
  );

# PROJECT_USERS ############################################################################################
INSERT INTO project_users (project_id, user_id)
  VALUES (
      '1',
      '2'
  );
INSERT INTO project_users (project_id, user_id)
  VALUES (
    '1',
    '3'
  );
INSERT INTO project_users (project_id, user_id)
  VALUES (
    '2',
    '1'
  );

# TASKS ######################################################################################
INSERT INTO tasks (parent_task, name, description, state, due_date, expected_hours, owner_id, worked_time, priority, progress, project_id, created_at, updated_at)
    VALUES (
      '1',
      'task 1',
      'task number one',
      'new',
      '2016-04-30 01:21:27',
      '3',
      '1',
      '0',
      '3',
      '0',
      '1',
      '2016-04-06 01:21:27',
      '2016-04-06 01:21:27'
    );
INSERT INTO tasks (parent_task, name, description, state, due_date, expected_hours, owner_id, worked_time, priority, progress, project_id, created_at, updated_at)
  VALUES (
    '1',
    'task 2',
    'task number two',
    'assigned',
    '2016-06-30 01:21:27',
    '3',
    '1',
    '0',
    '1',
    '0',
    '1',
    '2016-04-06 01:21:27',
    '2016-04-06 01:21:27'
  );
INSERT INTO tasks (parent_task, name, description, state, due_date, expected_hours, owner_id, worked_time, priority, progress, project_id, created_at, updated_at)
  VALUES (
    '1',
    'task 3',
    'task number three',
    'done',
    '2016-05-30 01:21:27',
    '3',
    '1',
    '3',
    '2',
    '100',
    '1',
    '2016-04-06 01:21:27',
    '2016-04-06 01:21:27'
  );

# TASKS USERS ########################################################################
INSERT INTO users_tasks(task_id, user_id)
    VALUES (
      '2',
      '1'
    );
INSERT INTO users_tasks(task_id, user_id)
  VALUES (
    '3',
    '2'
  );

# labels ############################################################################
INSERT INTO labels(name)
    VALUES (
        'tasklabel'
    );

# TASKS LABELS #######################################################################
INSERT INTO tasks_labels(task_id, label_id)
  VALUES (
    '1',
    '1'
  );

#############################################################################################################
COMMIT;
