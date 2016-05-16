INSERT INTO language (iso_code, name)
VALUES
  ('en', 'English'),
  ('cs', 'Čeština');

INSERT INTO message (language, code, message)
VALUES
  ('en', 'test.hello', 'Hello!'),
  ('cs', 'test.hello', 'Ahoj!'),
  ('en', 'test.choice', 'There {0,choice,0#are|1#is|2#are} {0} {0,choice,0#apples|1#apple|2#apples} in {1} {1,choice,0#baskets|1#basket|2#baskets}.'),
  ('en', 'test.plural', 'There {0,plural,one{is # apple}other{are # apples}} in {1,plural,one{# basket}other{# baskets}}.');