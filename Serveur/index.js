const PORT = 3000; // TODO Modify to change the listening port

const express = require("express");
const bodyParser = require('body-parser');
const sqlite3 = require('sqlite3').verbose();
const fs = require('fs');

String.prototype.hashCode = function() {
	var hash = 0, i, chr;
	if (this.length === 0) return hash;
	
	for (i = 0; i < this.length; i++)
	{
		chr   = this.charCodeAt(i);
		hash  = ((hash << 5) - hash) + chr;
		hash |= 0; // Convert to 32bit integer
	}
	return hash;
};


function onDatabaseConnect(error)
{
	 if(error !== null)
	{
		console.error("❌ Database connection failed", error);
		process.exit(1);
	}
	else
	{
		console.log("✅ Successfully connected to database");
		loadSchema();
	}
}

function loadSchema()
{
	try
	{
		const data = fs.readFileSync("./schema.sql", "utf8");
		
		database.exec(data, (error) => {
			if(error !== null)
			{
				console.error("❌ SQL schema loading failed", error);
				process.exit(1);
			}
			else
			{
				console.log("✅ Successfully loaded SQL schema");
				loadExampleData(); // TODO Comment to prevent database resetting at startup
			}
		});
	}
	catch(error)
	{
		console.error("❌ SQL schema loading failed", error);
		process.exit(1);
	}
}

function loadExampleData()
{
	try
	{
		const data = fs.readFileSync("./example.sql", "utf8");
		
		database.exec(data, (error) => {
			 if(error !== null)
			{
				console.error("❌ Example data loading failed", error);
				process.exit(1);
			}
			else
			{
				console.log("✅ Successfully loaded example data");
			}
		});
	}
	catch(error)
	{
		console.error("❌ Example data loading failed", error);
		process.exit(1);
	}
}

function onServerOpen()
{
	console.log("✅ Successfully starting server");
}

function onServerListen()
{
	console.log("✅ Successfully listening for client");
}

function onServerClose()
{
	console.log("🚫 Server is shutingdown");
	database.close();
}

function generateConversationId(announcement, user1, user2)
{
	const announcementHash = new String(announcement).hashCode();
	const user1Hash = new String(user1).hashCode();
	const user2Hash = new String(user2).hashCode();
	return (announcementHash * 31) + (user1Hash * 31) + (user2Hash * 31);
}

const database = new sqlite3.Database('./data.sqlite', sqlite3.OPEN_READWRITE | sqlite3.OPEN_CREATE | sqlite3.OPEN_FULLMUTEX, onDatabaseConnect);
const app = express();

app.use(bodyParser.json());

app.use((request, response, next) => {
	console.log("📨", request.method, request.originalUrl);
	next();
});

/**
 * USERs
**/
app.get('/user', (request, response) => {
	database.all("SELECT * FROM ParticularUser;", (error, particulars) => {
		if(error !== null)
		{
			response.status(500).end();
		}
		else
		{
			database.all("SELECT * FROM ProfessionalUser;", (error, professionals) => {
				if(error !== null)
				{
					response.status(500).end();
				}
				else
				{
					database.all("SELECT * FROM User;", (error, rows) => {
						if(error !== null)
						{
							response.status(500).end();
						}
						else
						{
							for(let row of rows)
							{
								row.address = {
									"address": row.address,
									"zip": row.zip,
									"city": row.city,
									"country": row.country 
								};
								
								row.zip = undefined;
								row.city = undefined;
								row.country = undefined;
								
								row.isProfessional = row.isProfessional === 1;
								
								if(row.isProfessional)
								{
									for(let professional of professionals)
									{
										if(professional.id === row.id)
										{
											row.name = professional.name;
											row.siret = professional.siret;
											row.isPremium = professional.isPremium === 1;
										}
									}
								}
								else
								{
									for(let particular of particulars)
									{
										if(particular.id === row.id)
										{
											row.civility = particular.civility;
											row.firstName = particular.firstName;
											row.lastName = particular.lastName;
										}
									}
								}
							}
							
							response.json(rows);
						}
					});
				}
			});
		}
	});
});

