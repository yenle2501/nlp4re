INSERT INTO precondition (KEY_NAME, REGEX, REQUIRED) 
VALUES
('if', '^if+',0),
('while', '^while+ ',0),
('during', '^during+ ',0),
('after', '^after+ ',0),
('before', '^before+ ',0),
('assoonas', '^as soon as+ ',0),
('incase', '^in case [\\w\\s]+ is included',0);


INSERT INTO systemname (KEY_NAME, REGEX, REQUIRED) 
VALUES
('all', '^all systems of the ',1),
('some', '^some systems of the ',1),
('those', '^those systems of the ',1),
('the', '^the ',1);

INSERT INTO modalverb (KEY_NAME, REQUIRED) 
VALUES 
('SHOULD',1),
('SHALL',1),
('COULD',1),
('WILL',1),
('MUST',1);

INSERT INTO activities (KEY_NAME, REGEX, REQUIRED) 
VALUES
('provide', 'provide [:alpha:] with the ability to [:alpha:]',1),
('be_able_to', 'be able to +',1);

INSERT INTO objects (KEY_NAME, REGEX, REQUIRED) 
VALUES
('a', '^a +',1),
('an', '^an +',1),
('the', '^the +',1),
('one', '^one +',1),
('each', '^each +',1),
('between', '^between [:alpha:] and +',1),
('all_the', '^all the +',1);

INSERT INTO details (KEY_NAME, REGEX, REQUIRED) 
VALUES
('condition', 'if and only if+',0);
