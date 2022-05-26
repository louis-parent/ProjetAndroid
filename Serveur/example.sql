DELETE FROM User;
DELETE FROM ParticularUser;
DELETE FROM ProfessionalUser;
DELETE FROM Announcement;
DELETE FROM Alert;
DELETE FROM Favorite;
DELETE FROM Message;
DELETE FROM Notification;

INSERT INTO User(id, email, password, address, zip, city, country, phone, isProfessional)
VALUES (1, 'jean.dupont@email.com', '1234', '1 rue Ailleurs', '00000', 'Ici', 'La-bas', '+33000000000', FALSE);

INSERT INTO ParticularUser(id, civility, firstName, lastName)
VALUES (1, 'MISTER', 'Jean', 'Dupont');

INSERT INTO User(id, email, password, address, zip, city, country, phone, isProfessional)
VALUES (2, 'contact@corporation.com', '1234', '1 rue Ailleurs', '00000', 'Ici', 'La-bas', '+33000000000', TRUE);

INSERT INTO ProfessionalUser(id, name, siret, isPremium)
VALUES (2, 'Corporation', '000000000000000000000000000', FALSE);

INSERT INTO Announcement(id, type, brand, model, price, address, zip, city, country, kilometers, year, technicalControl, energy, gearbox, exteriorColor, interiorColor, doors, places, formerOwnerCount, power, din, co2, consumption, author, isEnhance)
VALUES (1, 'RENT', 'Fiat', 'Panda', 90, '1 rue Ailleurs', '00000', 'Ici', 'La-bas', 12000, 2015, TRUE, 'DIESEL', 'MANUAL', 'Blanc Métalisé', 'Noir', 5, 5, 1, 6, 8, 2.4, 6, 2, FALSE);

INSERT INTO Announcement(id, type, brand, model, price, address, zip, city, country, kilometers, year, technicalControl, energy, gearbox, exteriorColor, interiorColor, doors, places, formerOwnerCount, power, din, co2, consumption, author, isEnhance)
VALUES (2, 'SELL', 'Renault', 'Twingo', 3200, '1 rue Ailleurs', '00000', 'Ici', 'La-bas', 35000, 2009, TRUE, 'GASOLINE', 'MANUAL', 'Violet Métalisé', 'Blanc Cassé', 3, 4, 2, 4, 6, 3.2, 7.3, 1, FALSE);

INSERT INTO Alert(id, user, enabled, search, type, brand, model, maxPrice, minPrice, maxKilometers, minYear, maxYear, technicalControlRequired, energy, gearbox, minPlaces, announcer)
VALUES (1, 1, TRUE, NULL, 'ANY', NULL, NULL, 100000, 0, 100000, 1971, 2022, TRUE, 'ANY', 'ANY', 1, NULL);

INSERT INTO Favorite(id, user, announcement)
VALUES (1, 1, 1);

INSERT INTO Message(id, announcement, source, destination, text, dateTime)
VALUES (1, 1, 1, 2, 'Bonjour, je suis interressé par votre annonce.', datetime('now'));

INSERT INTO Message(id, announcement, source, destination, text, dateTime)
VALUES (2, 1, 2, 1, 'Bonjour, pour quelle date ?', datetime('now'));

INSERT INTO Notification (id, dateTime, type, data, user, seen, notified)
VALUES (1, datetime('now'), 'MESSAGE', 4588, 1, FALSE, FALSE);

INSERT INTO Notification (id, dateTime, type, data, user, seen, notified)
VALUES (2, datetime('now'), 'ALERT', 1, 1, TRUE, TRUE);
