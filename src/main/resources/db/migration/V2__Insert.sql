INSERT INTO users (username, password, email, role)
VALUES ('admin', '$2a$12$TYQ6JEoTBk/B7RtwL49Cce2g7tEROVED4TSx0PXeB4nAgrAkvnBlq', 'admin@gmail.com', 'ADMIN');

INSERT INTO users (username, password, email, role)
VALUES ('user', '$2a$12$5hzBBQcDD0nQhpLn9QfNCeBEi1VHHH07pi3rYm03zkK4vc1FtzbwS', 'user@gmail.com', 'USER');

INSERT INTO books (title, author, description, quantity, available_quantity)
VALUES
    ('Lalka', 'Bolesław Prus', 'Powieść realistyczna ukazująca społeczeństwo XIX-wiecznej Warszawy.', 10, 10),
    ('Quo Vadis', 'Henryk Sienkiewicz', 'Historia miłosna w czasach Nerona, nagrodzona Noblem.', 8, 9),
    ('Zbrodnia i kara', 'Fiodor Dostojewski', 'Klasyka literatury rosyjskiej, analizująca moralność i zbrodnię.', 6, 6),
    ('Pan Tadeusz', 'Adam Mickiewicz', 'Epopeja narodowa opisująca życie szlachty na Litwie.', 12, 12),
    ('Ferdydurke', 'Witold Gombrowicz', 'Satyra na społeczne konwenanse i system edukacji.', 7, 7),
    ('Solaris', 'Stanisław Lem', 'Filozoficzna powieść science fiction o kontaktach z obcą cywilizacją.', 9, 9),
    ('Kamienie na szaniec', 'Aleksander Kamiński', 'Opowieść o młodych bohaterach walczących w okupowanej Polsce.', 10, 10),
    ('Król', 'Szczepan Twardoch', 'Powieść gangsterska osadzona w przedwojennej Warszawie.', 5, 5),
    ('Dom dzienny, dom nocny', 'Olga Tokarczuk', 'Oniryczna opowieść o życiu na pograniczu kultur i czasów.', 6, 6),
    ('Inny świat', 'Gustaw Herling-Grudziński', 'Wstrząsające świadectwo życia w sowieckim łagrze.', 7, 7);

INSERT INTO book_reservations (user_id, book_id, reservation_date, expiration_date, active)
VALUES
    (1, 1, '2023-10-10T10:00:00', '2023-10-20T10:00:00', true),
    (2, 2, '2023-10-11T11:00:00', '2023-10-21T11:00:00', true),
    (1, 3, '2023-10-12T12:00:00', '2023-10-22T12:00:00', true),
    (2, 4, '2023-10-13T13:00:00', '2023-10-23T13:00:00', true),
    (1, 5, '2023-10-14T14:00:00', '2023-10-24T14:00:00', false);