app.get('/user/:id', (request, response) => {
	const id = request.params.id;

	database.get("SELECT * FROM User WHERE id = ?;", id, (error, row) => {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(row === undefined)
		{
			response.status(404).end();
		}
		else
		{
			row.address = {
				"address": row.address,
				"zip": row.zip,
				"city": row.city,
				"country": row.country 
			};
			
			row.zip = undefined;
			row.city = undefined;
			row.country = undefined;
			
			row.isProfessional = row.isProfessional === 1;
			
			if(row.isProfessional)
			{
				database.get("SELECT * FROM ProfessionalUser WHERE id = ?;", id, (error, professional) => {
					if(error !== null)
					{
						response.status(500).end();
					}
					else
					{
						row.name = professional.name;
						row.siret = professional.siret;
						row.isPremium = professional.isPremium === 1;
						
						response.json(row);
					}
				});
			}
			else
			{
				database.get("SELECT * FROM ParticularUser WHERE id = ?;", id, (error, particular) => {
					if(error !== null)
					{
						response.status(500).end();
					}
					else
					{
						row.civility = particular.civility;
						row.firstName = particular.firstName;
						row.lastName = particular.lastName;
						
						response.json(row);
					}
				});
			}
		}
	});
});

app.post('/user', (request, response) => {
	const body = request.body;
	
	database.run("CREATE User(email, password, address, zip, city, country, phone, isProfessional) VALUES ($email, $password, $address, $zip, $city, $country, $phone, $isProfessional);", {
		$email: body.email,
		$password: body.password,
		$address: body.address.address,
		$zip: body.address.zip,
		$city: body.address.city,
		$country: body.address.country,
		$phone: body.phone,
		$isProfessional: (body.isProfessional ? 1 : 0)
	}, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else
		{
			const id = this.lastID;
			
			if(body.isProfessional)
			{
				database.run("CREATE ProfessionalUser(id, name, siret, isPremium) VALUES($id, $name, $siret, $isPremium);", {
					$id: id,
					$name: body.name,
					$siret: body.siret,
					$isPremium: (body.isPremium ? 1 : 0)
				}, error => {
					 if(error !== null)
					{
						response.status(500).end();
					}
					else
					{
						response.json(id);
					}
				});
			}
			else
			{
				database.run("CREATE ParticularUser(id, civility, firstName, lastName) VALUES ($id, $civility, $firstName, $lastName);", {
					$id: id,
					$civility: body.civility,
					$firstName: body.firstName,
					$lastName: body.lastName
				}, error => {
					 if(error !== null)
					{
						response.status(500).end();
					}
					else
					{
						response.json(id);
					}
				});
			}

		}
	});
});

app.put('/user/:id', (request, response) => {
	const body = request.body;
	const id = request.params.id;
	
	database.run("UPDATE User SET email = $email, password = $password, address = $address, zip = $zip, city = $city, country = $country, phone = $phone, isProfessional = $isProfessional WHERE id = $id;", {
		$id: id,
		$email: body.email,
		$password: body.password,
		$address: body.address.address,
		$zip: body.address.zip,
		$city: body.address.city,
		$country: body.address.country,
		$phone: body.phone,
		$isProfessional: (body.isProfessional ? 1 : 0)
	}, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(this.changes === 0)
		{
			response.status(404).end();
		}
		else
		{
			if(body.isProfessional)
			{
				database.run("UPDATE ProfessionalUser SET name = $name, siret = $siret, isPremium = $isPremium WHERE id = $id;", {
					$id: id,
					$name: body.name,
					$siret: body.siret,
					$isPremium: (body.isPremium ? 1 : 0)
				}, function(error) {
					if(error !== null)
					{
						response.status(500).end();
					}
					else if(this.changes === 0)
					{
						response.status(404).end();
					}
					else
					{
						response.status(200).end();
					}
				});
			}
			else
			{
				database.run("UPDATE ParticularUser SET civility = $civility, firstName = $firstName, lastName = $lastName WHERE id = $id;", {
					$id: id,
					$civility: body.civility,
					$firstName: body.firstName,
					$lastName: body.lastName
				}, function(error) {
					if(error !== null)
					{
						response.status(500).end();
					}
					else if(this.changes === 0)
					{
						response.status(404).end();
					}
					else
					{
						response.status(200).end();
					}
				});
			}

		}
	});
});

