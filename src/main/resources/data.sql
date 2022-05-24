INSERT INTO role (name)
VALUES ('USER'),
       ('ADMIN');

INSERT INTO employee (first_name, login, password, patronymic, position, surname)
VALUES ('admin', 'admin', '$2a$10$5eSuRQNBty4fLOQvO4UkeeplhQa11QC.VrZK.3USq6pdNDQ4ApQKW', null, 'admin', 'admin');

INSERT INTO employee_roles (employee_id, roles_id)
VALUES (1, 1),
       (1, 2);

