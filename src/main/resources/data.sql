INSERT INTO role (name)
VALUES ('USER'),
       ('ADMIN');

INSERT INTO employee (first_name, login, password, patronymic, position, surname)
VALUES ('admin', 'admin', '$2a$10$5eSuRQNBty4fLOQvO4UkeeplhQa11QC.VrZK.3USq6pdNDQ4ApQKW', null, 'admin', 'admin'),
       ('Сергей', 'merakses', '$2a$10$wfaOJTF5j3LTaILuxB0jYu15s0m/MkqOI0hehEK5bloxdzGxHYok.', 'Александрович', 'Стажер', 'Лосев'),
       ('Светлана', 'login', '$2a$10$wfaOJTF5j3LTaILuxB0jYu15s0m/MkqOI0hehEK5bloxdzGxHYok.', 'Андреевна', 'Тестировщик', 'Маткина'),
       ('Иван', 'ivan', '$2a$10$wfaOJTF5j3LTaILuxB0jYu15s0m/MkqOI0hehEK5bloxdzGxHYok.', 'Иванович', 'Разработчик', 'Иванов'),
       ('Пётр', 'petr', '$2a$10$wfaOJTF5j3LTaILuxB0jYu15s0m/MkqOI0hehEK5bloxdzGxHYok.', 'Петрович', 'HR', 'Петров');

INSERT INTO employee_role (employee_id, role_id)
VALUES (1, 1),
       (1, 2),
       (2, 1);

INSERT INTO project (name, description, creator_id)
VALUES ('Менеджер проектов',
        'Программное средство "Веб-сервис для организации и управления проектами и задачами для IT компании с использованием фреймворка Spring".',
        2),
       ('Project name', 'description', 2),
       ('name', 'description', 3),
       ('Project editor', 'description', 3),
       ('Project', 'description', 1);

INSERT INTO project_participants (project_id, participant_id)
VALUES (1, 2),
       (1, 3),
       (2, 2),
       (4, 2),
       (4, 3),
       (5, 1);