app.delete('/user/:id', (request, response) => {
	const id = request.params.id;
	
	database.run("DELETE FROM User WHERE id = ?;", id, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(this.changes === 0)
		{
			response.status(404).end();
		}
		else
		{
			database.run("DELETE FROM ParticularUser WHERE id = ?;", id, error1 => {
				database.run("DELETE FROM ProfessionalUser WHERE id = ?;", id, error2 => {
					if(error1 && error2)
					{
						response.status(500).end();
					}
					else
					{
						response.status(200).end();
					}		
				});
			});
		}
	});
});

/**
* NOTIFICATIONs
**/

app.get('/notification', (request, response) => {
	database.all("SELECT * FROM Notification;", (error, rows) => {
		if(error !== null)
		{
			response.status(500).end();
		}
		else
		{
			for(let row of rows)
			{				
				row.seen = row.seen === 1;
				row.notified = row.notified === 1;
			}
			
			response.json(rows);
		}
	});
});

app.get('/notification/:id', (request, response) => {
	const id = request.params.id;
	
	database.get("SELECT * FROM Notification WHERE id = ?;", id, (error, row) => {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(row === undefined)
		{
			response.status(404).end();
		}
		else
		{	
			row.seen = row.seen === 1;
			row.notified = row.notified === 1;	
			
			response.json(row);
		}
	});
});

app.post('/notification', (request, response) => {
	const body = request.body;
	
	database.run("INSERT INTO Notification(dateTime, type, data, user, seen, notified) VALUES ($dateTime, $type, $data, $user, $seen, $notified);", {
		$dateTime: body.dateTime,
		$type: body.type,
		$data: body.data,
		$user: body.user,
		$seen: (body.seen ? 1 : 0),
		$notified: (body.notified ? 1 : 0)
	}, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else
		{
			response.json(this.lastID);
		}
	});
});

app.put('/notification/:id', (request, response) => {
	const id = request.params.id;
	const body = request.body;
	
	database.run("UPDATE Notification SET dateTime = $dateTime, type = $type, data = $data, user = $user, seen = $seen, notified = $notified WHERE id = $id;", {
		$id: id,
		$dateTime: body.dateTime,
		$type: body.type,
		$data: body.data,
		$user: body.user,
		$seen: (body.seen ? 1 : 0),
		$notified: (body.notified ? 1 : 0)
	}, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(this.changes === 0)
		{
			response.status(404).end();
		}
		else
		{
			response.status(200).end();
		}
	});
});

app.delete('/notification/:id', (request, response) => {
	const id = request.params.id;
	
	database.run("DELETE FROM Notification WHERE id = ?;", id, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(this.changes === 0)
		{
			response.status(404).end();
		}
		else
		{
			response.status(200).end();
		}
	});
});

/**
* MESSAGEs
**/

app.get('/message', (request, response) => {
	database.all("SELECT * FROM Message;", (error, rows) => {
		if(error !== null)
		{
			response.status(500).end();
		}
		else
		{		
			response.json(rows);
		}
	});
});

app.get('/message/:id', (request, response) => {
	const id = request.params.id; 
	
	database.get("SELECT * FROM Message WHERE id = ?;", id, (error, row) => {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(row === undefined)
		{
			response.status(404).end();
		}
		else
		{
			response.json(row);		
		}
	});
});

app.post('/message', (request, response) => {
	const body = request.body;
	
	database.run("INSERT INTO Message (announcement, source, destination, text, dateTime) VALUES ($announcement, $source, $destination, $text, $dateTime);", {
		$announcement: body.announcement,
		$source: body.source,
		$destination: body.destination,
		$text: body.text,
		$dateTime: body.dateTime
	}, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else
		{
			const messageId = this.lastID;
			
			database.run("INSERT INTO Notification(dateTime, type, data, user, seen, notified) VALUES (datetime('now'), 'MESSAGE', $data, $user, 0, 0);", {
				$data: generateConversationId(body.announcement, body.source, body.destination),
				$user: body.destination
			}, function(error) {
				if(error !== null)
				{
					console.error(error);
				}
				else
				{
					console.log("NOTIFIED");
				}
			});
			
			response.json(messageId);
		}
	});
});

