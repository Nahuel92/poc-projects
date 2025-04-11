-- Important: This is not production-ready. Never store password as plain text!
-- I'm doing it here on purpose to focus on the bug I'm facing.
INSERT INTO public.my_users(username, password, first_name, last_name, email)
VALUES ('nahuel92', 'test', 'nahuel', '92', 'nahuel@92.com');