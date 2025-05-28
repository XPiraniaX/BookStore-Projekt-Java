INSERT INTO users (username, password, email, role)
VALUES ('admin', '$2a$12$0m7puby1aJ7hFC2i0yb4euV7dofmH30zokXeOsDWLoAI03ipuZE9S', 'admin@gmail.com', 'ADMIN');

INSERT INTO users (username, password, email, role)
VALUES ('user', '$2a$12$0m7puby1aJ7hFC2i0yb4euV7dofmH30zokXeOsDWLoAI03ipuZE9S', 'user@gmail.com', 'USER');

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