app.put('/message/:id', (request, response) => {
	const id = request.params.id;
	const body = request.body;
	
	database.run("UPDATE Message SET announcement = $announcement, source = $source, destination = $destination, text = $text, dateTime = $dateTime WHERE id = $id;", {
		$id: id,
		$announcement: body.announcement,
		$source: body.source,
		$destination: body.destination,
		$text: body.text,
		$dateTime: body.dateTime
	}, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(this.changes === 0)
		{
			response.status(404).end();
		}
		else
		{
			response.status(200).end();		
		}
	});
});

app.delete('/message/:id', (request, response) => {
	const id = request.params.id;
	
	database.run("DELETE FROM Message WHERE id = ?;", id, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(this.changes === 0)
		{
			response.status(404).end();
		}
		else
		{
			response.status(200).end();		
		}
	});
});

/**
* FAVORITEs
**/

app.get('/favorite', (request, response) => {
	database.all("SELECT * FROM Favorite;", (error, rows) => {
		 if(error !== null)
		{
			response.status(500).end();
		}
		else
		{
			response.json(rows);
		}
	});
});

app.get('/favorite/:id', (request, response) => {
	const id = request.params.id;
	
	database.get("SELECT * FROM Favorite WHERE id = ?;", id, (error, row) => {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(row === undefined)
		{
			response.status(404).end();
		}
		else
		{
			response.json(row);
		}
	});
});

app.post('/favorite', (request, response) => {
	const body = request.body;
	
	database.run("INSERT INTO Favorite (user, announcement) VALUES ($user, $announcement);", {
		$user: body.user,
		$announcement: body.announcement
	}, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else
		{
			response.json(this.lastID);
		}
	});
});

app.put('/favorite/:id', (request, response) => {
	const id = request.params.id;
	const body = request.body;
	
	database.run("UPDATE Favorite SET user = $user, announcement = $announcement WHERE id = $id;", {
		$id: id,
		$user: body.user,
		$announcement: body.announcement
	}, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(this.changes === 0)
		{
			response.status(404).end();
		}
		else
		{
			response.status(200).end();
		}
	});
});

app.delete('/favorite/:id', (request, response) => {
	const id = request.params.id;
	
	database.run("DELETE FROM Favorite WHERE id = ?;", id, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(this.changes === 0)
		{
			response.status(404).end();
		}
		else
		{
			response.status(200).end();
		}
	});
});

/**
* CONVERSATIONs
**/

function computeConversations(callback)
{
	database.all("SELECT * FROM Message;", (error, rows) => {
		if(error !== null)
		{
			callback(error, new Array());
		}
		else
		{
			let conversations = new Array();
			
			for(let row of rows)
			{
				const id = generateConversationId(row.announcement, row.source, row.destination); 
				
				const notAlreadyExists = conversations.filter((conversation) => {
					return conversation.id === id;
				}).length === 0;
				
				if(notAlreadyExists)
				{
					conversations.push({
						id: id,
						announcement: row.announcement,
						user1: row.source,
						user2: row.destination
					});
				}
			}
			
			callback(null, conversations);
		}
	});
}

app.get('/conversation', (request, response) => {
	computeConversations((error, conversations) => {
		if(error !== null)
		{
			response.status(500).end();
		}
		else
		{
			response.json(conversations);
		}
	});
});

app.get('/conversation/:id', (request, response) => {
	const id = parseInt(request.params.id);

	computeConversations((error, conversations) => {
		if(error !== null)
		{
			response.status(500).end();
		}
		else
		{
			let identified = null;

			for(let conversation of conversations)
			{
				if(conversation.id === id)
				{
					identified = conversation;
				}
			}

			if(identified === null)
			{
				response.status(404).end();
			}
			else
			{
				response.json(identified);
			}			
		}
	});
});

/**
* ANNOUNCEMENTs
**/

