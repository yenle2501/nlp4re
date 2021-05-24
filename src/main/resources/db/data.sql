INSERT INTO CONDITIONS (KEY_NAME, REGEX, REQUIRED) 
VALUES
('if', '^if+',0),
('while_during', '^while|during+ ',0),
('after', '^after|before|as soon as+ ',0),
('incase', '^in case [:alpha:] is included+',0);


INSERT INTO SYSTEMNAME (KEY_NAME, REGEX, REQUIRED) 
VALUES
('all_some', '^all|some systems of the [\\w\\s]+',1),
('those', '^those systems of the [\\w\\s]+',1),
('the', '^the [\\w\\s]+',1);

INSERT INTO MODAL (KEY_NAME, REQUIRED) 
VALUES 
('SHOULD',1),
('SHALL',1),
('COULD',1),
('WILL',1),
('MUST',1);

INSERT INTO ANCHOR (KEY_NAME, REGEX, REQUIRED) 
VALUES
('provide', 'provide [\\w\\s]+ with the ability to [\\w\\s]',1),
('be_able_to', 'be able to +',1);

INSERT INTO OBJECT (KEY_NAME, REGEX, REQUIRED) 
VALUES
('single_obj', '^a |^an |^the |^one |^each +',1),
('between', '^between [] and +',1),
('all_the', '^all the +',1);

INSERT INTO DETAILS (KEY_NAME, REGEX, REQUIRED) 
VALUES
('condition', 'if and only if+',0);