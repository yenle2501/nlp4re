INSERT INTO CONDITIONS (KEY_NAME, REGEX) 
VALUES
('if', '^if+'),
('while_during', '^while|during+ '),
('after', '^after|before|as soon as+ '),
('incase', '^in case [:alpha:] is included+');


INSERT INTO SYSTEMNAME (KEY_NAME, REGEX) 
VALUES
('all_some', '^all|some systems of the [\\w\\s]+'),
('those', '^those systems of the [\\w\\s]+'),
('the', '^the [\\w\\s]+');

INSERT INTO MODAL (KEY_NAME) 
VALUES 
('SHOULD'),
('SHALL'),
('COULD'),
('WILL'),
('MUST');

INSERT INTO ANCHOR (KEY_NAME, REGEX) 
VALUES
('provide', 'provide [\\w\\s]+ with the ability to [\\w\\s]'),
('be_able_to', 'be able to +');

INSERT INTO OBJECT (KEY_NAME, REGEX) 
VALUES
('single_obj', '^a |^an |^the |^one |^each +'),
('between', '^between [:alpha:] and +'),
('all_the', '^all the +');

INSERT INTO DETAILS (KEY_NAME, REGEX) 
VALUES
('condition', 'if and only if+');