app.get('/announcement', (request, response) => {
	database.all("SELECT * FROM Announcement;", (error, rows) => {
		if(error !== null)
		{
			response.status(500).end();
		}
		else
		{
			for(let row of rows)
			{
				row.address = {
					"address": row.address,
					"zip": row.zip,
					"city": row.city,
					"country": row.country 
				};
				
				row.zip = undefined;
				row.city = undefined;
				row.country = undefined;
				
				row.technicalControl = row.technicalControl === 1;
				row.isEnhance = row.isEnhance === 1;
			}
			
			response.json(rows);
		}
	});
});

app.get('/announcement/:id', (request, response) => {
	const id = request.params.id;
	
	database.get("SELECT * FROM Announcement WHERE id = ?;", id, (error, row) => {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(row === undefined)
		{
			response.status(404).end();
		}
		else
		{
			row.address = {
				"address": row.address,
				"zip": row.zip,
				"city": row.city,
				"country": row.country 
			};
			
			row.zip = undefined;
			row.city = undefined;
			row.country = undefined;
			
			row.technicalControl = row.technicalControl === 1;
			row.isEnhance = row.isEnhance === 1;
			
			response.json(row);
		}
	});
});

function match(alert, announcement)
{
	let match = alert.enabled;
	
	const lowerSearch = alert.search.toLowerCase();
	
	match &= search == null ||
	        announcement.brand.toLowerCase().includes(lowerSearch) ||
	        announcement.model.toLowerCase().includes(lowerSearch) ||
	        announcement.type.toLowerCase().includes(lowerSearch) ||
	        announcement.year.toString().includes(lowerSearch) ||
	        announcement.gearbox.toLowerCase().includes(lowerSearch) ||
	        announcement.energy.toLowerCase().includes(lowerSearch);
            
	match &= alert.type == "ANY" || alert.type == announcement.type;
	match &= alert.brand == null || (announcement.brand.toLowerCase().includes(alert.brand.toLowerCase()));
	match &= alert.model == null || (announcement.model.toLowerCase().includes(alert.model.toLowerCase())); 
	match &= alert.maxPrice == null || (alert.maxPrice >= announcement.price);
	match &= alert.minPrice == null || (alert.minPrice <= announcement.price);
	match &= alert.maxKilometers == null || (alert.maxKilometers >= announcement.kilometers);
	match &= alert.maxYear == null || (alert.maxYear >= announcement.year);
	match &= alert.minYear == null || (alert.minYear <= announcement.year);
	match &= alert.technicalControlRequired !== true || alert.technicalControlRequired == announcement.technicalControl;
	match &= alert.energy == null || alert.energy == announcement.energy;
	match &= alert.gearbox == null || alert.gearbox == announcement.gearbox;
	match &= alert.minPlaces == null || (alert.minPlaces <= announcement.places);
	match &= alert.announcer == null || alert.announcer == announcement.author;

	return match;
}

app.post('/announcement', (request, response) => {
	const body = request.body;
	console.log(body)
	
	database.run("INSERT INTO Announcement(type, brand, model, price, address, zip, city, country, kilometers, year, technicalControl, energy, gearbox, exteriorColor, interiorColor, doors, places, formerOwnerCount, power, din, co2, consumption, author, isEnhance) VALUES ($type, $brand, $model, $price, $address, $zip, $city, $country, $kilometers, $year, $technicalControl, $energy, $gearbox, $exteriorColor, $interiorColor, $doors, $places, $formerOwnerCount, $power, $din, $co2, $consumption, $author, $isEnhance);", {
		$type: body.type,
		$brand: body.brand,
		$model: body.model,
		$price: body.price,
		$address: body.address.address,
		$zip: body.address.zip,
		$city: body.address.city,
		$country: body.address.country,
		$kilometers: body.kilometers,
		$year: body.year,
		$technicalControl: (body.technicalControltechnicalControl ? 1 : 0),
		$energy: body.energy,
		$gearbox: body.gearbox,
		$exteriorColor: body.exteriorColor,
		$interiorColor: body.interiorColor,
		$doors: body.doors,
		$places: body.places,
		$formerOwnerCount: body.formerOwnerCount,
		$power: body.power,
		$din: body.din,
		$co2: body.co2,
		$consumption: body.consumption,
		$author: body.author,
		$isEnhance: (body.isEnhance ? 1 : 0)
	}, function(error) {
		console.log(error, this);
		if(error !== null)
		{
			response.status(500).end();
		}
		else
		{
			const announcementId = this.lastID;
			
			database.all("SELECT * FROM Alert;", (error, alerts) => {
				for(let alert of alerts)
				{
					alert.enabled = alert.enabled === 1;
					alert.technicalControlRequired = alert.technicalControlRequired === 1;
					
					if(match(alert, body))
					{
						database.run("INSERT INTO Notification(dateTime, type, data, user, seen, notified) VALUES (datetime('now'), 'MESSAGE', $data, $user, 0, 0);", {
							$data: announcementId,
							$user: alert.user
						}, function(error) {});
					}
				}
			});
			
			response.json(announcementId);
		}
	});
});

app.put('/announcement/:id', (request, response) => {
	const id = request.params.id;
	const body = request.body;
	
	database.run("UPDATE Announcement SET type = $type, brand = $brand, model = $model, price = $price, address = $address, zip = $zip, city = $city, country = $country, kilometers = $kilometers, year = $year, technicalControl = $technicalControl, energy = $energy, gearbox = $gearbox, exteriorColor = $exteriorColor, interiorColor = $interiorColor, doors = $doors, places = $places, formerOwnerCount = $formerOwnerCount, power = $power, din = $din, co2 = $co2, consumption = $consumption, author = $author, isEnhance = $isEnhance WHERE id = $id;", {
		$id: id,
		$type: body.type,
		$brand: body.brand,
		$model: body.model,
		$price: body.price,
		$address: body.address.address,
		$zip: body.address.zip,
		$city: body.address.city,
		$country: body.address.country,
		$kilometers: body.kilometers,
		$year: body.year,
		$technicalControl: (body.technicalControltechnicalControl ? 1 : 0),
		$energy: body.energy,
		$gearbox: body.gearbox,
		$exteriorColor: body.exteriorColor,
		$interiorColor: body.interiorColor,
		$doors: body.doors,
		$places: body.places,
		$formerOwnerCount: body.formerOwnerCount,
		$power: body.power,
		$din: body.din,
		$co2: body.co2,
		$consumption: body.consumption,
		$author: body.author,
		$isEnhance: (body.isEnhance ? 1 : 0)
	}, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(this.changes === 0)
		{
			response.status(404).end();
		}
		else
		{
			response.status(200).end();
		}
	});
});

app.delete('/announcement/:id', (request, response) => {
	const id = request.params.id;
	
	database.run("DELETE FROM Announcement WHERE id = ?;", id, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(this.changes === 0)
		{
			response.status(404).end();
		}
		else
		{
			response.status(200).end();
		}
	});
});


/**
* ALERTs
**/

app.get('/alert', (request, response) => {
	database.all("SELECT * FROM Alert;", (error, rows) => {
		if(error !== null)
		{
			response.status(500).end();
		}
		else
		{
			for(let row of rows)
			{
				row.filters = {
					search: row.search,
					type: row.type,
					brand: row.brand,
					model: row.model,
					maxPrice: row.maxPrice,
					minPrice: row.minPrice,
					maxKilometers: row.maxKilometers,
					minYear: row.minYear,
					maxYear: row.maxYear,
					technicalControlRequired: row.technicalControlRequired === 1,
					energy: row.energy,
					gearbox: row.gearbox,
					minPlaces: row.minPlaces,
					announcer: row.announcer
				};
				
				row.search = undefined;
				row.type = undefined;
				row.brand = undefined;
				row.model = undefined;
				row.maxPrice = undefined;
				row.minPrice = undefined;
				row.maxKilometers = undefined;
				row.minYear = undefined;
				row.maxYear = undefined;
				row.technicalControlRequired = undefined;
				row.energy = undefined;
				row.gearbox = undefined;
				row.minPlaces = undefined;
				row.announcer = undefined;
				
				row.enabled = row.enabled === 1;
			}
		
			response.json(rows);
		}
	});
});

app.get('/alert/:id', (request, response) => {
	const id = request.params.id;
	
	database.get("SELECT * FROM Alert WHERE id = ?;", id, (error, row) => {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(row === undefined)
		{
			response.status(404).end();
		}
		else
		{
			row.filters = {
				search: row.search,
				type: row.type,
				brand: row.brand,
				model: row.model,
				maxPrice: row.maxPrice,
				minPrice: row.minPrice,
				maxKilometers: row.maxKilometers,
				minYear: row.minYear,
				maxYear: row.maxYear,
					technicalControlRequired: row.technicalControlRequired === 1,
				energy: row.energy,
				gearbox: row.gearbox,
				minPlaces: row.minPlaces,
				announcer: row.announcer
			};
			
			row.search = undefined;
			row.type = undefined;
			row.brand = undefined;
			row.model = undefined;
			row.maxPrice = undefined;
			row.minPrice = undefined;
			row.maxKilometers = undefined;
			row.minYear = undefined;
			row.maxYear = undefined;
			row.technicalControlRequired = undefined;
			row.energy = undefined;
			row.gearbox = undefined;
			row.minPlaces = undefined;
			row.announcer = undefined;
			
			row.enabled = row.enabled === 1;
			
			response.json(row);
		}
	});
});

app.post('/alert', (request, response) => {
	const body = request.body;
	
	database.run("INSERT INTO Alert (user, enabled, search, type, brand, model, maxPrice, minPrice, maxKilometers, minYear, maxYear, technicalControlRequired, energy, gearbox, minPlaces, announcer) VALUES ($user, $enabled, $search, $type, $brand, $model, $maxPrice, $minPrice, $maxKilometers, $minYear, $maxYear, $technicalControlRequired, $energy, $gearbox, $minPlaces, $announcer);", {
		$user: body.user,
		$enabled: (body.enabled ? 1 : 0),
		$search: body.filters.search,
		$type: body.filters.type,
		$brand: body.filters.brand,
		$model: body.filters.model,
		$maxPrice: body.filters.maxPrice,
		$minPrice: body.filters.minPrice,
		$maxKilometers: body.filters.maxKilometers,
		$minYear: body.filters.minYear,
		$maxYear: body.filters.maxYear,
		$technicalControlRequired: (body.filters.technicalControlRequired ? 1 : 0),
		$energy: body.filters.energy,
		$gearbox: body.filters.gearbox,
		$minPlaces: body.filters.minPlaces,
		$announcer: body.filters.announcer
	}, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else
		{
			response.json(this.lastID);
		}
	});
});

app.put('/alert/:id', (request, response) => {
	const id = request.params.id;
	const body = request.body;
	
	database.run("UPDATE Alert SET user = $user, enabled = $enabled, search = $search, type = $type, brand = $brand, model = $model, maxPrice = $maxPrice, minPrice = $minPrice, maxKilometers = $maxKilometers, minYear = $minYear, maxYear = $maxYear, technicalControlRequired = $technicalControlRequired, energy = $energy, gearbox = $gearbox, minPlaces = $minPlaces, announcer = $announcer WHERE id = $id;", {
		$id: id,
		$user: body.user,
		$enabled: (body.enabled ? 1 : 0),
		$search: body.filters.search,
		$type: body.filters.type,
		$brand: body.filters.brand,
		$model: body.filters.model,
		$maxPrice: body.filters.maxPrice,
		$minPrice: body.filters.minPrice,
		$maxKilometers: body.filters.maxKilometers,
		$minYear: body.filters.minYear,
		$maxYear: body.filters.maxYear,
		$technicalControlRequired: (body.filters.technicalControlRequired ? 1 : 0),
		$energy: body.filters.energy,
		$gearbox: body.filters.gearbox,
		$minPlaces: body.filters.minPlaces,
		$announcer: body.filters.announcer
	}, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(this.changes === 0)
		{
			response.status(404).end();
		}
		else
		{
			response.status(200).end();
		}
	});
});

app.delete('/alert/:id', (request, response) => {
	const id = request.params.id;
	
	database.run("DELETE FROM Alert WHERE id = ?;", id, function(error) {
		if(error !== null)
		{
			response.status(500).end();
		}
		else if(this.changes === 0)
		{
			response.status(404).end();
		}
		else
		{
			response.status(200).end();
		}
	});
});

app.listen(PORT, onServerListen);
process.on("exit", onServerClose);
onServerOpen();
